package cn.zimeedu.sky.annotation;


import cn.zimeedu.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  与aop配合 自定义注解，用于标识某些方法需要进行功能字段自动填充
 * */
@Target(ElementType.METHOD) // 修饰注解的注解 叫元注解  定义注解生效的范围 在方法上生效
@Retention(RetentionPolicy.RUNTIME)  // 定义注解什么时候生效
public @interface AutoFill {
    // 指定数据库操作类型
    OperationType value();
}
