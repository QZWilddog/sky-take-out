package cn.zimeedu.sky.controller.admin;

import cn.zimeedu.sky.constant.MessageConstant;
import cn.zimeedu.sky.result.Result;
import cn.zimeedu.sky.utils.AiOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
public class CommonController {

    private final AiOssUtil aliOssUtil;

    public CommonController(AiOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    /**
     * 文件上传
     * */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);

        try {
            String filePath = aliOssUtil.upload(file.getBytes(), file.getOriginalFilename());//  获取原始文件名

            return Result.success(filePath);
        } catch (Exception e) {
            log.info("文件上传失败:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
