package cn.zimeedu.sky.controller.user;

import cn.zimeedu.sky.constant.StatusConstant;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.jshell.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userCategoryController")  // 自定义的 Bean 名称。 避免命名冲突
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询分类")
    public Result<List<Category>> list(Integer type) {
        log.info("查询分类：{}", type);
        List<Category> list = categoryService.listUser(type, StatusConstant.ENABLE);
        return Result.success(list);
    }
}
