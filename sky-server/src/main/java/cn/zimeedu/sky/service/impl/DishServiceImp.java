package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.constant.StatusConstant;
import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.DishDTO;
import cn.zimeedu.sky.dto.DishPageQueryDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.entity.DishFlavor;
import cn.zimeedu.sky.exception.DeletionNotAllowedException;
import cn.zimeedu.sky.mapper.DishFlavorMapper;
import cn.zimeedu.sky.mapper.DishMapper;
import cn.zimeedu.sky.mapper.SetmealDishMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.DishService;
import cn.zimeedu.sky.vo.DishVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishServiceImp implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveAndFlavor(DishDTO dishDTO){

        // 向菜品表插入1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.save(dish);
        // 获取inset 语句生成的主键值
        Long dishId = dish.getId();

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && ! flavors.isEmpty()){
            flavors.forEach(dishflavor -> {dishflavor.setDishId(dishId);});
            // 向口味批量插入数据
            dishFlavorMapper.saveBatch(flavors);
        }

    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVo> dishPage = dishMapper.page(dishPageQueryDTO);

        return new PageResult(dishPage.getTotal(), dishPage.getResult());
    }

    @Override
    @Transactional
    public void delBatch(List<Long> ids) {

        // 是否存在启售菜品
        ids.forEach(id -> {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        // 判断菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()){
            // 当前套餐被关联 不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中数据
        dishMapper.delBatch(ids);
        // 直接根据菜品id 删除口味表 不管有没有
        dishFlavorMapper.delBatch(ids);
    }

    @Override
    public DishVo getByIdWithFlavor(Long id) {

        Dish dish = dishMapper.getById(id);

        List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(id);

        DishVo dishVo = new DishVo();
        BeanUtils.copyProperties(dish, dishVo);

        if (dishFlavor != null){
            dishVo.setFlavors(dishFlavor);
        }

        return dishVo;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 修改菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 修改菜品对应口味 直接将口味全部删掉  再重新添加
        dishFlavorMapper.delByDishId(dish.getId());

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && ! dishFlavors.isEmpty()){
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            // 批量插入口味数据
            dishFlavorMapper.saveBatch(dishFlavors);
        }
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();

        dishMapper.update(dish);
    }

}
