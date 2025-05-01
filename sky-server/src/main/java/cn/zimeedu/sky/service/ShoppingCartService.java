package cn.zimeedu.sky.service;

import cn.zimeedu.sky.dto.ShoppingCartDTO;
import cn.zimeedu.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    // 查询购物车内的所有商品
    List<ShoppingCart> list();

    // 清空购物车
    void clean();
    // 删除购物车中的某一个商品
    void del(ShoppingCartDTO shoppingCartDTO);
}
