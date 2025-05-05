package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.dto.OrdersPaymentDTO;
import cn.zimeedu.sky.dto.OrdersSubmitDTO;
import cn.zimeedu.sky.entity.*;
import cn.zimeedu.sky.exception.AddressBookBusinessException;
import cn.zimeedu.sky.exception.OrderBusinessException;
import cn.zimeedu.sky.exception.ShoppingCartBusinessException;
import cn.zimeedu.sky.mapper.*;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.OrderService;
import cn.zimeedu.sky.utils.WeChatPayUtil;
import cn.zimeedu.sky.vo.OrderPaymentVO;
import cn.zimeedu.sky.vo.OrderStatisticsVO;
import cn.zimeedu.sky.vo.OrderSubmitVO;
import cn.zimeedu.sky.vo.OrderVO;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    @Transactional
    public OrderSubmitVO submitOrders(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理各种业务异常（地址，购物车等）， 我觉得对于这种最重要的模块之一，确实要前后端都要做校验 更何况是关于钱的模块
        // 特别是防黑客或大学生工具，比如用除了小程序以外的工具发生请求 其他不涉及钱的还好 这种涉及钱的就会很有问题
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null || addressBook.getDetail().isEmpty()){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);

        if (shoppingCarts == null || shoppingCarts.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表插入一条数据  订单号用时间戳（纯数字 不重复）
        Orders orders = Orders.builder()
                        .number(String.valueOf(System.currentTimeMillis()))
                        .status(Orders.PENDING_PAYMENT)
                        .userId(userId)
                        .orderTime(LocalDateTime.now())
                        .payStatus(Orders.UN_PAID)
                        .phone(addressBook.getPhone())
                        .consignee(addressBook.getConsignee())
                        .address(addressBook.getDetail())
                        .build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        orderMapper.insert(orders);

        // 向订单明细表插入一条或多条数据 并计算订单的金额  批量插入效率更高
        List<OrderDetail> orderDetails = new ArrayList<>();
        shoppingCarts.forEach(shoppingCartTemp -> {
            OrderDetail orderDetail = OrderDetail.builder()
                                                .orderId(orders.getId())
                                                .build();
            BeanUtils.copyProperties(shoppingCartTemp, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetail.setId(null);
            orderDetails.add(orderDetail);
        });

        orderDetailMapper.insertBatch(orderDetails);

        // 如果下单成功 清空用户购物车数据
        shoppingCartMapper.deleteByUserId(shoppingCart);

        // 封装返回VO数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderNumber(orders.getNumber())
                .orderTime(LocalDateTime.now())
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);


        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal("0.01"), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

        JSONObject jsonObject = new JSONObject();

/*        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        OrderPaymentVO orderPaymentVO = jsonObject.toJavaObject(OrderPaymentVO.class);
        orderPaymentVO.setPackageStr(jsonObject.getString("package"));

        // 微信小程序支付功能需要企业资质，所以完成不了，这里直接调用微信服务器推送的支付结果 来更改数据库数据
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return orderPaymentVO;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * 退款成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void refund(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.REFUND)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult list(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> ordersPages = orderMapper.list(ordersPageQueryDTO);

        List<OrderVO> orderVOS = new ArrayList<>();

        if (ordersPages != null && !ordersPages.isEmpty()){

            ordersPages.forEach(orders -> {
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
                if (orderDetails != null && !orderDetails.isEmpty()){

                    OrderVO orderVO = new OrderVO();
                    orderVO.setOrderDetailList(orderDetails);
                    BeanUtils.copyProperties(orders, orderVO);

                    orderVOS.add(orderVO);
                }
            });
        }

        return new PageResult(ordersPages.getTotal(), orderVOS);
    }

    @Override
    public OrderVO getById(Long id) {

         Orders orders = orderMapper.getById(id);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);

        return orderVO;
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);

        return new PageResult(orders.getTotal(), orders);
    }

    @Override
    public void cancelOrders(Long id) {
        Orders orders = orderMapper.getById(id);

        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        if ( orders.getStatus().equals(4) && orders.getStatus().equals(5) && orders.getStatus().equals(6)  && orders.equals(3)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(6);
        orderMapper.update(orders);
    }

    @Override
    public void saveGetById(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        // 将订单详情转换为购物车对象
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品重新复制到购物车对象中
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");  // 排除了指定的 "id" 属性不进行复制
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.saveBatch(shoppingCarts);

    }

    @Override
    public OrderStatisticsVO countStatus() {

        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    @Override
    public void update(Orders orders) throws Exception {
        Orders ordersDB = orderMapper.getById(orders.getId());

        // 根据是否有拒单理由判断是接单还是拒单
        if (orders.getRejectionReason() != null && !orders.getRejectionReason().isEmpty()){
            // 拒绝订单逻辑 只有代接单可以拒单
            if (!ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
                throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
            }
            //支付状态 已经付款需要退款  没有这个
//            Integer payStatus = orders.getPayStatus();
//            if (payStatus == Orders.PAID) {
//                //用户已支付，需要退款
//                String refund = weChatPayUtil.refund(
//                        orders.getNumber(),
//                        orders.getNumber(),
//                        new BigDecimal(0.01),
//                        new BigDecimal(0.01));
//                log.info("申请退款：{}", refund);
//            }
            log.info("已退款");
            refund(ordersDB.getNumber());

            orders.setStatus(6);
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.update(orders);

        } else if (orders.getCancelReason() != null && !orders.getCancelReason().isEmpty()) {
            // 取消订单逻辑

            //支付状态 已经付款需要退款  没有这个
//            Integer payStatus = orders.getPayStatus();
//            if (payStatus == Orders.PAID) {
//                //用户已支付，需要退款
//                String refund = weChatPayUtil.refund(
//                        orders.getNumber(),
//                        orders.getNumber(),
//                        new BigDecimal(0.01),
//                        new BigDecimal(0.01));
//                log.info("申请退款：{}", refund);
//            }
            log.info("已退款");
            refund(ordersDB.getNumber());

            orders.setStatus(6);
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.update(orders);
        } else {

            orders.setStatus(3);
            orderMapper.update(orders);
        }
    }

    @Override
    // 店家派送订单
    public void delivery(Orders orders) {
        orders.setStatus(4);

        orderMapper.update(orders);
    }

    @Override
    // 店家派送完成
    public void complete(Orders orders) {
        orders.setStatus(5);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


}
