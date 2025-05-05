package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.dto.OrdersPageQueryDTO;
import cn.zimeedu.sky.entity.Orders;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    // 新增订单
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    // 用户查询所有订单信息
    Page<Orders> list(OrdersPageQueryDTO ordersPageQueryDTO);

    // 根据id查询订单信息
    @Select("select * from orders where id = #{id};")
    Orders getById(Long id);

    // 管理端根据条件查询历史订单
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    // 统计订单状态
    @Select("select count(status) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
