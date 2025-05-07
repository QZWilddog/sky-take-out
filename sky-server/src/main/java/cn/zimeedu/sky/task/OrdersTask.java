package cn.zimeedu.sky.task;

import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
// 定时器
public class OrdersTask {

    @Autowired
    private OrderMapper orderMapper;

    // 处理超时订单的方法
    @Scheduled(cron = "0 * * * * ?")  // 每分钟触发一次 每分钟的第0秒触发
    public void processTimeoutOrder(){
//        log.info("定时处理超时订单：{}", LocalDateTime.now());

        // 处理超过15分钟未付款的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if (orders != null){
            orders.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }

    // 处理一直处于派送中的订单
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点触发
    public void processDeliveryOrder(){
        log.info("处理一直处于派送中订单{}", LocalDateTime.now());

        // 处理每前一天一直派送中的订单 已经新一天了 所有这里时间是对的
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (orders != null){
            orders.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }
}
