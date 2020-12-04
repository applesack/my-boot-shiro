package xyz.scootaloo.bootshiro.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 20:32
 */
public abstract class BaseHttpServ {

    protected final Map<String, String> getRequestParameter(HttpServletRequest request) {
        return null;
    }

    protected final Map<String, String> getRequestBody(HttpServletRequest request) {
        return null;
    }

    protected final Map<String, String> getRequestHeader(HttpServletRequest request) {
        return null;
    }

}
