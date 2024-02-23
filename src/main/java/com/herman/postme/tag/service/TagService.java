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

    public Tag getTagOrSaveIt(String tagName) throws RuntimeException {
        if (tagName.isBlank()) throw new RuntimeException("Tag name is blank");

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

    public List<TagDto> getAllTags(int page, int limit) {
        try {
            log.debug("Entering getAllTags method");
            log.debug("Got page value {} and limit value {}", page, limit);

            Pageable pageable = PageRequest.of(page, limit);
            List<Tag> tags = tagRepository.findTagsSortedByAmountOfUsage(pageable);
            log.debug("Db return result {}", tags);

            List<TagDto> mappedTags = mapper.map(tags, new TypeToken<List< TagDto >>() {}.getType());
            log.debug("Mapping List<Tag> to List<TagDto> {}", mappedTags);
            log.debug("Exiting getAllTags method");

            return mappedTags;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getAllTags method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public List<TagDto> getTop10TagsByUsage() {
        try {
            log.debug("Entering getTop10TagsByUsage method");

            Pageable pageable = PageRequest.of(0, 10);
            List<Tag> tags = tagRepository.findTagsSortedByAmountOfUsage(pageable);
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
