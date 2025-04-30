package cn.zimeedu.sky.mapper;

import cn.zimeedu.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 根据openid查询用户
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    // 添加用户
    void save(User user);
}
