package com.herman.postme.tag.controller;

import com.herman.postme.tag.dto.TagDto;
import com.herman.postme.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/secured/tags")
@RequiredArgsConstructor
public class TagControllerSecured {

    @Value("${limit.size.constraint}")
    private int limitSizeConstraint;

    private final TagService tagService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<TagDto> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.debug("Entering getAllTags method");
        log.debug("Got {} value as page argument, {} value as limit argument", page, limit);

        if (limit > limitSizeConstraint) {
            limit = limitSizeConstraint;
            log.debug("Param limit is too big, setting up to {}", limitSizeConstraint);
        }

        List<TagDto> tags = tagService.getAllTags(page, limit);

        log.debug("Exiting getAllTags method");

        return tags;
    }
}
