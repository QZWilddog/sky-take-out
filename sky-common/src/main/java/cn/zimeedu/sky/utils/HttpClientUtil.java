package cn.zimeedu.sky.utils;


import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Http工具类 发送请求
public class HttpClientUtil {

    static final  int TIMEOUT_MSEC = 5 * 1000;

    /**
     * 发送GET方式请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String doGet(String url, Map<String, String> paramMap){
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String result = "";
        CloseableHttpResponse response = null;

        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null && !paramMap.isEmpty()){
                paramMap.forEach((k,v) -> {
                    builder.addParameter(k, v);
                });
            }
            URI uri = builder.build();

            // 创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 发送请求
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200){
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                response.close();
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送POST方式请求
     * @param url
     * @param paramMap
     * @return
     * @throws IOException
     */
    public static String doPost(String url, Map<String,String> paramMap) throws IOException {
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";

        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            // 创建参数列表
            if (paramMap != null) {
                // 创建参数列表
                List<NameValuePair> paramList = new ArrayList();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                // 模拟表单   使用 UrlEncodedFormEntity 将参数列表转换为 URL 编码的表单数据 模拟表单提交格式
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"UTF-8");
                // 用于为 HTTP POST 请求设置请求体
                httpPost.setEntity(entity);
            }
            // 这允许你自定义和调整 HTTP 请求的行为，以满足特定的需求。通过 RequestConfig，你可以对诸如连接超时时间、socket 超时时间、是否允许重定向等进行精细控制。
            httpPost.setConfig(builderRequestConfig());

            // 执行http请求
            response = httpClient.execute(httpPost);

            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static RequestConfig builderRequestConfig() {

        /**
         * 使用 setConfig 可以帮助你：
         * 设置连接超时：即客户端尝试与服务器建立连接的最大等待时间。
         * 设置socket超时：一旦建立了连接，等待数据传输完成的最大时间。
         * 配置重定向策略：决定是否自动跟随HTTP重定向响应。
         * 指定代理服务器：如果你的应用程序需要通过代理服务器访问网络资源。
         * 其他高级选项：如 cookie 规则、认证机制等。
         * */
        return RequestConfig.custom()  // 开始构建一个新的 RequestConfig 实例。
                .setConnectTimeout(TIMEOUT_MSEC)   // 设置建立连接的超时时间（毫秒）。
                .setConnectionRequestTimeout(TIMEOUT_MSEC)  //  设置从连接管理器获取连接的超时时间（毫秒）。
                .setSocketTimeout(TIMEOUT_MSEC).build();  //  设置等待数据的超时时间（毫秒）。如果在这个时间内没有收到任何数据，连接将被关闭。
                                                // build(): 完成配置并创建 RequestConfig 实例。
    }
}
