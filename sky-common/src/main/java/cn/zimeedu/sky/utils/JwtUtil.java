package cn.zimeedu.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// 生成和解析令牌
public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {


        return Jwts.builder() // 构建Jwt令牌
                // - 第一部分：Header(头）， 记录令牌类型、签名算法等。 例如：{"alg":"HS256","type":"JWT"}
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))   // 指定签名算法（加密算法，密钥）
                // - 第二部分：Payload(有效载荷），携带一些自定义信息、默认信息等。 例如：{"id":"1","username":"Tom"}
                .setClaims(claims)  // 添加自定义信息  是一个map集合
                //  // 指定有效期（指定日期时间类型 ）        - 第三部分：Signature(签名），防止Token被篡改、确保安全性。将header、payload，并加入指定秘钥，通过指定签名算法计算而来。
                .setExpiration(new Date(System.currentTimeMillis() + ttlMillis))
                .compact();  // 生成令牌
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 创建 JwtParser 对象，用于解析 JWT 令牌
        return Jwts.parser()
                // 设置解析 JWT 令牌时使用的签名密钥，确保令牌的完整性和真实性
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 解析传入的 JWT 令牌字符串，验证签名并返回 Jws<Claims> 对象
                .parseClaimsJws(token)
                // 从 Jws<Claims> 对象中提取出 JWT 令牌的负载部分，即 Claims 对象
                .getBody(); // Jws<Claims>是自定义信息部分 本质是一个Map集合 {"id":"1","username":"Tom" “Exp”：12323 }
    }

}
