package cn.zimeedu.sky.utils;

import cn.zimeedu.sky.properties.WeChatProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 微信支付工具类
 */
@Component
public class WeChatPayUtil {

    //微信支付下单接口地址
    public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    //申请退款接口地址
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 获取调用微信接口的客户端工具对象
     *
     * @return
     */
    private CloseableHttpClient getClient() {
        // Java 中 java.security 包下的一个类，它代表了非对称加密算法中的私钥部分。非对称加密使用一对密钥：一个公钥和一个私钥。这两个密钥在数学上是相关的，但根据当前的技术，从一个密钥计算出另一个密钥是非常困难的。
        PrivateKey merchantPrivateKey = null;
        try {
            //merchantPrivateKey商户API私钥，如何加载商户API私钥请看常见问题
            // 于解密由对应公钥加密的数据，或者用于数字签名以验证消息的发送者身份。 通过私钥对数据进行签名，接收方可以使用相应的公钥来验证签名的真实性，从而确保数据未被篡改且确实来自拥有相应私钥的一方。
            // loadPrivateKey(InputStream inputStream)从给定的输入流（通常是 PEM 格式的私钥文件）中加载私钥，并将其转换为 java.security.PrivateKey 对象。  PemUtil它是微信支付官方提供的一个工具类，专门用于处理 PEM（Privacy Enhanced Mail）格式的密钥文件。这个工具类简化了从 PEM 文件加载私钥的过程，使得开发者可以更容易地在与微信支付相关的开发中使用这些密钥。
            // 这里我没有这个商户私钥所以注释
//            merchantPrivateKey = PemUtil.loadPrivateKey(new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath())));

            //加载平台证书文件  PemUtil它是微信支付官方提供的一个工具类，专门用于处理 PEM（Privacy Enhanced Mail）格式的密钥文件。这个工具类简化了从 PEM 文件加载私钥的过程，使得开发者可以更容易地在与微信支付相关的开发中使用这些密钥。
            //  是 Java 中 java.security.cert 包下的一个类，它代表了 X.509 标准定义的公钥证书。X.509 是一种用于数字证书的标准格式，广泛应用于网络安全领域，比如 SSL/TLS 证书、客户端认证和代码签名等。
            // 身份验证  数据完整性  安全通信   loadCertificate（）从给定的输入流（通常是 PEM 格式的证书文件）中加载 X.509 证书，并将其转换为 java.security.cert.X509Certificate 对象。
            // 这里没有这个证书，因为是根据店家私钥等动态生成的
            X509Certificate x509Certificate = PemUtil.loadCertificate(new FileInputStream(new File(weChatProperties.getWeChatPayCertFilePath())));

            //wechatPayCertificates微信支付平台证书列表。你也可以使用后面章节提到的“定时更新平台证书功能”，而不需要关心平台证书的来龙去脉
            // 接收可变参数（可以是一个或多个对象），并返回一个包含这些对象的固定大小的 List。   批量处理: 如果你需要处理多个证书，比如从微信支付平台下载了多个证书，那么将它们都放入一个 List 中会非常方便进行批量操作。  API兼容性: 某些 API 或框架可能要求传入一个集合类型的数据结构（如 List），而不是单个对象。因此，即使只有一个证书，也需要将其放入 List 中以满足接口要求。扩展性: 即使当前只有一个证书，未来可能会有多个证书需要处理，提前设计成 List 形式可以使代码更具扩展性和灵活性。
            List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);

            // WechatPayHttpClientBuilder 简化与微信支付API的安全通信。它提供了一种方便的方式来配置和创建适合与微信支付API交互的HTTP客户端。
            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()  // .create(): 静态工厂方法，用于创建一个新的 WechatPayHttpClientBuilder 实例。
                    // withMerchant 方法用于设置商户信息：  mchId: 商户号  mchSerialNo: 商户证书序列号 没有 privateKey: 商户私钥 没有
//                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey);
                    .withMerchant(weChatProperties.getMchid(), null, merchantPrivateKey);
                    //.withWechatPay 方法用于设置微信支付平台的证书列表： 没有
//                    .withWechatPay(wechatPayCertificates);

            // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签  就是帮我简化了 配置HTTP客户端的过程
            CloseableHttpClient httpClient = builder.build();  // 调用 .build() 方法后，最终会返回一个已经配置好签名、验签、加密等安全机制的 CloseableHttpClient 实例。
            return httpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送post方式请求
     *
     * @param url
     * @param body
     * @return
     */
    private String post(String url, String body) throws Exception {
        // 创建Http请求客户端对象
        CloseableHttpClient httpClient = getClient();

        // 创建请求对象
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头，HttpClient 自动加到请求头
        // 设置请求头：告诉微信服务器，我能接受什么样的返回内容格式 APPLICATION_JSON 表示我们希望收到的是 JSON 格式的数据
        // HttpHeaders 是一个包含常用 HTTP 头部名称的静态常量集合。它提供了一种标准化的方式来引用常见的 HTTP 头部字段名。
        // ContentType 类用于表示 HTTP 请求或响应中的内容类型（MIME 类型）。它不仅包含了 MIME 类型本身，还可能包括字符集编码等附加信息。
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

        // 设置请求头：告诉微信服务器，我这次发送给你的数据是什么格式 APPLICATION_JSON 表示我们发送的是 JSON 格式的内容
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        // 设置请求头：告诉微信服务器我是谁（使用的是哪个商户私钥对应的证书）
        // 这个编号叫做 "商户证书序列号"，在微信后台可以看到 没有
//        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

        // 用于为 HTTP POST 请求设置请求体
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        //发送请求并获取响应结果
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            // 将响应体 数据返回
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * 发送get方式请求
     *
     * @param url
     * @return
     */
    private String get(String url) throws Exception {
        CloseableHttpClient httpClient = getClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());

        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return bodyAsString;
        } finally {
            httpClient.close();
            response.close();
        }
    }

    /**
     * jsapi下单
     *
     * @param orderNum    商户订单号
     * @param total       总金额
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    private String jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        // 封装BODY参数 appid 商户号 订单描述 管理端订单号 商户回调地址 为json数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", weChatProperties.getAppid());
        jsonObject.put("mchid", weChatProperties.getMchid());
        jsonObject.put("description", description);
        jsonObject.put("out_trade_no", orderNum);
        jsonObject.put("notify_url", weChatProperties.getNotifyUrl());

        // 封装BODY嵌套部分参数amount（订单金额）  BigDecimal 是 Java 中的一个类，位于 java.math 包中。它提供了高精度的十进制数操作，主要用于需要精确表示和处理小数值的情况，比如货币计算等场景。使用 BigDecimal 可以避免浮点类型（如 float 和 double）常见的精度问题。
        JSONObject amount = new JSONObject();
        // 用 BigDecimal 的构造函数创建一个新的 BigDecimal 对象，这里初始化为 100（教学阶段写死）  setScale 设置小数位数和舍入模式（保留2位，RoundingMode 枚举类四舍五入）  intValue() 将 BigDecimal 对象转换为 int 类型的值。此方法会截断小数部分，仅保留整数部分。
        // total.multiply(...) 是 BigDecimal 类中的一个方法，用于执行两个 BigDecimal 数值的乘法运算。 因为微信用整数作为金额单位 1 = 0.01 分
        amount.put("total", total.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.put("currency", "CNY");

        // 封装BODY嵌套部分参数payer【支付者信息】支付者信息
        jsonObject.put("amount", amount);
        // 支付者信息 用户的openid 用户在商户appid下的唯一标识。
        JSONObject payer = new JSONObject();
        payer.put("openid", openid);

        jsonObject.put("payer", payer);

        String body = jsonObject.toJSONString();
        return post(JSAPI, body);
    }

    /**
     * 小程序支付 调用微信下单接口
     *
     * @param orderNum    商户订单号
     * @param total       金额，单位 元
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return
     */
    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        //统一下单，生成预支付交易单
        String bodyAsString = jsapi(orderNum, total, description, openid);
        //解析返回结果
        JSONObject jsonObject = JSON.parseObject(bodyAsString);
        System.out.println(jsonObject);

        String prepayId = jsonObject.getString("prepay_id");
        if (prepayId != null) {
            // 封装数据 加密 签名  给前端小程序调动支付功能传递前端配置参数
            // 获取时间戳 标准北京时间，时区为东八区，自1970年1月1日 0点0分0秒以来的秒数。  部分系统取到的值为毫秒级，商户需要转换成秒(10位数字)。
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            // 【随机字符串】不长于32位。该值建议使用随机数算法生成。 RandomStringUtils 类生成一个由数字组成的随机字符串的方法。 .randomNumeric(int count): 这是 RandomStringUtils 类中的一个静态方法，用于生成指定长度的只包含数字字符的随机字符串。
            String nonceStr = RandomStringUtils.randomNumeric(32);
            // 【预支付交易会话标识】JSAPI/小程序下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
            String packageWx = "prepay_id=" + prepayId;

            //  【签名值】使用字段appId、timeStamp、nonceStr、package计算得出的签名值，详细参考：小程序调起支付签名。  注意：此处签名需使用实际调起支付小程序appid，且为JSAPI/小程序下单时传入的appid，微信支付会校验下单与调起支付所使用的appid的一致性。
            ArrayList<Object> list = new ArrayList<>();
            list.add(weChatProperties.getAppid());
            list.add(timeStamp);
            list.add(nonceStr);
            list.add(packageWx);
            //二次签名，调起支付需要重新签名  StringBuilder可变的字符序列类  都是微信开发文档规定的
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : list) {
                stringBuilder.append(o).append("\n");
            }
            String signMessage = stringBuilder.toString();
            byte[] message = signMessage.getBytes();

            // 提供了用于数字签名和签名验证的方法。它允许你生成一个数字签名或将数据与已知的数字签名进行比较以验证其真实性。 该对象使用 SHA-256 哈希算法和 RSA 加密算法进行数字签名或验证
            Signature signature = Signature.getInstance("SHA256withRSA");
            // 加载商户私钥  weChatProperties.getPrivateKeyFilePath() 返回私钥文件的路径
            // PemUtil.loadPrivateKey(InputStream inputStream) 方法从输入流中读取并解析 PEM 格式的私钥
            // 使用私钥初始化签名对象  initSign 方法准备签名对象以生成数字签名
//            signature.initSign(PemUtil.loadPrivateKey(new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath()))));
            // 更新要签名的消息内容 update 方法将数据添加到签名对象中，以便后续生成签名
            signature.update(message);
            // 将生成的数字签名编码为 Base64 字符串
            // Base64 编码使得签名可以方便地在文本协议（如 HTTP 请求）中传输
            String packageSign = Base64.getEncoder().encodeToString(signature.sign());

            //构造数据给微信小程序，用于调起微信支付
            JSONObject jo = new JSONObject();
            jo.put("timeStamp", timeStamp);
            jo.put("nonceStr", nonceStr);
            jo.put("package", packageWx);
            jo.put("signType", "RSA");
            jo.put("paySign", packageSign);

            return jo;
        }
        return jsonObject;
    }

    /**
     * 申请退款
     *
     * @param outTradeNo    商户订单号
     * @param outRefundNo   商户退款单号
     * @param refund        退款金额
     * @param total         原订单金额
     * @return
     */
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);

        JSONObject amount = new JSONObject();
        amount.put("refund", refund.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = jsonObject.toJSONString();

        //调用申请退款接口
        return post(REFUNDS, body);
    }
}
