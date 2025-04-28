package cn.zimeedu.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfiguration {

    @Bean
    //创建一个 RedisTemplate 对象的。RedisTemplate 是 Spring Data Redis 提供的一个核心类，用于操作 Redis 数据库。它封装了与 Redis 的交互逻辑，比如存取数据、序列化等。
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){  // Redis 的连接工厂对象，负责管理与 Redis 服务器的连接。 这个工厂对象通常由 Spring Boot 自动配置生成，开发者不需要手动创建。
        log.info("开始创建redis模板对象...");
        // RedisTemplate 是 Spring Data Redis 提供的核心工具类，用于操作 Redis 数据库。
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        // 设置reids的连接工程对象  RedisConnectionFactory 负责管理与 Redis 服务器的连接（比如建立连接、断开连接等）。 RedisTemplate 知道如何与 Redis 通信。
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置redis key的序列化器  设置 Redis 键的序列化方式  不设置有默认的是二进制存储   当你使用 StringRedisSerializer 时，键和值会被转换为 UTF-8 编码的字符串，然后再被 Redis 存储为字节数组。
        // redisTemplate.setKeySerializer(new StringRedisSerializer()) 的作用：将 Redis 的键序列化为 UTF-8 字符串，避免出现乱码。  而使用 StringRedisSerializer 后，键将以字符串的形式存储和读取，而不是字节数组。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
