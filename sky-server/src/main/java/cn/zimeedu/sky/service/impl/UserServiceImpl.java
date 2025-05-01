package cn.zimeedu.sky.service.impl;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.dto.UserLoginDTO;
import cn.zimeedu.sky.entity.User;
import cn.zimeedu.sky.exception.LoginFailedException;
import cn.zimeedu.sky.mapper.UserMapper;
import cn.zimeedu.sky.properties.WeChatProperties;
import cn.zimeedu.sky.service.UserService;
import cn.zimeedu.sky.utils.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    // 微信服务接口地址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    // 微信登陆固定传递的参数
    public static final String GRANT_TYPE = "authorization_code";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 抽成方法 调用微信接口服务 获取用户 openid
        String openId = getOpenId(userLoginDTO.getCode());

        // 判断openid是否存在，如果为空表示登陆失败 抛出业务异常
        if(openId == null || openId.isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断当前用户是否是新用户  为新用户 自动完成注册（就是数据库里添加他）
        User user = userMapper.getByOpenId(openId);

        if (user == null){
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.save(user);
        }

        return user;
    }

    // 调用微信接口服务，获取当前微信用户的openid
    private String getOpenId(String code) {
        // 调用微信接口服务，获取当前微信用户的openid
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", GRANT_TYPE);
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, paramMap);
        //解析返回的json字符串  反序列化为JAVA对象
        JSONObject jsonObject = JSON.parseObject(json);

        return jsonObject.getString("openid");
    }
}
