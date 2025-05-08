package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.dto.GoodsSalesDTO;
import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.mapper.OrderMapper;
import cn.zimeedu.sky.mapper.UserMapper;
import cn.zimeedu.sky.service.ReportService;
import cn.zimeedu.sky.vo.OrderReportVO;
import cn.zimeedu.sky.vo.SalesTop10ReportVO;
import cn.zimeedu.sky.vo.TurnoverReportVO;
import cn.zimeedu.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            dates.add(begin);
            // 表示在原始日期基础上增加了指定天数后的日期
            begin = begin.plusDays(1);
        }
        dates.add(end);

        String dateList = StringUtils.join(dates, ",");

        // 根据dates统计每天的营业额并返回一个list 存放每天营业额
        List<Double> turnovers = new ArrayList<>();

        dates.forEach(date -> {
            // 查询date日期对应的营业额数据  订单状态为“已完成”
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  // 获取一天的开始00:00:00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);   // 到一天结束24:59:9999 无限接近后一天

            // 为了复用性
            Map<String, Object> map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            // Double 默认值为空，前端要求为空的话为0 所以进行处理
            turnover = turnover == null ? 0.0 : turnover;
            turnovers.add(turnover);
        });

        String turnoverList = StringUtils.join(turnovers, ",");

        return TurnoverReportVO.builder()
                .dateList(dateList)
                .turnoverList(turnoverList)
                .build();
    }

    @Override
    public UserReportVO getUserStatistice(LocalDate begin, LocalDate end) {

        // 先封装日期列表
        List<LocalDate> localDates = new ArrayList<>();

        while(!begin.equals(end)){

            localDates.add(begin);
            begin = begin.plusDays(1);
        }
        localDates.add(begin);
        String localDateList = StringUtils.join(localDates, ",");

        // 封装用户总数
        List<Integer> users = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        localDates.forEach(date -> {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);

            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);

            // 用户总数
            Integer userCounst = userMapper.getByMap(map);
            userCounst = userCounst == null ? 0 : userCounst;
            users.add(userCounst);

            // 用户新增
            map.put("begin", beginTime);
            Integer newUser = userMapper.getByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUsers.add(newUser);
        });

        String usersList = StringUtils.join(users, ",");
        String newUsersList = StringUtils.join(newUsers, ",");

        return UserReportVO.builder()
                .dateList(localDateList)
                .totalUserList(usersList)
                .newUserList(newUsersList)
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

        // 先封装日期列表
        List<LocalDate> dates = new ArrayList<>();

        while(!begin.equals(end)){

            dates.add(begin);
            begin = begin.plusDays(1);
        }
        dates.add(begin);

        List<Integer> orderCounts = new ArrayList<>();
        List<Integer> validOrderCounts = new ArrayList<>();
        // 遍历查询没有有效顶顶那数和订单总数
        dates.forEach(date -> {
            // 因为类型问题，所以与数据库datetime一样
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);

            Map<String, Object> map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            // 查询每天的订单总数
            Integer orderCount = orderMapper.countByMap(map);
            orderCounts.add(orderCount);
            // 查询每天有效订单数
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);
            validOrderCounts.add(validOrderCount);
        });

        String dateList = StringUtils.join(dates, ",");
        String orderCountList = StringUtils.join(orderCounts, ",");
        String validOrderCountList = StringUtils.join(validOrderCounts, ",");
        // 计算时间区间内的订单总数量
        Integer totalOrderCount = orderCounts.stream().reduce(Integer::sum).get();
        // 计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCounts.stream().reduce(Integer::sum).get();
        // 计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(dateList)
                .orderCountList(orderCountList)
                .validOrderCountList(validOrderCountList)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop = orderMapper.getSalesTop(beginTime, endTime);
        // 菜品名称封装
        List<String> names = salesTop.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        //  数量封装
        List<Integer> numbers = salesTop.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
