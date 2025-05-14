package cn.zimeedu.sky.service;

import cn.zimeedu.sky.vo.OrderReportVO;
import cn.zimeedu.sky.vo.SalesTop10ReportVO;
import cn.zimeedu.sky.vo.TurnoverReportVO;
import cn.zimeedu.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

public interface ReportService {
    // 统计每日营业额
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
    // 统计用户数量与每日新增用户
    UserReportVO getUserStatistice(LocalDate begin, LocalDate end);
    // 统计订单数量
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);
    // 查询销量排名top10接口
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
    // 导出运营数据报表
    void ExportBusinessData(HttpServletResponse response);
}
