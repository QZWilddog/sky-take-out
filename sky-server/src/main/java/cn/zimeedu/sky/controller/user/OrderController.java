package cn.zimeedu.sky.controller.user;

import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.dto.OrdersPaymentDTO;
import cn.zimeedu.sky.dto.OrdersSubmitDTO;
import cn.zimeedu.sky.mapper.OrderMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.OrderService;
import cn.zimeedu.sky.vo.OrderPaymentVO;
import cn.zimeedu.sky.vo.OrderSubmitVO;
import cn.zimeedu.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("user/order")
@Slf4j
@Api("订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("提交订单，新增订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        Long userId = BaseContext.getCurrentId();
        log.info("{}用户提交订单：{}", userId, ordersSubmitDTO);

        OrderSubmitVO orderSubmitVO = orderService.submitOrders(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    // 用户端查询所有历史订单信息
    @GetMapping("/historyOrders")
    public Result<PageResult> list(OrdersPageQueryDTO ordersPageQueryDTO){
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}查询所有订单信息：{}",userId ,ordersPageQueryDTO);
        ordersPageQueryDTO.setUserId(userId);

        PageResult pageResult = orderService.list(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    // 根据订单id查询订单详情
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getById(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}查询订单信息：{}",userId , id);

        OrderVO orderVO = orderService.getById(id);

        return Result.success(orderVO);
    }

    // 用户取消订单
    @PutMapping("cancel/{id}")
    public Result<Object> cancelOrders(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}取消订单：{}",userId , id);

        orderService.cancelOrders(id);

        return Result.success("操作成功");
    }

    // 再来一单
    @PostMapping("/repetition/{id}")
    public Result<Object> saveGetById(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("用户{}添加订单：{}",userId , id);

        orderService.saveGetById(id);

        return Result.success("操作成功");
    }
}
