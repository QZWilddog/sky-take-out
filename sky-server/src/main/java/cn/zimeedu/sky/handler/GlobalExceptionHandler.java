package cn.zimeedu.sky.handler;


import cn.zimeedu.sky.exception.BaseException;
import cn.zimeedu.sky.exception.PasswordErrorException;
import cn.zimeedu.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice  // 声明当前类是一个全局异常处理器 @ControllerAdvice + @ResponseBody  处理异常的方法返回值会转换为json后再响应给前端
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
