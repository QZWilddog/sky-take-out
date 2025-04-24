package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    // 新增口味
    void saveBatch(List<DishFlavor> flavors);
    // 删除口味
    void delBatch(List<Long> dishIds);
    // 根据菜品id查询口味数据
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    @Delete("delete from dish_flavor where dish_id = #{id}")
    void delByDishId(Long id);

}
