package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopControoler")  // 自定义的 Bean 名称。 避免命名冲突
@RequestMapping("/admin/shop")
@Slf4j
@Api("店铺管理相关接口 打样等")
public class ShopControoler {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<Object> setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态：{}", status == 1 ? "营业中" : "打样中");
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        stringObjectValueOperations.set(KEY, status);

        return Result.success("设置成功");
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);

        log.info("获取到店铺的状态为：{}", status == 1 ? "营业中":"打样中");

        return Result.success(status);
    }
}
