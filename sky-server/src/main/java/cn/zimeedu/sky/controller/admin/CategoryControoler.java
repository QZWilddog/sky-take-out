package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.dto.CategoryDTO;
import cn.zimeedu.sky.dto.CategoryPageQueryDTO;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.result.PageResult;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/category")
public class CategoryControoler {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询菜品和分类
     * */
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询菜品分类{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 禁用或启用菜品或分类
     * */
    @PostMapping("/status/{status}")
    public Result<Object> setStatus(@PathVariable Integer status, Long id){
        log.info("菜品状态设置：{}", status);
        categoryService.setStatus(status, id);

        return Result.success("菜品状态设置成功");
    }

    /**
     * 新增菜品
     * */
    @PostMapping
    public  Result<Object> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增菜品：{}", categoryDTO);
        categoryService.save(categoryDTO);

        return Result.success();
    }

    /**
     * 修改菜品或分类
     * */
    @PutMapping
    public Result<Object> update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改菜品分类:{}", categoryDTO);
        categoryService.update(categoryDTO);

        return Result.success("修改菜品成功");
    }

    /**
     * 删除菜品或分类
     * */
    @DeleteMapping
    public Result<Object> deleteById(Long id){
        log.info("删除菜品或分类id：{}", id);
        categoryService.deleteById(id);

        return Result.success("删除成功");
    }
}
