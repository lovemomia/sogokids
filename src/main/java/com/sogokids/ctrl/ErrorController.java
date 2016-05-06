package com.sogokids.ctrl;

import com.sogokids.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController extends BaseController {
    @RequestMapping(value = "/400")
    public Response badRequest() {
        return Response.BAD_REQUEST;
    }

    @RequestMapping(value = "/403")
    public Response forbidden() {
        return Response.FORBIDDEN;
    }

    @RequestMapping(value = "/404")
    public Response notFound() {
        return Response.NOT_FOUND;
    }

    @RequestMapping(value = "/405")
    public Response methodNotAllowed() {
        return Response.METHOD_NOT_ALLOWED;
    }

    @RequestMapping(value = "/500")
    public Response internalServerError() {
        return Response.INTERNAL_SERVER_ERROR;
    }
}
