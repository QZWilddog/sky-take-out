package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.OrderService;
import cn.zimeedu.sky.vo.OrderStatisticsVO;
import cn.zimeedu.sky.vo.OrderVO;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/order")
@Api("管理端订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<Object> conditionSearcht(OrdersPageQueryDTO ordersPageQueryDTO){
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        log.info("当前管理员{}查询所有历史订单{}", userId, ordersPageQueryDTO);

        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    // 根据订单id查询订单详情
    @GetMapping("/details/{id}")
    public Result<OrderVO> getById(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}根据订单id查询历史订单{}", userId, id);

        OrderVO orderVO = orderService.getById(id);

        return Result.success(orderVO);
    }

    // 各个状态的订单数量统计
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> countStatus(){
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}统计当前订单状态", userId);

        OrderStatisticsVO orderStatisticsVO = orderService.countStatus();

        return Result.success(orderStatisticsVO);
    }

    // 商家接单
    @PutMapping("/confirm")
    public Result<Object> confirm(@RequestBody Orders orders) throws Exception {
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}接单", userId);
        orders.setUserId(userId);

        orderService.update(orders);

        return Result.success("操作成功");
    }

    // 店家拒单
    @PutMapping("/rejection")
    public Result<Object> rejection(@RequestBody Orders orders) throws Exception {
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}拒单", userId);
        orders.setUserId(userId);

        orderService.update(orders);

        return Result.success("操作成功");
    }

    // 店家拒单订单
    @PutMapping("/cancel")
    public Result<Object> cancel(@RequestBody Orders orders) throws Exception {
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}拒单", userId);
        orders.setUserId(userId);

        orderService.update(orders);

        return Result.success("操作成功");
    }

    // 店家派送订单
    @PutMapping("/delivery/{id}")
    public Result<Object> delivery(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}拒单", userId);
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(id);

        orderService.delivery(orders);

        return Result.success("操作成功");
    }

    // 店家完成订单
    @PutMapping("/complete/{id}")
    public Result<Object> complete(@PathVariable Long id){
        Long userId = BaseContext.getCurrentId();
        log.info("当前管理员{}拒单", userId);
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(id);

        orderService.complete(orders);

        return Result.success("操作成功");
    }
}
