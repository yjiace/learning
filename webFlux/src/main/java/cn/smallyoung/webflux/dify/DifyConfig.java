package cn.smallyoung.webflux.dify;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author smallyoung
 * @date 2025/6/9
 */
@Configuration
public class DifyConfig {

    @Bean
    public DifyClient difyClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(WebClient.builder().build())).build();
        return httpServiceProxyFactory.createClient(DifyClient.class);
    }

}
