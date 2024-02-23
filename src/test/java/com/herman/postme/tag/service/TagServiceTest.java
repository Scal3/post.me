package com.herman.postme.tag.service;

import com.herman.postme.post.dto.CreatePostDto;
import com.herman.postme.post.service.PostService;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
import com.herman.postme.tag.dto.TagDto;
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.tag.repository.TagRepository;
import com.herman.postme.user.entity.User;
import com.herman.postme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TagServiceTest {

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_TAG_SECOND = "second";

    private static final String MOCK_TAG_THIRD = "third";

    private static final String MOCK_TAG_FORTH = "forth";

    private static final String MOCK_TAG_FIFTH = "fifth";

    private final ModelMapper modelMapper;

    private final TagRepository tagRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TagService tagService;

    private final RoleService roleService;

    private final PostService postService;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

        addMockTagToDB(MOCK_TAG_FIRST);
        addMockTagToDB(MOCK_TAG_SECOND);
        addMockTagToDB(MOCK_TAG_THIRD);
        addMockTagToDB(MOCK_TAG_FORTH);
        addMockTagToDB(MOCK_TAG_FIFTH);

        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                userRole.getName()
        );
    }

    private Tag addMockTagToDB(String tagName) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tagName);

        return tagRepository.save(tagEntity);
    }

    private User addMockUserToDB(
            String email, String login, String password, LocalDateTime createdAt, Role role
    ) {
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setLogin(login);
        mockUser.setPasswordHash(passwordEncoder.encode(password));
        mockUser.setCreatedAt(createdAt);
        mockUser.setRole(role);

        return userRepository.save(mockUser);
    }

    private void setAuthenticationToMockUser(String email, String passwordHash, String roleName) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        email,
                        passwordHash,
                        Collections.singletonList(new SimpleGrantedAuthority(roleName))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    public void getTagOrSaveIt_new_tag_case() {
        String newTagName = "newTag";
        Tag createdTag = tagService.getTagOrSaveIt(newTagName);

        assertEquals(newTagName, createdTag.getName());
    }

    @Test
    public void getTagOrSaveIt_existed_tag_case() {
        Tag existedTag = tagService.getTagOrSaveIt(MOCK_TAG_THIRD);

        assertEquals(MOCK_TAG_THIRD, existedTag.getName());
    }

    @Test
    public void getTagOrSaveIt_tagName_is_blank_case() {
        assertThrows(RuntimeException.class, () -> tagService.getTagOrSaveIt(""));
    }

    @Test
    public void getAllTags_normal_case() {
        List<TagDto> tags = tagService.getAllTags(0 , 10);

        assertEquals(MOCK_TAG_FIRST, tags.get(0).getName());
        assertEquals(MOCK_TAG_SECOND, tags.get(1).getName());
        assertEquals(MOCK_TAG_THIRD, tags.get(2).getName());
        assertEquals(MOCK_TAG_FORTH, tags.get(3).getName());
        assertEquals(MOCK_TAG_FIFTH, tags.get(4).getName());
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getAllTags_no_tags_in_db_case() {
        List<TagDto> tags = tagService.getAllTags(0 , 10);

        assertEquals(0, tags.size());
    }

    @Test
    public void getTop10TagsByUsage_normal_case() {
        CreatePostDto post1 = new CreatePostDto();
        post1.setHeading("post1");
        post1.setText("post1 text wow");
        post1.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post2 = new CreatePostDto();
        post2.setHeading("post2");
        post2.setText("post2 text wow");
        post2.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post3 = new CreatePostDto();
        post3.setHeading("post3");
        post3.setText("post3 text wow");
        post3.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_FIRST));

        CreatePostDto post4 = new CreatePostDto();
        post4.setHeading("post4");
        post4.setText("post4 text wow");
        post4.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_SECOND));

        CreatePostDto post5 = new CreatePostDto();
        post5.setHeading("post5");
        post5.setText("post5 text wow");
        post5.setTags(List.of(MOCK_TAG_FIFTH, MOCK_TAG_SECOND));

        postService.createPost(post1);
        postService.createPost(post2);
        postService.createPost(post3);
        postService.createPost(post4);
        postService.createPost(post5);

        List<TagDto> tags = tagService.getTop10TagsByUsage();

        assertEquals(MOCK_TAG_FIFTH, tags.get(0).getName());
        assertEquals(MOCK_TAG_FIRST, tags.get(1).getName());
        assertEquals(MOCK_TAG_SECOND, tags.get(2).getName());
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getTop10TagsByUsage_no_tags_in_db_case() {
        List<TagDto> tags = tagService.getTop10TagsByUsage();

        assertEquals(0, tags.size());
    }
}