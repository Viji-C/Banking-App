package com.example.bankingapp.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "The Banking App",
        description = "Rest Api for Banking Application to create account, credit, debit, transfer amount",
        version = "V1.0",
        contact = @Contact(
            name = "Viji C",
            email = "vijichinnaraj2001@gmail.com",
            url = "https://github.com/Viji-C"
        ),
        license = @License(
            name = "Viji C",
            url = "https://github.com/Viji-C"
        )
    ),
    externalDocs = @ExternalDocumentation(
        description = "The Bank App Documentation",
        url = "https://github.com/Viji-C"
    )
)
public class OpenApiConfig
{

}
