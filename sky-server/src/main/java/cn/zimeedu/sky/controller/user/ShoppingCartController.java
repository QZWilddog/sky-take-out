package cn.zimeedu.sky.controller.user;

import cn.zimeedu.sky.dto.ShoppingCartDTO;
import cn.zimeedu.sky.entity.ShoppingCart;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("C端购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("add")
    @ApiOperation("添加购物车")
    public Result<Object> add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息为：{}", shoppingCartDTO);

        shoppingCartService.addShoppingCart(shoppingCartDTO);

        return Result.success("添加成功");
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("查询所有购物车内的商品");

        List<ShoppingCart> shoppingCarts =  shoppingCartService.list();

        return Result.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    public Result<Object> clean(){
        log.info("清空购物车");

        shoppingCartService.clean();

        return Result.success("清空成功");
    }

    @PostMapping("/sub")
    public Result<Object> del(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中的{}商品", shoppingCartDTO);

        shoppingCartService.del(shoppingCartDTO);
        return Result.success("删除成功");
    }
}
