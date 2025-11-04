package org.example.boxes.config;

<<<<<<< HEAD
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
=======
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
>>>>>>> 274ee59967deb523f16d5cc722ba6731b64355b7
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
<<<<<<< HEAD
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能收纳盒系统 API 文档")
                        .description("智能收纳盒系统的RESTful API接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("14577")
                                .email(""))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
=======
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("智能收纳盒系统 API 文档")
                        .description("智能收纳盒系统的RESTful API接口文档")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目仓库")
                        .url("https://github.com/SHIJYang/Smartboxes"));
    }
}
>>>>>>> 274ee59967deb523f16d5cc722ba6731b64355b7
