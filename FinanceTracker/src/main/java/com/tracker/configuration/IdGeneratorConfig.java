package com.tracker.configuration;

import net.mguenther.idem.flake.Flake64L;
import net.mguenther.idem.provider.LinearTimeProvider;
import net.mguenther.idem.provider.StaticWorkerIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {
    @Bean
    public Flake64L flake64L() {
        return new Flake64L(new LinearTimeProvider(), new StaticWorkerIdProvider("0.0.0.0"));
    }
}
