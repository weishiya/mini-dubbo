package org.minidubbo.adapter.spring.processor;

import cn.hutool.core.bean.BeanUtil;
import org.minidubbo.adapter.spring.ReferenceBean;
import org.minidubbo.adapter.spring.annotation.MiniDubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

@Component
public class ReferenceAnnotationProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    private BeanFactory beanFactory;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        this.beanDefinitionRegistry = (BeanDefinitionRegistry)applicationContext.getAutowireCapableBeanFactory();
    }

    //实例化以后，设置属性以前执行
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues propertyValues, PropertyDescriptor[] propertyDescriptors, Object bean, String s) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field:declaredFields){
            MiniDubboReference miniDubboReference = field.getAnnotation(MiniDubboReference.class);
            if(miniDubboReference != null){
                //注册bean的定义信息
                registerBeanDefinitionIfNotExist(field,miniDubboReference);
                Class<?> type = field.getType();
                Object o = beanFactory.getBean(type);
                BeanUtil.setFieldValue(bean,field.getName(),o);
            }
        }
        return propertyValues;
    }


    private void registerBeanDefinitionIfNotExist(Field field,MiniDubboReference miniDubboReference){
        Class<?> type = field.getType();
        String simpleName = type.getSimpleName();
        if(beanDefinitionRegistry.containsBeanDefinition(simpleName)){
            return;
        }
        HashMap<String,Object> map = new HashMap();
        Class<? extends Annotation> aClass = miniDubboReference.annotationType();
        Field[] fields = aClass.getFields();
        String group = miniDubboReference.group();
        int timeout = miniDubboReference.timeout();
        String version = miniDubboReference.version();
        map.put("group",group);
        map.put("timeout",timeout);
        map.put("version",version);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(type);
        GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
        definition.getConstructorArgumentValues().addGenericArgumentValue(type);

        definition.setAttribute("props",map);

        definition.setBeanClass(ReferenceBean.class);
        beanDefinitionRegistry.registerBeanDefinition(simpleName,definition);
    }


}
