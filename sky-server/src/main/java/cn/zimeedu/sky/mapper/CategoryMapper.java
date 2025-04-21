package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.annotation.AutoFill;
import cn.zimeedu.sky.dto.CategoryPageQueryDTO;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {  // 分类表管理
    /**
     * 分页查询菜品和分类
     * */
    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);
    /**
     * 修改菜品和分类全部操作
     * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);
    /**
     * 新增菜品
     * */
    @AutoFill(value = OperationType.INSERT)
    void save(Category category);
    /**
     * 删除菜品或分类
     * */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
