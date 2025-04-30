package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.entity.Setmeal;
import cn.zimeedu.sky.enumeration.OperationType;
import cn.zimeedu.sky.vo.DishItemVO;
import cn.zimeedu.sky.vo.SetmealVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {  // 套餐表管理

    /**
     * 根据分类id查询套餐的数量
     * */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    // 套餐分页查询
    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    //新增套餐
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);

    // 根据id查询套餐
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    //更改套餐信息
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    // 批量删除套餐
    void delBatch(List<Long> ids);

    // 动态条件查询套餐
    List<Setmeal> list(Setmeal setmeal);

    // 根据套餐id查询包含的菜品
    @Select("select sd.name, sd.copies, dish.image, dish.description " +
            "from setmeal_dish sd left join dish on sd.dish_id = dish.id " +
            "where sd.setmeal_id = #{setmealId} ")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
