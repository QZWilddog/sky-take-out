package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.ReportService;
import cn.zimeedu.sky.vo.OrderReportVO;
import cn.zimeedu.sky.vo.SalesTop10ReportVO;
import cn.zimeedu.sky.vo.TurnoverReportVO;
import cn.zimeedu.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api("数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate  begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){  // 日期有各种格式 需要用注解格式化日期格式 否则不能正确格式封装
        log.info("统计营业额：{}-{}", begin, end);

        TurnoverReportVO turnoverReportVO =  reportService.getTurnoverStatistics(begin, end);

        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户总量与每日新增用户总量")
    public Result<UserReportVO> userStatistice(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("统计用户总量与每日新增用户总量：{}-{}", begin, end);

        UserReportVO userReportVO = reportService.getUserStatistice(begin, end);

        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计：{}-{}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrdersStatistics(begin, end);

        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> salesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询销量排名top10接口：{}-{}", begin, end);

        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    @GetMapping("/export")
    @ApiOperation("导出Excel表格数据 没有返回值 导出报表功能本质是文件下载 通过输出流将Excel文件下载到客户端浏览器")
    public void getExport(HttpServletResponse response){
        log.info("导出表格报表数据");
        reportService.ExportBusinessData(response);
    }
}
