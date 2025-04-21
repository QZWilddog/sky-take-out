package cn.zimeedu.sky.context;

public class BaseContext {

    /**  为每个线程提供一份单独的存储空间，具有线程隔离的效果，不同线程之间不会互相干扰
     * 静态常量 ThreadLocal，用于存储当前线程的员工 ID。
     * 每个 ThreadLocal 对象只能存储一个值，并且这个值是线程隔离的。你可以将 ThreadLocal 理解为一个泛型变量，但它为每个线程维护了一个独立的副本。
     */
    private static final ThreadLocal<Long> ThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的员工 ID。
     * @param id
     */
    public static void setCurrentId(Long id) {
        ThreadLocal.set(id);
    }

    /**
     * 获取当前线程的员工 ID。
     *
     * @return 当前线程的员工 ID，如果未设置则返回 null。
     */
    public static Long getCurrentId() {
        return ThreadLocal.get();
    }

    /**
     * 移除当前线程中存储的员工 ID。
     * 建议在使用完 ThreadLocal 后调用此方法，以避免内存泄漏。
     */
    public static void removeCurrentId() {
        ThreadLocal.remove();
    }
}
