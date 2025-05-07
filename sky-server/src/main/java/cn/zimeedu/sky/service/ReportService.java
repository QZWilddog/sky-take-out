package cn.zimeedu.sky.service;

import cn.zimeedu.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
