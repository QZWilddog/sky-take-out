package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.context.BaseContext;
import cn.zimeedu.sky.dto.ShoppingCartDTO;
import cn.zimeedu.sky.entity.Dish;
import cn.zimeedu.sky.entity.Setmeal;
import cn.zimeedu.sky.entity.ShoppingCart;
import cn.zimeedu.sky.mapper.DishMapper;
import cn.zimeedu.sky.mapper.SetmealMapper;
import cn.zimeedu.sky.mapper.ShoppingCartMapper;
import cn.zimeedu.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入到购物车中的商品是否已经存在  只有一条或者没有 只是为了复用性和解耦
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        // 如果存在 商品+1
        if (list != null && !list.isEmpty()){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);

            shoppingCartMapper.updateNumberById(cart);
        }else {

            // 判断当前是新增菜品信息还是套餐信息
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if ( dishId != null){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            // 如果不存在，插入一条购物车
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.save(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();

        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void clean() {

        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();

        shoppingCartMapper.deleteByUserId(shoppingCart);
    }

    @Override
    public void del(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();

        BeanUtils.copyProperties(shoppingCartDTO, cart);

        cart =  shoppingCartMapper.list(cart).get(0);

        int number = cart.getNumber();
        if (number > 1){
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartMapper.updateNumberById(cart);
        }else {
            shoppingCartMapper.deleteByUserId(cart);
        }

    }
}
