package com.herman.postme.tag.service;

import com.herman.postme.tag.entity.Tag;
import com.herman.postme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

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
}
