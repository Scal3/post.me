package com.herman.postme.tag.controller;

import com.herman.postme.tag.dto.TagDto;
import com.herman.postme.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/free/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<TagDto> getTop10TagsByUsage() {
        log.debug("Entering getTop10TagsByUsage method");

        List<TagDto> tags = tagService.getTop10TagsByUsage();

        log.debug("Exiting getTop10TagsByUsage method");

        return tags;
    }
}
