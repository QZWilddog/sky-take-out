package cn.zimeedu.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 配置类，注册web层相关组件
 *  WebMvcConfigurationSupport 主要用于通过 Java 配置方式自定义 Spring MVC 的行为 提供了一组默认的 Spring MVC 配置（如视图解析器、拦截器、静态资源处理等）
 *      通过继承 WebMvcConfigurationSupport，你可以完全掌控 Spring MVC 的配置，而不是依赖 Spring Boot 的自动配置。静态资源的路径 拦截器。
 *      WebMvcConfigurer(当你只需要对 Spring MVC 的某些部分进行微调时（如添加拦截器、视图解析器、格式化器等），可以实现这个接口并覆盖你需要的方法。) 和 WebMvcConfigurationSupport(当你需要完全控制 Spring MVC 的配置时（例如，你需要自定义几乎所有的配置项）) 都是 Spring MVC 提供的用于自定义配置的机制
 *      继承 WebMvcConfigurationSupport 会禁用 Spring Boot 的自动配置功能。这意味着你需要手动配置所有相关的组件（如视图解析器、静态资源处理器等）。
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    /**
     * 通过knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket docket() {
        log.info("准备生成接口文档...");
        // 创建 ApiInfo 对象  构建接口文档的基本信息 是一个构建器类，用于逐步设置接口文档的基本信息
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")   // 接口文档的标题
                .version("2.0")   //  接口文档的版本号
                .description("苍穹外卖项目接口文档")  //  接口文档的描述信息
                .build();  // 生成目标对象  这个对象包含了接口文档的所有基本信息，后续会被传递给 Docket 使用

        // 创建并配置 Docket 对象 是 Swagger 的核心类，用于定义和配置接口文档的行为
        Docket docket = new Docket(DocumentationType.SWAGGER_2)  // 定文档类型为 DocumentationType.SWAGGER_2，表示使用 Swagger 2 规范。
                .apiInfo(apiInfo)  //  调用 apiInfo 方法，将上面创建的 ApiInfo 对象设置到 Docket 中
                .select()  // 方法返回一个 ApiSelectorBuilder 对象，用于配置接口扫描规则
                .apis(RequestHandlerSelectors.basePackage("cn.zimeedu.sky.controller"))  //  指定生成接口要扫描的包路径  即只扫描该包下的控制器类
                .paths(PathSelectors.any())  // 指定路径匹配规则，PathSelectors.any() 表示匹配所有路径。
                .build();  //生成目标对象
        return docket;
    }

    /**
     * 设置静态资源映射  重写MVC配置静态资源的处理规则
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {  // 这是一个注册器对象，用于将静态资源的访问路径与实际的资源位置关联起来。
        log.info("开始设置静态资源映射...");
        // 配置 /doc.html 的访问路径
        registry.addResourceHandler("/doc.html")  // 表示当用户访问 /doc.html 时，Spring MVC 会去指定的资源位置查找对应的文件。
                .addResourceLocations("classpath:/META-INF/resources/");   // 指定资源的实际位置，这里是类路径下的 /META-INF/resources/ 目录。
        // 配置 /webjars/** 的访问路径
        registry.addResourceHandler("/webjars/**")  // 表示匹配所有以 /webjars/ 开头的请求路径。** 是通配符，表示任意子路径。
                .addResourceLocations("classpath:/META-INF/resources/webjars/");  // 指定资源的实际位置，这里是类路径下的 /META-INF/resources/webjars/ 目录。
    }
}
