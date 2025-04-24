package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.dto.DishDTO;
import cn.zimeedu.sky.dto.DishPageQueryDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.DishService;
import cn.zimeedu.sky.vo.DishVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api("菜品相关接口")
public class DishControoler {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品和对应口味")
    public Result<Object> save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品和对应口味:{}", dishDTO);
        dishService.saveAndFlavor(dishDTO);

        return Result.success("菜品保存成功");
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品:{}", dishPageQueryDTO);

        PageResult pageResult =  dishService.page(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<Object> delBatch(@RequestParam List<Long> ids){
        log.info("删除菜品:{}", ids);
        dishService.delBatch(ids);

        return Result.success("删除菜品成功");
    }

    @GetMapping("/{id}")
    public Result<DishVo> getById(@PathVariable Long id){
        log.info("查询菜品:{}", id);

        DishVo dishVo =  dishService.getByIdWithFlavor(id);

        return Result.success(dishVo);
    }

    @PutMapping
    public Result<Object> update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        return Result.success("更改成功");
    }

    @PostMapping("/status/{status}")
    public Result<Object> setStatus(@PathVariable Integer status, Long id){
        log.info("启售\\禁用菜品:{}-{}", id, status);
        dishService.setStatus(status, id);

        return Result.success("操作成功");
    }
}
