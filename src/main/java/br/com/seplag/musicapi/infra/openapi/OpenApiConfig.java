package br.com.seplag.musicapi.infra.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.utils.SpringDocUtils;

@Configuration
public class OpenApiConfig {

  static {
    SpringDocUtils.getConfig().replaceWithClass(Pageable.class, PageableRequest.class);
  }

  @Bean
  public OpenAPI openAPI() {
    final String schemeName = "bearerAuth";
    return new OpenAPI()
        .info(new Info().title("Music API").version("v1"))
        .addSecurityItem(new SecurityRequirement().addList(schemeName))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes(schemeName, new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
  }
}
