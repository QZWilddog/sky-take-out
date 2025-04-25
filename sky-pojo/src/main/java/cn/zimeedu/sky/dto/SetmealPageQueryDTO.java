package cn.zimeedu.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("菜品分页查询数据模型")
public class SetmealPageQueryDTO implements Serializable {

    @ApiModelProperty("菜品id")
    private Long categoryId;

    private String name;

    private Integer page;

    private Integer pageSize;

    private Integer status;
}
