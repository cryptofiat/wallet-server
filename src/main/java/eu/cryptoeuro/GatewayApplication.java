package eu.cryptoeuro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@SpringBootApplication
@EnableAutoConfiguration
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        ProtobufHttpMessageConverter protobufHttpMessageConverter = new ProtobufHttpMessageConverter();
/*
        MediaType BITCOIN_PAYMENTREQUEST = new MediaType("application", "bitcoin-paymentrequest", Charset.forName("UTF-8"));
        protobufHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(BITCOIN_PAYMENTREQUEST, ProtobufHttpMessageConverter.PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
*/
        return protobufHttpMessageConverter;
    }

}
