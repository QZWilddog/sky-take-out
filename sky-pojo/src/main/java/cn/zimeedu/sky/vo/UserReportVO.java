package cn.zimeedu.sky.vo;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Api("用户总量数据模型")
public class UserReportVO implements Serializable {

    private String dateList;
    private String newUserList;
    private String totalUserList;
}
