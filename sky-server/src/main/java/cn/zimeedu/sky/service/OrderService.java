package cn.zimeedu.sky.service;

import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.dto.OrdersPaymentDTO;
import cn.zimeedu.sky.dto.OrdersSubmitDTO;
import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.vo.OrderPaymentVO;
import cn.zimeedu.sky.vo.OrderStatisticsVO;
import cn.zimeedu.sky.vo.OrderSubmitVO;
import cn.zimeedu.sky.vo.OrderVO;

public interface OrderService {

    // 保存提交的订单
    OrderSubmitVO submitOrders(OrdersSubmitDTO ordersSubmitDTO);
    // 支付订单
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    // 用户成功支付后，接收到微信服务器返回成功请求后，更改商户服务器数据订单状态
    void paySuccess(String outTradeNo);
    // 用户查询历史订单
    PageResult list(OrdersPageQueryDTO ordersPageQueryDTO);
    // 根据订单id查询订单详情
    OrderVO getById(Long id);
    //管理端查询所有订单
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    // 用户取消订单
    void cancelOrders(Long id);
    // 用户再来一单
    void saveGetById(Long id);
    // 当前管理员统计当前订单状态
    OrderStatisticsVO countStatus();
    // 修改订单
    void update(Orders orders) throws Exception;
    // 店家派送订单
    void delivery(Orders orders);
    // 店家派送完成
    void complete(Orders orders);
    // 客户催单
    void reminder(Long id);
}
