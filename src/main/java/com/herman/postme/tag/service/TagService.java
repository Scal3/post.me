package com.herman.postme.tag.service;

import com.herman.postme.exception.exceptionimp.InternalServerException;
import com.herman.postme.tag.dto.TagDto;
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final ModelMapper mapper;

    public Tag getTagOrSaveIt(String tagName) {
        Optional<Tag> optionalTag = tagRepository.findOneByName(tagName);

        if (optionalTag.isPresent()) {
            return optionalTag.get();
        } else {
            Tag newTag = new Tag();
            newTag.setName(tagName);
            newTag.setPosts(new ArrayList<>());

            return tagRepository.save(newTag);
        }
    }

    public List<TagDto> getTop10TagsByUsage() {
        try {
            log.debug("Entering getTop10TagsByUsage method");

            Pageable pageable = PageRequest.of(0, 10);
            List<Tag> tags = tagRepository.find10TagsByAmountOfUsage(pageable);
            log.debug("Db return result {}", tags);

            List<TagDto> mappedTags = mapper.map(tags, new TypeToken<List< TagDto >>() {}.getType());
            log.debug("Mapping List<Tag> to List<TagDto> {}", mappedTags);
            log.debug("Exiting getTop10TagsByUsage method");

            return mappedTags;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getTop10TagsByUsage method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
