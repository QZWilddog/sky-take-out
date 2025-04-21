package cn.zimeedu.sky.exception;

public class DeletionNotAllowedException extends BaseException {
    /**
     * 删除不允许异常
     * */
    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
