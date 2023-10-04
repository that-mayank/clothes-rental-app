package com.nineleaps.leaps;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest(classes = LeapsApplication.class)

class LeapsApplicationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {

        // Ensure that the application context loads successfully

        SpringApplication application = new SpringApplication(LeapsApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        ConfigurableApplicationContext context = application.run();
        assertNotNull(context);
        context.close();
    }

    @Test
    void testMain() {
        SpringApplication applicationMock = Mockito.mock(SpringApplication.class);
        ConfigurableApplicationContext contextMock = Mockito.mock(ConfigurableApplicationContext.class);

        // Mocking the SpringApplication.run() call
        Mockito.when(applicationMock.run(any())).thenReturn(contextMock);

        // Call the main method
        LeapsApplication.main(new String[]{});

        // Verify that SpringApplication.run() was called
        Mockito.verify(applicationMock).run(any());
    }



    @Test

    void passwordEncoderBeanShouldExist() {
        assertNotNull(passwordEncoder);

    }

}