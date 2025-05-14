package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.vo.BusinessDataVO;
import cn.zimeedu.sky.vo.DishOverViewVO;
import cn.zimeedu.sky.vo.OrderOverViewVO;
import cn.zimeedu.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.zimeedu.sky.service.WorkspaceService;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
public class WorkspaceControoler {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        log.info("查询今日数据");

        // 获得当天时间的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        // 获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);

        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("查询订单管理数据");

        OrderOverViewVO orderOverViewVO = workspaceService.getOrderOverView();

        return Result.success(orderOverViewVO);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes(){
        log.info("菜品总览数据");

        DishOverViewVO dishOverViewVO = workspaceService.getDishOverView();

        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("套餐总览数据");

        SetmealOverViewVO setmealOverViewVO = workspaceService.getSetmealOverView();

        return Result.success(setmealOverViewVO);
    }
}
