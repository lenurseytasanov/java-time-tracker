package edu.spring.javatimetracker.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Duration taskTtl;

    private TimeZone timeZone;
}
