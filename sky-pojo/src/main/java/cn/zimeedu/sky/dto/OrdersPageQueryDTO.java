package cn.zimeedu.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("接收用户端查看历史订单数据模型")
public class OrdersPageQueryDTO implements Serializable {

    private Long userId;

    private Integer page;

    private  Integer pageSize;

    //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String phone;

    private String number;
}
