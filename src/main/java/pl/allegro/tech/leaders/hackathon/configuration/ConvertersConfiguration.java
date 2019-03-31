package pl.allegro.tech.leaders.hackathon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.net.InetAddress;
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
                return String.format("%s:%d", source.getAddress().getCanonicalHostName(), source.getPort());
            }
        };
    }

    private Converter<String, InetSocketAddress> inetSocketAddressReaderConverter() {
        return new Converter<String, InetSocketAddress>() {
            @Override
            public InetSocketAddress convert(String source) {
                try {
                    String[] parts = source.split(":");
                    InetAddress host = InetAddress.getByName(parts[0]);
                    int port = Integer.parseInt(parts[1]);
                    return new InetSocketAddress(host, port);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

}