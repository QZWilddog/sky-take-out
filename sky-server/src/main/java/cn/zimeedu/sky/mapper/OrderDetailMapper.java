package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    // 插入订单明细数据
    void insert(OrderDetail orderDetail);
    // 批量插入
    void insertBatch(List<OrderDetail> orderDetails);
    // 根据订单id 查询订单明细
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
