package cn.zimeedu.sky.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("菜品分离分页查询数据模型")
public class CategoryPageQueryDTO implements Serializable {

    private String name;

    private Integer page;

    private Integer pageSize;

    private Integer type;
}
