package com.herman.postme;

import com.herman.postme.comment.entity.Comment;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.dto.PostDtoWithCommentQuantity;
import com.herman.postme.post.dto.PostDtoWithComments;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post_rate.entity.PostRate;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class PostMeConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<List<PostRate>, Integer> postsLikesConverter =
                context -> {
                    if (context.getSource() == null) return 0;

                    return context.getSource()
                            .stream()
                            .mapToInt(PostRate::getRate)
                            .sum();
                };

        Converter<List<Comment>, Integer> commentsQuantityConverter =
                (MappingContext<List<Comment>, Integer> context) -> {
                    List<Comment> comments = context.getSource();
                    return comments != null ? comments.size() : 0;
                };

        TypeMap<Post, PostDtoWithComments> postToPostDtoWithCommentsMapper =
                modelMapper.createTypeMap(Post.class, PostDtoWithComments.class);

        postToPostDtoWithCommentsMapper.addMappings(
                mapper -> mapper.using(postsLikesConverter)
                        .map(Post::getRates, PostDtoWithComments::setRate)
        );

        TypeMap<Post, PostDto> postToPostDtoMapper =
                modelMapper.createTypeMap(Post.class, PostDto.class);

        postToPostDtoMapper.addMappings(
                mapper -> mapper.using(postsLikesConverter)
                        .map(Post::getRates, PostDto::setRate)
        );

        TypeMap<Post, PostDtoWithCommentQuantity> postToPostDtoWithCommentQuantityMapper =
                modelMapper.createTypeMap(Post.class, PostDtoWithCommentQuantity.class);

        postToPostDtoWithCommentQuantityMapper.addMappings(
                mapper -> mapper.using(postsLikesConverter)
                        .map(Post::getRates, PostDtoWithCommentQuantity::setRate)
        );

        postToPostDtoWithCommentQuantityMapper.addMappings(
                mapper -> mapper.using(commentsQuantityConverter)
                        .map(Post::getComments, PostDtoWithCommentQuantity::setCommentsQuantity)
        );

        return modelMapper;
    }
}
