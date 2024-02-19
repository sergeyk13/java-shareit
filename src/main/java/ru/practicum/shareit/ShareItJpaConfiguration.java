package ru.practicum.shareit;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.practicum.shareit")
@EntityScan(basePackages = "ru.practicum.shareit")
public class ShareItJpaConfiguration {
}
