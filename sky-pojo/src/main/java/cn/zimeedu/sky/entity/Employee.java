package cn.zimeedu.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// employee表为员工表，用于存储商家内部的员工信息
// 如果一个类需要支持序列化和反序列化操作，它必须实现 Serializable 接口。Serializable 是一个标记接口（没有方法），表示这个类的对象可以被序列化。
// 当一个类实现Serializable接口 表明这个类的对象可以被序列化成字节流 或者由字节流反序列化成还原成对象
public class Employee implements Serializable {

    // Java 中用于序列化和反序列化的标识字段。用于序列化和反序列化的版本控制字段。它的作用是确保序列化和反序列化时的类版本兼容性。
    @Serial // 用来标记序列化相关内容 提高代码可读性
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 配置了MVC消息转换器
    private LocalDateTime createTime;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
