package cn.zimeedu.sky.controller.nofity;


import cn.zimeedu.sky.properties.WeChatProperties;
import cn.zimeedu.sky.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
public class PayNotifyController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 支付成功回调
     *
     * @param request  表示客户端发送到服务器的 HTTP 请求。它封装了所有请求相关信息，包括请求行（如请求方法、URL）、请求头、请求参数等。
     * @param response  提供了向客户端发送HTTP响应的方法。通过该接口可以设置响应的状态码、响应头、响应体等内容。
     */
    @RequestMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //读取数据
//        String body = readData(request);
//        log.info("支付成功回调：{}", body);
//
//        //数据解密
//        String plainText = decryptData(body);
//        log.info("解密后的文本：{}", plainText);
//
//        JSONObject jsonObject = JSON.parseObject(plainText);
//        String outTradeNo = jsonObject.getString("out_trade_no");//商户平台订单号
//        String transactionId = jsonObject.getString("transaction_id");//微信支付交易号
//
//        log.info("商户平台订单号：{}", outTradeNo);
//        log.info("微信支付交易号：{}", transactionId);

        //业务处理，修改订单状态、来单提醒
//        orderService.paySuccess(outTradeNo);

//        给微信响应  接收不到微信请求 不用这个
//        responseToWeixin(response);
    }

    /**
     * 从 HttpServletRequest 中读取整个请求体的内容，并拼接成一个字符串返回
     *
     * @param request 客户端发来的 HTTP 请求对象
     * @return 返回请求体中的完整数据（如 JSON 字符串）
     * @throws Exception 如果读取过程中出现错误，会抛出异常
     */
    private String readData(HttpServletRequest request) throws Exception {
        // 1. 获取请求体的字符流（BufferedReader），可以用来一行一行地读取数据  读取器可以读取 HTTP 请求体中的数据。具体来说，它允许你以字符流的形式读取请求体内容。
        BufferedReader reader = request.getReader();

        // 2. 创建一个 StringBuilder 对象，用来拼接读取到的每一行内容
        StringBuilder result = new StringBuilder();

        // 3. 定义一个变量 line，用来临时保存每次读取的一行数据
        String line = null;

        // 4. 循环读取请求体中的每一行内容，直到读完为止
        while ((line = reader.readLine()) != null) {
            // 如果 result 不是第一次添加内容，在前面加一个换行符，保持原始格式
            if (!result.isEmpty()) {
                result.append("\n");
            }

            // 把当前读取到的一行内容追加到 result 中
            result.append(line);
        }

        // 5. 最后把所有行拼接成一个完整的字符串返回
        return result.toString();
    }

    /**
     * 对微信回调中的加密数据进行解密，返回原始明文字符串
     *
     * @param body 微信回调传来的完整 JSON 字符串数据
     * @return 解密后的明文字符串（通常是订单或其他业务数据）
     * @throws Exception 如果解析或解密失败，会抛出异常
     */
    private String decryptData(String body) throws Exception {
        // 1. 把微信发过来的整个 JSON 字符串转换成 JSONObject 对象，方便取值
        JSONObject resultObject = JSON.parseObject(body);

        // 2. 从 JSON 中取出 "resource" 字段，这是微信加密的数据对象
        JSONObject resource = resultObject.getJSONObject("resource");

        // 3. 从 resource 中分别取出以下三个关键字段：
        // - ciphertext: 加密的密文数据（我们要解密的内容）
        // - nonce: 随机字符串，用于 AES-GCM 解密时的 nonce 参数
        // - associated_data: 附加数据，也是解密需要的参数之一
        String ciphertext = resource.getString("ciphertext");  // 【数据密文】Base64编码后的回调数据密文，商户需Base64解码并使用APIV3密钥解密，
        String nonce = resource.getString("nonce");    // 参与解密的随机串。
        String associatedData = resource.getString("associated_data");    // 【附加数据】参与解密的附加数据，该字段可能为空。

        // 4. 创建一个 AesUtil 工具类实例，用来执行 AES-GCM 解密操作
        // weChatProperties.getApiV3Key() 是微信 APIv3 密钥，必须是 32 字节长度
        AesUtil aesUtil = new AesUtil(weChatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));

        // 5. 调用 decryptToString 方法开始解密
        // 参数分别是：附加数据、随机数、密文
        String plainText = aesUtil.decryptToString(
                associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        // 6. 返回解密后的明文字符串，通常是原始的订单信息 JSON 字符串
        return plainText;
    }

    /**
     * 向微信服务器发送响应，表示我们已经成功接收并处理了它的回调通知
     *
     * @param response HttpServletResponse 是服务器用来向客户端（这里是微信服务器）返回响应的对象
     * @throws Exception 如果写入响应失败，可能会抛出异常
     */
    private void responseToWeixin(HttpServletResponse response) throws Exception {
        // 1. 设置 HTTP 状态码为 200，表示请求处理成功
        // 微信收到这个状态码后，就知道我们成功收到了它的通知
        response.setStatus(200);

        // 2. 创建一个 HashMap，用来存放我们要返回给微信的数据
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");   // code 表示处理结果状态
        map.put("message", "SUCCESS"); // message 是附加信息，用于说明原因

        // 3. 设置响应头 Content-Type 为 JSON 格式
        // 这样微信就知道我们返回的是一个 JSON 数据
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());

        // 4. 获取输出流，并把我们构造的 map 转成 JSON 字符串，写回给微信
        // JSON.toJSONString(map) 是使用 Fastjson 把 map 转成 {"code":"SUCCESS","message":"SUCCESS"}
        response.getOutputStream().write(
                JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8)
        );

        // 5. 强制刷新缓冲区，确保数据立即发送出去 强制将当前缓冲区中的所有数据立即发送给客户端，并清空缓冲区。通常情况下，当整个响应完成后，Servlet 容器会自动执行这个操作。
        response.flushBuffer();
    }
}
