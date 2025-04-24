package cn.zimeedu.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id查询关联套餐id
     * */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
