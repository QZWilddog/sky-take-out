package cn.zimeedu.sky.service;


import cn.zimeedu.sky.dto.DishDTO;
import cn.zimeedu.sky.dto.DishPageQueryDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应口味
     * */
    void saveAndFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * */

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    // 删除菜品
    void delBatch(List<Long> ids);
    // 根据id查询菜品
    DishVO getByIdWithFlavor(Long id);
    // 修改菜品
    void updateWithFlavor(DishDTO dishDTO);
    // 启售或禁用菜品
    void setStatus(Integer status, Long id);
    // 根据菜品分类查询 菜品
    List<Dish> list(Long categoryId);
    // 批量查询菜品及口味
    List<DishVO> listWithFlavor(Dish dish);
}
