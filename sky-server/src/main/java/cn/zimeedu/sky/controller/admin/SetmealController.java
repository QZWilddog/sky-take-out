package cn.zimeedu.sky.controller.admin;


import cn.zimeedu.sky.dto.SetmealDTO;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.SetmealService;
import cn.zimeedu.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/admin/setmeal")
@RestController
@Api("套餐控制器")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询{}", setmealPageQueryDTO);

        PageResult pageResult = setmealService.page(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")  // 精确清理缓存 kys=setmealCache::1  这里不为User端查询分类下的套餐提供数据所以只管理清理旧缓存，而且如果不清除就会使reids中的数据项不一致  当然这里可以用CachePut但是这里可以统一用删除就用删除 只管删除
    @PostMapping
    public Result<Object> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);

        setmealService.saveWithDish(setmealDTO);

        return Result.success("新增套餐成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> getByIdWithDish(@PathVariable Long id){
        log.info("根据id查询套餐与包含的菜品：{}", id);
        SetmealVO setmealVo =  setmealService.getByIdWithDish(id);

        return Result.success(setmealVo);
    }

    @PutMapping
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")  // 这里如果更改categoryId那么在redis中，就是影响了两个数据项 这里无法做到精确删除 所以全删
    public Result<Object> update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);

        return Result.success("修改成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)   // 因为redis中存储的是cacheName::categoryId 这里无法获取 做到精确删除 所以全删
    public Result<Object> setStatus(@PathVariable Integer status, Long id){
        log.info("更改套餐状态：{}", status);
        setmealService.setStatus(status, id);

        return Result.success("更改状态成功");
    }

    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)   // 获取不到categoryId 这里无法做到精确删除 所以全删
    public Result<Object> delBatch(@RequestParam  List<Long> ids){
        log.info("批量删除套餐：{}", ids);

        setmealService.delBatch(ids);

        return Result.success("删除成功");
    }
}
