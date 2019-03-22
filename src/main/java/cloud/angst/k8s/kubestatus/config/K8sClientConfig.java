package cloud.angst.k8s.kubestatus.config;

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class K8sClientConfig {
    @Bean
    @Profile("local")
    public KubeConfig createFromProxy() {
        return KubeConfig.fromUrl("http://127.0.0.1:8001");
    }

    @Bean
    @SneakyThrows
    @Profile("!local")
    public KubeConfig createFromCluster() {
        return KubeConfig.fromCluster();
    }

    @Bean
    @SneakyThrows
    public RestTemplate restTemplate() {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
    }
}
