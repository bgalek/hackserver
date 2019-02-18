package pl.allegro.tech.leaders.hackathon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Configuration
class ConvertersConfiguration {

    @Bean
    MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(inetSocketAddressWriterConverter(), inetSocketAddressReaderConverter()));
    }

    private Converter<InetAddress, String> inetSocketAddressWriterConverter() {
        return new Converter<InetAddress, String>() {
            @Override
            public String convert(InetAddress source) {
                return source.getHostAddress();
            }
        };
    }

    private Converter<String, InetAddress> inetSocketAddressReaderConverter() {
        return new Converter<String, InetAddress>() {
            @Override
            public InetAddress convert(String source) {
                try {
                    return InetAddress.getByName(source);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

}