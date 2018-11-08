package net.omisoft.rest.resolver;

import com.google.common.net.HttpHeaders;
import net.omisoft.rest.configuration.annotation.RemoteIp;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class RemoteIpArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(RemoteIp.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String ip = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
        if (ip == null || ip.trim().length() == 0 || !InetAddressValidator.getInstance().isValid(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}