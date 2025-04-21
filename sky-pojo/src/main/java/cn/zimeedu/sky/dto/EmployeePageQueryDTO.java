package cn.zimeedu.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("员工分页查询数据模型")
public class EmployeePageQueryDTO implements Serializable {

    @ApiModelProperty("员工姓名")
    private String name;
    @ApiModelProperty("页码")
    private Integer page;
    @ApiModelProperty("每页显示记录数")
    private Integer pageSize;
}
