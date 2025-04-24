package cn.zimeedu.sky.config;


import cn.zimeedu.sky.properties.AliOssPropertes;
import cn.zimeedu.sky.utils.AiOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AiOssUtil aliOssUtil(AliOssPropertes aliOssPropertes){
        log.info("开始创建阿里云文件上传工具类对象");

        return AiOssUtil.builder()
                .endpoint(aliOssPropertes.getEndpoint())
                .region(aliOssPropertes.getRegion())
                .accessKeyId(aliOssPropertes.getAccessKeyId())
                .accessKeySecret(aliOssPropertes.getAccessKeySecret())
                .bucketName(aliOssPropertes.getBucketName())
                .build();
    }
}
