package com.herman.postme;

import com.herman.postme.post_rate.entity.PostRate;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PostMeConfig {

    @Bean
    public ModelMapper modelMapper() {
        Converter<List<PostRate>, Integer> PostsLikesConverter =
                (MappingContext<List<PostRate>, Integer> context) -> {
                    List<PostRate> rates = context.getSource();
                    return rates != null ? rates.size() : 0;
                };

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(PostsLikesConverter);

        return modelMapper;
    }

}
