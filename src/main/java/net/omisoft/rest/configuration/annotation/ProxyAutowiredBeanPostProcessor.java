package net.omisoft.rest.configuration.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProxyAutowiredBeanPostProcessor implements BeanPostProcessor, Ordered {

    private Map<String, Object> map = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field x : fields) {
            if (x.isAnnotationPresent(ProxyAutowired.class)) {
                map.put(beanName, bean);
                break;
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object obj = map.get(beanName);
        if (obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field x : fields) {
                if (x.isAnnotationPresent(ProxyAutowired.class)) {
                    x.setAccessible(true);
                    ReflectionUtils.setField(x, obj, bean);
                }
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
