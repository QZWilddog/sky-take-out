package cn.zimeedu.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {  // 菜品表管理

    /**
     * 根据分类id查询菜品数量
     * */
    @Select("select  count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
