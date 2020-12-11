package xyz.scootaloo.bootshiro.controller;

import xyz.scootaloo.bootshiro.utils.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 做为所有Controller的父类，
 * 提供一些Controller中经常使用的方法
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 20:32
 */
public abstract class BaseHttpServ {

    protected final Map<String, String> getRequestParameter(HttpServletRequest request) {
        return HttpUtils.getRequestParameters(request);
    }

    protected final Map<String, String> getRequestBody(HttpServletRequest request) {
        return HttpUtils.getRequestBodyMap(request);
    }

    protected final Map<String, String> getRequestHeader(HttpServletRequest request) {
        return HttpUtils.getRequestHeader(request);
    }

}
