package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.constant.StatusConstant;
import cn.zimeedu.sky.dto.SetmealDTO;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.entity.Setmeal;
import cn.zimeedu.sky.entity.SetmealDish;
import cn.zimeedu.sky.exception.DeletionNotAllowedException;
import cn.zimeedu.sky.mapper.DishMapper;
import cn.zimeedu.sky.mapper.SetmealDishMapper;
import cn.zimeedu.sky.mapper.SetmealMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.service.SetmealService;
import cn.zimeedu.sky.vo.SetmealVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImp implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVo> setmeals = setmealMapper.page(setmealPageQueryDTO);

        return new PageResult(setmeals.getTotal(), setmeals.getResult());
    }

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.save(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmeal.getId());});

            setmealDishMapper.saveBatch(setmealDishes);
        }
    }

    @Override
    public SetmealVo getByIdWithDish(Long id) {

        SetmealVo setmealVo =  new SetmealVo();

        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal, setmealVo);

        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        if (setmealDishes != null && !setmealDishes.isEmpty()){
            setmealVo.setSetmealDishes(setmealDishes);
        }

        return setmealVo;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishMapper.dleBatchBySetmealId(setmeal.getId());

        if(setmealDishes != null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmeal.getId());});

            setmealDishMapper.saveBatch(setmealDishes);
        }
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        // 先检查菜品表内是否有为起售的菜品 有的话不能起售该套餐
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealDishes.forEach(setmealDish -> {
            Dish byId = dishMapper.getById(setmealDish.getDishId());
            if (byId.getStatus().equals(StatusConstant.DISABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        });

        setmealMapper.update(setmeal);
    }

    @Override
    public void delBatch(List<Long> ids) {

        // 是否存在启售套餐
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        // 删除套餐
        setmealMapper.delBatch(ids);
        // 删除套餐关联的菜品
        setmealDishMapper.delBatch(ids);
    }
}
