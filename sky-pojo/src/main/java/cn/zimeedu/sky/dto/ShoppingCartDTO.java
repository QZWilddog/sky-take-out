package cn.zimeedu.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("购物车数据模型")
public class ShoppingCartDTO implements Serializable {

    @ApiModelProperty("菜品id")
    private Long dishId;
    private Long setmealId;
    private String dishFlavor;
}
