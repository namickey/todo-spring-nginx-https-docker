package com.example.demo.core.logs;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbackAccessConfig {
@Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> logbackAccessValve() {
        return factory -> factory.addContextCustomizers(context -> {
            LogbackValve valve = new LogbackValve();
            // クラスパス上の logback-access.xml を読む
            valve.setFilename("logback-access.xml");
            // async=true で非同期書き込み（遅延少し増, スループット向上）
            valve.setAsyncSupported(true);
            context.getPipeline().addValve(valve);
        });
    }
}
