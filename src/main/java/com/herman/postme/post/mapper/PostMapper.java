package com.herman.postme.post.mapper;

import com.herman.postme.comment.entity.Comment;
import com.herman.postme.post.dto.PostDto;
import com.herman.postme.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final ModelMapper modelMapper;

    private final Converter<List<Comment>, Integer> CommentsQuantityConverter =
            (MappingContext<List<Comment>, Integer> context) -> {
                List<Comment> comments = context.getSource();
                return comments != null ? comments.size() : 0;
            };

    public PostMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.addConverter(CommentsQuantityConverter);
    }

    public List<PostDto> mapPostListToPostDtoList(List<Post> posts) {
        return this.modelMapper.map(posts, new TypeToken<List<PostDto>>() {}.getType());
    }
}
