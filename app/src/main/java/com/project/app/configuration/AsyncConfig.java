package com.project.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
public class AsyncConfig {

    // pretty sure its ok empty, thats what the tutorial I used did.
    // source : https://www.baeldung.com/spring-async#:~:text=Enable%20Async%20Support,The%20enable%20annotation%20is%20enough.
}
