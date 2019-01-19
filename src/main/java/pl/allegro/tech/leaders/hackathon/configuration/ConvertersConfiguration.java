package pl.allegro.tech.leaders.hackathon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.net.InetSocketAddress;
import java.util.List;

@Configuration
class ConvertersConfiguration {

    @Bean
    MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(inetSocketAddressWriterConverter(), inetSocketAddressReaderConverter()));
    }

    private Converter<InetSocketAddress, String> inetSocketAddressWriterConverter() {
        return new Converter<InetSocketAddress, String>() {
            @Override
            public String convert(InetSocketAddress source) {
                return source.toString();
            }
        };
    }

    private Converter<String, InetSocketAddress> inetSocketAddressReaderConverter() {
        return new Converter<String, InetSocketAddress>() {
            @Override
            public InetSocketAddress convert(String source) {
                var split = source.split(":");
                return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
            }
        };
    }

}