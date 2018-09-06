package org.cboard.util;

import org.apache.http.HttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class PathTool {

    public static String getRealPath(){
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return  request.getSession().getServletContext().getRealPath("/");
    }
}
