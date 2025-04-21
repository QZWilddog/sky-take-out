package cn.zimeedu.sky.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
    // 泛型类的局限：静态方法和静态属性访问不了类上定义的泛型参数
    // 在指定泛型类参数时 是准备实例化该泛型类 所以静态访问不了实例
    // 解决：就是定义泛型方法  让静态方法的泛型参数 由静态方法本身在被调用时定义泛型参数类型 而编译器可以自行推导参数类型 不用显示指定
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    // <T> 声明这是一个泛型方法：泛型方法与泛型类没有必然联系  也可以定义在普通类中
    //  调用泛型方法不用显示指定具体类型  编译器可以推导出参数类型 T  这里的 T 是指整个方法的 T 的类型 所以说泛型方法不依赖泛型类
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
