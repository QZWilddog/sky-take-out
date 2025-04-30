package cn.zimeedu.sky.controller.user;


import cn.zimeedu.sky.constant.JwtClaimsConstant;
import cn.zimeedu.sky.dto.UserLoginDTO;
import cn.zimeedu.sky.entity.User;
import cn.zimeedu.sky.properties.JwtProperties;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.service.UserService;
import cn.zimeedu.sky.utils.JwtUtil;
import cn.zimeedu.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api("用户端用户相关接口")
@Slf4j
public class UesrController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信用户登陆")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登陆：{}", userLoginDTO);

        User user =  userService.wxLogin(userLoginDTO);

        // 为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token).build();

        return Result.success(userLoginVO);
    }
}
