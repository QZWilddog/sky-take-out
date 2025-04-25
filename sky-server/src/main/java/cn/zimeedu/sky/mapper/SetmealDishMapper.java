package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品id查询关联套餐id
     * */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    void saveBatch(List<SetmealDish> setmealDishes);

    // 根据套餐id查询里面包含的菜品
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void dleBatchBySetmealId(Long setmealId);

    // 删除套餐关联的菜品
    void delBatch(List<Long> ids);

}
