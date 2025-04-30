package cn.zimeedu.sky.interceptor;

import cn.zimeedu.sky.constant.JwtClaimsConstant;
import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.properties.JwtProperties;
import cn.zimeedu.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {  // 是 Spring 框架中用于实现请求拦截功能的一个核心接口

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    //目标资源方法执行前执行。 返回true：放行    返回false：不放行    preHandle 方法中被使用。handler 是 Object 类型，它通常代表即将执行的处理程序对象
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是不是HandlerMethod（映射到控制器方法的处理器）的类
        if(!(handler instanceof HandlerMethod)){
            // 不是控制器的方法类型 直接放行
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        if (token == null || token.isEmpty()){
            log.info("令牌为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 返回401
            return false;
        }

        // 校验令牌
        try {
            log.info("jwt令牌校验{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            // 获取令牌中的当前用户id 并存储在当前线程存储区域
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(userId);
            log.info("令牌校验通过 当前用户id:{}", userId);
            return true;
        } catch (Exception e) {
            log.info("jwt非法{}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 返回401
            return false;
        }

    }

    //目标资源方法执行后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 目标资源执行完后 移除当前线程的 ThreadLocal 中存储的员工 ID  建议在使用完 ThreadLocal 后调用此方法，以避免内存泄漏。
        BaseContext.removeCurrentId();
    }
}
