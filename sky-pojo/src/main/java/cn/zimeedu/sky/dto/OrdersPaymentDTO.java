package cn.zimeedu.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersPaymentDTO implements Serializable {

    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;
}
