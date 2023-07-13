package com.nineleaps.leaps.config.documentation;


import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContext;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig = new SwaggerConfig();

//    @Test
//    void testSwaggerConfigBean() {
//        // Arrange & Act
//        Docket docket = swaggerConfig.api();
//
//        // Assert
//        assertThat(docket).isNotNull();
//        // Add more assertions to verify the desired configuration settings
//        // For example:
//        assertThat(getSecuritySchemes(docket)).hasSize(1);
//        assertThat(getSecuritySchemes(docket).get(0).getName()).isEqualTo("JWT");
//        assertThat(getSecurityContexts(docket)).hasSize(1);
//        // Add more assertions as needed
//    }

    @Test
    void testGetApiInfo() throws Exception {
        // Arrange
        Method getApiInfoMethod = SwaggerConfig.class.getDeclaredMethod("getApiInfo");
        getApiInfoMethod.setAccessible(true);

        // Act
        ApiInfo apiInfo = (ApiInfo) getApiInfoMethod.invoke(swaggerConfig);

        // Assert
        assertThat(apiInfo).isNotNull();
        assertThat(apiInfo.getTitle()).isEqualTo("Leaps API");
        assertThat(apiInfo.getDescription()).isEqualTo("Clothes Rental Application");
        assertThat(apiInfo.getVersion()).isEqualTo("1.0.0");
        assertThat(apiInfo.getLicense()).isEqualTo("Apache 2.0");
        assertThat(apiInfo.getLicenseUrl()).isEqualTo("https://www.apache.org/licenses/LICENSE-2.0");

        Contact contact = apiInfo.getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Batch 4 Team 3");
        assertThat(contact.getUrl()).isEqualTo("https://github.com/that-mayank");
        assertThat(contact.getEmail()).isEqualTo("mayank.01@nineleaps.com");
    }

    @SuppressWarnings("unchecked")
    private List<SecurityScheme> getSecuritySchemes(Docket docket) {
        try {
            Method method = docket.getClass().getDeclaredMethod("getSecuritySchemes");
            method.setAccessible(true);
            return (List<SecurityScheme>) method.invoke(docket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access security schemes", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<SecurityContext> getSecurityContexts(Docket docket) {
        try {
            Method method = docket.getClass().getDeclaredMethod("getSecurityContexts");
            method.setAccessible(true);
            return (List<SecurityContext>) method.invoke(docket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access security contexts", e);
        }
    }
}
