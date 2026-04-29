package com.payment.paymentServices.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){

        return new OpenAPI()
                .info(new Info()
                        .title("Transactions API Documents")
                        .version("1.0")
                        .description("Rest API for transactions"))
                .components(new Components()
                        .addSchemas("ErrorResponse", new ObjectSchema()
                                .addProperty("status",new IntegerSchema().example(400))
                                .addProperty("message",new StringSchema().example("Resource not found"))));

    }

    @Bean
    public OpenApiCustomizer globalResponseController(){

        return openApi -> openApi.getPaths().values().forEach(
                pathItem -> pathItem.readOperations().forEach(operation -> {
                    ApiResponses apiResponses = new ApiResponses();

                    apiResponses.addApiResponse("400",new ApiResponse()
                            .description("Bad Request -Invalid Input Data")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));

                    apiResponses.addApiResponse("404",new ApiResponse()
                            .description("Not Found -Resource Missing")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));

                    apiResponses.addApiResponse("500",new ApiResponse()
                            .description("Internal Server Error")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));

                })
        );

    }

}

