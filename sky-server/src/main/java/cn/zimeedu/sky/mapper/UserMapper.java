package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    // 根据openid查询用户
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    // 添加用户
    void save(User user);

    // 根据id查询用户
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    // 查询每日新增用户数量与用户总数
    Integer getByMap(Map<String, Object> map);
}
