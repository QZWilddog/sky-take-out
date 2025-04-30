package cn.zimeedu.sky.vo;

import cn.zimeedu.sky.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("套操分页展示数据模型")
public class SetmealVO implements Serializable {

    @ApiModelProperty("套餐ID")
    private Long id;

    //分类id
    private Long categoryId;

    //套餐名称
    private String name;

    //套餐价格
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //描述信息
    private String description;

    //图片
    private String image;

    private LocalDateTime updateTime;

    private String categoryName;

    //套餐和菜品的关联关系
    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
