package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    // 动态条件查询购物车
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    // 根据id修改数量 这里不考虑复用
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    // 新增购物车数据
    void save(ShoppingCart shoppingCart);

    // 根据userId查询数据
    void deleteByUserId(ShoppingCart shoppingCart);
}
