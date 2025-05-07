package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.mapper.OrderMapper;
import cn.zimeedu.sky.service.ReportService;
import cn.zimeedu.sky.vo.TurnoverReportVO;
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

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

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
}
