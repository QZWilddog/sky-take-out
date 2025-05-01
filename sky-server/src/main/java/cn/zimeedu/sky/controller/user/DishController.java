package cn.zimeedu.sky.controller.user;


import cn.zimeedu.sky.constant.StatusConstant;
import cn.zimeedu.sky.entity.Category;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.DishService;
import cn.zimeedu.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据套餐ID查询对应菜品：{}", categoryId);

        // 查询Redis中是否有缓存的数据
        String key = "dish_" + categoryId;
        List<DishVO> dishs = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishs != null && !dishs.isEmpty()){
            // 如果存在直接返回缓存数据，无序查询数据库
            return Result.success(dishs);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        dishs = dishService.listWithFlavor(dish);

        // 如果不存在 查询数据库并将数据缓存在redis中
        redisTemplate.opsForValue().set(key, dishs);

        return Result.success(dishs);
    }

}
