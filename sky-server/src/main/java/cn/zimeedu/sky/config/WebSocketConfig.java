package cn.zimeedu.sky.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        // ServerEndpointExporter：这是 Jakarta WebSocket API 和 Spring 集成的一个关键组件。
        // 它负责扫描应用上下文中所有带有 @ServerEndpoint 注解的类，并将它们注册到 Servlet 容器的 WebSocket 端点中。

        //为什么需要 ServerEndpointExporter：默认情况下，Spring Boot 并不会自动扫描和注册 @ServerEndpoint 注解的类。
        // 因此，如果你想要在 Spring Boot 应用中使用 WebSocket 并且已经使用了 @ServerEndpoint 来定义端点，
        // 则需要手动配置 ServerEndpointExporter 来确保这些端点能够被正确地注册。
        return new ServerEndpointExporter();
    }
}
