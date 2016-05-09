package com.sogokids.service.course;

public class SubjectSku {
    public static final SubjectSku NOT_EXIST_SUBJECT_SKU = new SubjectSku();

    private int id;
    private int subjectId;
    private int courseCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public boolean exist() {
        return id > 0;
    }
}
