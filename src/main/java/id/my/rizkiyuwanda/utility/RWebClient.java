package id.my.rizkiyuwanda.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RWebClient {

    @Bean
    public WebClient getWebClient(){
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080")
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("admin@gmail.com", "admin"))
                .build();
        return webClient;
    }
}
