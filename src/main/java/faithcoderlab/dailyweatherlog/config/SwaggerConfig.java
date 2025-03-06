package faithcoderlab.dailyweatherlog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Weather Diary API")
                        .description("날씨 일기 작성/조회/수정/삭제 API")
                        .version("v1.0.0"));
    }
}
