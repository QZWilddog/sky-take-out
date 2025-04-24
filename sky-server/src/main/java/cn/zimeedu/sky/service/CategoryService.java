package cn.zimeedu.sky.service;

import cn.zimeedu.sky.dto.CategoryDTO;
import cn.zimeedu.sky.dto.CategoryPageQueryDTO;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    // 菜品分页查询
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);
    // 菜品状态设置
    void setStatus(Integer status, Long id);
    // 更改菜品信息
    void update(CategoryDTO categoryDTO);
    // 新增菜品
    void save(CategoryDTO categoryDTO);
    // 删除菜品或分类
    void deleteById(Long id);
    // 分页查询菜品和分类
    List<Category> list(Integer type);

}
