package cn.zimeedu.sky.aspect;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.constant.AutoFillConstant;
import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect // 声明当前类是一个切面类
@Component // 声明当前类是spring中一个bean
@Slf4j
public class AutoFillAspect {

    /**
     * 自定义切入点
     * */
    @Pointcut("execution(* cn.zimeedu.sky.mapper.*.*(..)) && @annotation(cn.zimeedu.sky.annotation.AutoFill)")  // 定义一个切入点表达式
    public void autoFillPointCut(){
    }

    /**
     * 自定义前置通知，在通知中进行公共字段的赋值
     * 通知用于拦截具体的连接点也就是方法
     * */
    @Before("autoFillPointCut()") //
    public void autoFill(JoinPoint joinPoint){  // JoinPoint：可选参数，提供目标方法的上下文信息。
        log.info("进行公共字段的自动填充...");

        // 1.获取到当前被拦截的方法上的数据库操作类型
        // 获取被拦截方法的签名信息（包括方法名、返回类型等）   表示连接点（Join Point）签名信息的一个接口
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();  // 但是方法不够丰富  所以spirng进行了多次扩展 需要强转MethodSignature接口

        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);  // 获得方法上的注解对象
        OperationType value = autoFill.value();  // 获得数据库操作类型

        // 获取到当前被拦截的方法的参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        Object entity = args[0];
        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (value == OperationType.INSERT){
            try {
                // 通过反射获得对象set方法
                Method setCreateTime =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value == OperationType.UPDATE) {
            try {
                // 通过反射获得对象set方法
                Method setUpdateTime =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser =  entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
