package com.jieming.ad.index;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Qinyi.
 *
 * 索引服务类缓存，主要是保存所有的索引
 */
@Component
public class DataTable implements ApplicationContextAware, PriorityOrdered {

    private static ApplicationContext applicationContext;

    public static final Map<Class, Object> dataTableMap =
            new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        DataTable.applicationContext = applicationContext;
    }


    //在所有的索引服务被加载之前，就需要将此缓存先加载出来，所以设置最高优先级的加载
    @Override
    public int getOrder() {
        //数值越小，优先级越高
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @SuppressWarnings("all")
    public static <T> T of(Class<T> clazz) {

        // 用到了反射，根据传递进来的class对象，通过反射去IOC容器中
        // 查找对应的bean，就不用每次都需要在每个类中注入，
        // 起到了缓存的作用
        T instance = (T) dataTableMap.get(clazz);
        if (null != instance) {
            return instance;
        }

        dataTableMap.put(clazz, bean(clazz));
        return (T) dataTableMap.get(clazz);
    }

    @SuppressWarnings("all")
    private static <T> T bean(String beanName) {
        // 通过beanName获取对应的bean
        return (T) applicationContext.getBean(beanName);
    }

    @SuppressWarnings("all")
    private static <T> T bean(Class clazz) {
        // 通过class对象获取对应的bean
        return (T) applicationContext.getBean(clazz);
    }
}
