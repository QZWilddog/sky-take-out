package cn.zimeedu.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 统计订单状态数据模型
public class OrderStatisticsVO implements Serializable {

    private Integer confirmed;

    private Integer deliveryInProgress;

    private Integer toBeConfirmed;
}
