package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.entity.Setmeal;
import cn.zimeedu.sky.enumeration.OperationType;
import cn.zimeedu.sky.vo.SetmealVo;
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
    Page<SetmealVo> page(SetmealPageQueryDTO setmealPageQueryDTO);

    //新增套餐
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);

    // 根据id查询
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    //更改套餐信息
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    // 批量删除套餐
    void delBatch(List<Long> ids);
}
