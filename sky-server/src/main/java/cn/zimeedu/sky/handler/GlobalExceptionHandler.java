//package cn.zimeedu.sky.handler;
//
//
//import cn.zimeedu.sky.result.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.BaseExecutor;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@Slf4j
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler
//    public Result exceptionHandler(BaseExecutor ex){
//        log.error("异常信息：{}", ex.getMessage());
//
//        return Result.error(ex.getMessage());
//    }
//}
