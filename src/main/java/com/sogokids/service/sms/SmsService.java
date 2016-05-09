package com.sogokids.service.sms;

import com.google.common.collect.Sets;
import com.sogokids.common.config.Configuration;
import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.service.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SmsService extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    private ExecutorService sendCodeExecutorService;

    private Object signal = new Object();
    private ExecutorService notifyExecutorService = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
    private Queue<NotifyTask> tasksQueue = new LinkedList<NotifyTask>();

    private SmsSenderFactory smsSenderFactory;

    public void setSmsSenderFactory(SmsSenderFactory smsSenderFactory) {
        this.smsSenderFactory = smsSenderFactory;
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        consume();
                    } catch (InterruptedException e) {
                        LOGGER.error("InterruptedException", e);
                    }
                }
            }
        }).start();
    }

    private void consume() throws InterruptedException {
        synchronized (signal) {
            if (tasksQueue.isEmpty()) {
                signal.wait();
            }

            int count = 0;
            while (!tasksQueue.isEmpty() && count++ < 1000) {
                NotifyTask task = tasksQueue.poll();
                if (task == null) continue;
                notifyExecutorService.submit(task);
            }
        }
    }

    public boolean sendCode(String mobile) {
        checkFrequency(mobile);

        String code = getOrGenerateCode(mobile);
        sendCodeAsync(mobile, buildCodeMsg(code));

        return true;
    }

    private void checkFrequency(String mobile) {
        Date lastSendTime = getLastSendTime(mobile);
        if (lastSendTime != null && new Date().getTime() - lastSendTime.getTime() < 60 * 1000)
            throw new SogoErrorException("发送频率过快，请稍后再试");
    }

    private Date getLastSendTime(String mobile) {
        String sql = "SELECT SendTime FROM SG_SmsCode WHERE Mobile=?";
        return queryDate(sql, new Object[] { mobile });
    }

    private String getOrGenerateCode(String mobile) {
        String code = getGeneratedCode(mobile);
        if (StringUtils.isBlank(code)) {
            code = generateCode(mobile);
            updateCode(mobile, code);
        }

        return code;
    }

    private String getGeneratedCode(String mobile) {
        String sql = "SELECT Code FROM SG_SmsCode WHERE Mobile=? AND GenerateTime>? AND Status<>0";
        return queryString(sql, new Object[] { mobile, new Date(new Date().getTime() - 30 * 60 * 1000) });
    }

    private String generateCode(String mobile) {
        int number = (int) (Math.random() * 1000000);
        return String.format("%06d", number);
    }

    private void updateCode(String mobile, String code) {
        if (exists(mobile)) {
            String sql = "UPDATE SG_SmsCode SET Mobile=?, Code=?, GenerateTime=NOW(), SendTime=NULL, Status=1 WHERE Mobile=?";
            update(sql, new Object[] { mobile, code, mobile });
        } else {
            String sql = "INSERT INTO SG_SmsCode(Mobile, Code, GenerateTime) VALUES (?, ?, NOW())";
            update(sql, new Object[] { mobile, code });
        }
    }

    private boolean exists(String mobile) {
        String sql = "SELECT COUNT(1) FROM SG_SmsCode WHERE Mobile=?";
        return queryInt(sql, new Object[] { mobile }) > 0;
    }

    private String buildCodeMsg(String code) {
        return "验证码：" + code + "，30分钟内有效";
    }

    private void sendCodeAsync(final String mobile, final String codeMsg) {
        if (sendCodeExecutorService == null) initExecutorService();

        sendCodeExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                SmsSender sender = smsSenderFactory.getSmsSender(Configuration.getString("Sms.Enabled"));
                if (sender.send(mobile, codeMsg)) {
                    updateSendTime(mobile);
                }
            }
        });
    }

    private synchronized void initExecutorService() {
        if (sendCodeExecutorService != null) return;

        int corePoolSize = Configuration.getInt("Sms.CorePoolSize");
        int maxPoolSize = Configuration.getInt("Sms.MaxPoolSize");
        int queueSize = Configuration.getInt("Sms.QueueSize");
        sendCodeExecutorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    private boolean updateSendTime(String mobile) {
        String sql = "UPDATE SG_SmsCode SET SendTime=NOW() WHERE Mobile=?";
        return update(sql, new Object[] { mobile });
    }

    public boolean verifyCode(String mobile, String code) {
        String sql = "SELECT COUNT(1) FROM SG_SmsCode WHERE Mobile=? AND Code=? AND GenerateTime>? AND Status<>0";
        boolean successful = queryInt(sql, new Object[] { mobile, code, new Date(new Date().getTime() - 30 * 60 * 1000) }) > 0;

        if (successful) disable(mobile, code);

        return successful;
    }

    private void disable(String mobile, String code) {
        String sql = "UPDATE SG_SmsCode SET Status=0 WHERE Mobile=? AND Code=?";
        update(sql, new Object[] { mobile, code });
    }

    public boolean notify(String mobile, String message) {
        return notify(Sets.newHashSet(mobile), message);
    }

    public boolean notify(Collection<String> mobiles, String message) {
        synchronized (signal) {
            for (String mobile : mobiles) {
                tasksQueue.add(new NotifyTask(mobile, message));
            }
            signal.notify();
        }

        return true;
    }

    private class NotifyTask implements Runnable {
        private String mobile;
        private String message;

        public NotifyTask(String mobile, String message) {
            this.mobile = mobile;
            this.message = message;
        }

        @Override
        public void run() {
            SmsSender sender = smsSenderFactory.getSmsSender(Configuration.getString("Sms.Enabled"));
            sender.send(mobile, message);
        }
    }
}
