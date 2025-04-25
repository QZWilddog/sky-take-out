package cn.zimeedu.sky.controller.admin;


import cn.zimeedu.sky.dto.SetmealDTO;
import cn.zimeedu.sky.dto.SetmealPageQueryDTO;
import cn.zimeedu.sky.mapper.SetmealMapper;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.SetmealService;
import cn.zimeedu.sky.vo.SetmealVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping
    public Result<Object> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);

        setmealService.save(setmealDTO);

        return Result.success("新增套餐成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealVo> getByIdWithDish(@PathVariable Long id){
        log.info("根据id查询套餐与包含的菜品：{}", id);
        SetmealVo setmealVo =  setmealService.getByIdWithDish(id);

        return Result.success(setmealVo);
    }

    @PutMapping
    public Result<Object> update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}", setmealDTO);
        setmealService.update(setmealDTO);

        return Result.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public Result<Object> setStatus(@PathVariable Integer status, Long id){
        log.info("更改套餐状态：{}", status);
        setmealService.setStatus(status, id);

        return Result.success("更改状态成功");
    }

    @DeleteMapping
    public Result<Object> delBatch(@RequestParam  List<Long> ids){
        log.info("批量删除套餐：{}", ids);

        setmealService.delBatch(ids);

        return Result.success("删除成功");
    }
}
