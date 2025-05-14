package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.dto.GoodsSalesDTO;
import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.entity.Orders;
import cn.zimeedu.sky.mapper.OrderMapper;
import cn.zimeedu.sky.mapper.UserMapper;
import cn.zimeedu.sky.service.ReportService;
import cn.zimeedu.sky.service.WorkspaceService;
import cn.zimeedu.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    @Override
    public void ExportBusinessData(HttpServletResponse response) {
        // 查询数据库 获取营业数据  获取最近30天的数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        // 查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));


        // 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            // 获取表格文件的sheet页
            XSSFSheet sheet = excel.getSheetAt(0);
            // 填充第二行第二个单元格数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
            // 填充第4行与第5行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            // 填充明细数据 30天每天的数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                // 查询某一天数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                // 填充数据
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 通过输出流将Excle文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);


            // 关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
