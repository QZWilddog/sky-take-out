package cn.zimeedu.sky.service;


import cn.zimeedu.sky.dto.SetmealDTO;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.vo.SetmealVo;

import java.util.List;

public interface SetmealService {

    // 套餐分页查询
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    // 新增套餐
    void saveWithDish(SetmealDTO setmealDTO);
    // 根据id查询套餐与包含的菜品
    SetmealVo getByIdWithDish(Long id);
    // 修改套餐
    void update(SetmealDTO setmealDTO);
    // 更改套餐状态
    void setStatus(Integer status, Long id);
    // 批量删除套餐
    void delBatch(List<Long> ids);
}
