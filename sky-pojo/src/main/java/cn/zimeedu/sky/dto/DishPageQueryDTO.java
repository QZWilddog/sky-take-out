package cn.zimeedu.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

@ApiModel("分页查询菜品数据模型")
@Data
public class DishPageQueryDTO implements Serializable {

    @ApiModelProperty("分类id")
    private Integer categoryId;

    private String name;

    private Integer page;

    private Integer pageSize;

    private Integer status;
}
