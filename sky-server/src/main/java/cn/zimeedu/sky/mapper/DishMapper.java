package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.dto.DishPageQueryDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.enumeration.OperationType;
import cn.zimeedu.sky.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {  // 菜品表管理

    /**
     * 根据分类id查询菜品数量
     * */
    @Select("select  count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * */
    @AutoFill(value = OperationType.INSERT)
    void save(Dish dish);

    /**
     * 分页查询菜品
     * */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    // 根据id查询菜品
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 批量删除菜品
     * */
    void delBatch(List<Long> dishIds);

    // 修改菜品
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    // // 动态条件查询菜品
    List<Dish> list(Dish dish);
}
