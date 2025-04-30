package cn.zimeedu.sky.service;

import cn.zimeedu.sky.dto.UserLoginDTO;
import cn.zimeedu.sky.entity.User;
import cn.zimeedu.sky.vo.UserLoginVO;

public interface UserService {

    // 微信登陆
    User wxLogin(UserLoginDTO userLoginDTO);
}
