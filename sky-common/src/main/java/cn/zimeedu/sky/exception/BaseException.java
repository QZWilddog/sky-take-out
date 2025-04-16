package cn.zimeedu.sky.exception;

/**
 * 自定义运行时异常（还有一个叫自定义编译时异常）
 * BaseException 业务异常类，所有业务异常类需要继承这个类（因为我不能直接去该RuntiomeException的代码 所以我要继承它 然后用他的子类来管理）
 * RuntimeException 它是所有运行时异常的基类，表示在程序运行期间可能会发生的错误或异常情况。
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
