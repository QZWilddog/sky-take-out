package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.ReportService;
import cn.zimeedu.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
