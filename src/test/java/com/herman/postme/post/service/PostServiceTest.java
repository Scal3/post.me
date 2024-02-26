package com.herman.postme.post.service;

import com.herman.postme.exception.exceptionimp.ForbiddenException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.dto.*;
import com.herman.postme.post.entity.Post;
import com.herman.postme.tag.entity.Tag;
import com.herman.postme.post.enums.PostSortOrder;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
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
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostServiceTest {

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_POST_FIRST_HEADING = "post number 1";

    private static final String MOCK_POST_SECOND_HEADING = "post number 2";

    private static final String MOCK_POST_THIRD_HEADING = "post number 3";

    private static final String MOCK_POST_FORTH_HEADING = "post number 4";

    private static final String MOCK_POST_FIFTH_HEADING = "post number 5";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_TAG_SECOND = "second";

    private static final String MOCK_TAG_THIRD = "third";

    private static final String MOCK_TAG_FORTH = "forth";

    private static final String MOCK_TAG_FIFTH = "fifth";

    private final PostService postService;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
        Tag tagSecond = addMockTagToDB(MOCK_TAG_SECOND);
        Tag tagThird = addMockTagToDB(MOCK_TAG_THIRD);
        addMockTagToDB(MOCK_TAG_FORTH);
        addMockTagToDB(MOCK_TAG_FIFTH);

        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);

        User mockUser = addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        LocalDateTime wasCreatedFirst =
                LocalDateTime.of(2020, Month.JULY, 20, 12, 30);
        LocalDateTime wasCreatedSecond =
                LocalDateTime.of(2020, Month.JULY, 21, 12, 30);
        LocalDateTime wasCreatedThird =
                LocalDateTime.of(2020, Month.JULY, 22, 12, 30);
        LocalDateTime wasCreatedForth =
                LocalDateTime.of(2020, Month.JULY, 23, 12, 30);
        LocalDateTime wasCreatedFifth =
                LocalDateTime.of(2020, Month.JULY, 24, 12, 30);

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);
        Set<Tag> secondTagSet = new HashSet<>();
        secondTagSet.add(tagSecond);
        Set<Tag> thirdTagSet = new HashSet<>();
        thirdTagSet.add(tagThird);

        addMockPostsToDB(MOCK_POST_FIRST_HEADING, "post number 1", wasCreatedFirst, mockUser, firstTagSet);
        addMockPostsToDB(MOCK_POST_SECOND_HEADING, "post number 2", wasCreatedSecond, mockUser, firstTagSet);
        addMockPostsToDB(MOCK_POST_THIRD_HEADING, "post number 3", wasCreatedThird, mockUser, secondTagSet);
        addMockPostsToDB(MOCK_POST_FORTH_HEADING, "post number 4", wasCreatedForth, mockUser, secondTagSet);
        addMockPostsToDB(MOCK_POST_FIFTH_HEADING, "post number 5", wasCreatedFifth, mockUser, thirdTagSet);

        setAuthenticationToMockUser(
                MOCK_USER_EMAIL,
                passwordEncoder.encode(MOCK_USER_PASSWORD),
                userRole.getName()
        );
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

    private Tag addMockTagToDB(String tagName) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tagName);
        tagEntity.setPosts(new ArrayList<>());

        return tagRepository.save(tagEntity);
    }

    private Post addMockPostsToDB(
            String heading, String text, LocalDateTime createdAt,
            User user, Set<Tag> tags
            ) {
        Post post = new Post();
        post.setHeading(heading);
        post.setText(text);
        post.setCreatedAt(createdAt);
        post.setUser(user);

        tags.forEach(tag -> tag.getPosts().add(post));

        return postRepository.save(post);
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
    public void get_all_posts_date_fresher_sort_no_tags_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, null, PostSortOrder.DATE_FRESHER);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FIRST_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_all_posts_date_older_sort_no_tags_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, null, PostSortOrder.DATE_OLDER);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_FIRST_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_all_posts_likes_more_sort_no_tags_case() {
        postService.likePost(3);

        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, null, PostSortOrder.LIKES_MORE);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
    }

    @Test
    public void get_all_posts_likes_less_sort_no_tags_case() {
        postService.likePost(3);

        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, null, PostSortOrder.LIKES_LESS);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_all_posts_page_0_limit_2_no_tags_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 2, null, PostSortOrder.DATE_FRESHER);

        assertEquals(2, posts.size());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FORTH_HEADING, posts.get(1).getHeading());
    }

    @Test
    public void get_all_posts_page_1_limit_2_no_tags_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(1, 2, null, PostSortOrder.DATE_FRESHER);

        assertEquals(2, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_SECOND_HEADING, posts.get(1).getHeading());
    }

    @Test
    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    public void get_all_posts_no_posts_in_db_no_tags_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, null, PostSortOrder.DATE_FRESHER);

        assertEquals(0, posts.size());
    }











    @Test
    public void get_all_posts_date_fresher_sort_with_tags_case() {
        List<String> tags = List.of(MOCK_TAG_FIRST);
        List<PostDtoWithCommentQuantity> posts =
                postService.getAllPosts(0, 15, tags, PostSortOrder.DATE_FRESHER);

        assertEquals(2, posts.size());
    }

//    @Test
//    public void get_all_posts_date_older_sort_with_tags_case() {
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(0, 15, PostSortOrder.DATE_OLDER);
//
//        assertEquals(5, posts.size());
//        assertEquals(MOCK_POST_FIRST_HEADING, posts.get(0).getHeading());
//        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(4).getHeading());
//    }
//
//    @Test
//    public void get_all_posts_likes_more_sort_with_tags_case() {
//        postService.likePost(3);
//
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(0, 15, PostSortOrder.LIKES_MORE);
//
//        assertEquals(5, posts.size());
//        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
//    }
//
//    @Test
//    public void get_all_posts_likes_less_sort_with_tags_case() {
//        postService.likePost(3);
//
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(0, 15, PostSortOrder.LIKES_LESS);
//
//        assertEquals(5, posts.size());
//        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(4).getHeading());
//    }
//
//    @Test
//    public void get_all_posts_page_0_limit_2_with_tags_case() {
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(0, 2, PostSortOrder.DATE_FRESHER);
//
//        assertEquals(2, posts.size());
//        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(0).getHeading());
//        assertEquals(MOCK_POST_FORTH_HEADING, posts.get(1).getHeading());
//    }
//
//    @Test
//    public void get_all_posts_page_1_limit_2_with_tags_case() {
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(1, 2, PostSortOrder.DATE_FRESHER);
//
//        assertEquals(2, posts.size());
//        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
//        assertEquals(MOCK_POST_SECOND_HEADING, posts.get(1).getHeading());
//    }
//
//    @Test
//    @Tag("excludeBeforeEach")
//    public void get_all_posts_no_posts_in_db_with_tags_case() {
//        List<PostDtoWithCommentQuantity> posts =
//                postService.getAllPosts(0, 15, PostSortOrder.DATE_FRESHER);
//
//        assertEquals(0, posts.size());
//    }
//













    @Test
    public void get_one_post_by_id_normal_case() {
        PostDtoWithComments post = postService.getOnePostById(1L);

        assertEquals(MOCK_POST_FIRST_HEADING, post.getHeading());
    }

    @Test
    public void get_one_post_by_id_post_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> {
            postService.getOnePostById(1000L);
        });
    }

    @Test
    public void get_users_post_by_id_date_fresher_sort_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 0, 15, PostSortOrder.DATE_FRESHER);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FIRST_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_users_post_by_id_date_older_sort_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 0, 15, PostSortOrder.DATE_OLDER);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_FIRST_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_users_post_by_id_likes_more_sort_case() {
        postService.likePost(3);

        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 0, 15, PostSortOrder.LIKES_MORE);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
    }

    @Test
    public void get_users_post_by_id_likes_less_sort_case() {
        postService.likePost(3);

        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 0, 15, PostSortOrder.LIKES_LESS);

        assertEquals(5, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(4).getHeading());
    }

    @Test
    public void get_users_post_by_id_page_0_limit_2_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 0, 2, PostSortOrder.DATE_FRESHER);

        assertEquals(2, posts.size());
        assertEquals(MOCK_POST_FIFTH_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_FORTH_HEADING, posts.get(1).getHeading());
    }

    @Test
    public void get_users_post_by_id_page_1_limit_2_case() {
        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(2, 1, 2, PostSortOrder.DATE_FRESHER);

        assertEquals(2, posts.size());
        assertEquals(MOCK_POST_THIRD_HEADING, posts.get(0).getHeading());
        assertEquals(MOCK_POST_SECOND_HEADING, posts.get(1).getHeading());
    }

    @Test
    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    public void get_users_post_by_id_no_posts_in_db_case() {
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User user = addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        List<PostDtoWithCommentQuantity> posts =
                postService.getUsersPostById(user.getId(), 0, 15, PostSortOrder.DATE_FRESHER);

        assertEquals(0, posts.size());
    }

    @Test
    public void get_users_post_by_id_user_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> {
            postService.getUsersPostById(1000, 0, 15, PostSortOrder.DATE_FRESHER);
        });
    }

//    @Test
//    public void create_post_normal_case() {
//        String heading = "test heading";
//        String text = "test post's text";
//
//        CreatePostDto dto = new CreatePostDto();
//        dto.setHeading(heading);
//        dto.setText(text);
//
//        PostDto postDto = postService.createPost(dto);
//
//        assertEquals(heading, postDto.getHeading());
//        assertEquals(text, postDto.getText());
//    }
//
//    @Test
//    public void update_post_normal_case() {
//        long id = 1;
//        String heading = "Updated " + MOCK_POST_FIRST_HEADING;
//        String text = "Updated " + MOCK_POST_FIRST_HEADING;
//
//        PostDtoWithComments postForUpdate = postService.getOnePostById(id);
//
//        assertEquals(MOCK_POST_FIRST_HEADING, postForUpdate.getHeading());
//
//        UpdatePostDto dto = new UpdatePostDto();
//        dto.setId(id);
//        dto.setHeading(heading);
//        dto.setText(text);
//
//        PostDto updatedPost = postService.updatePost(dto);
//
//        assertEquals(id, updatedPost.getId());
//        assertEquals(heading, updatedPost.getHeading());
//        assertEquals(text, updatedPost.getText());
//        assertTrue(updatedPost.getIsUpdated());
//    }
//
//    @Test
//    public void update_post_post_is_not_found_case() {
//        long notFoundId = 1000;
//        String heading = "Updated post";
//        String text = "Updated posts text";
//
//        UpdatePostDto dto = new UpdatePostDto();
//        dto.setId(notFoundId);
//        dto.setHeading(heading);
//        dto.setText(text);
//
//        assertThrows(NotFoundException.class, () -> {
//            postService.updatePost(dto);
//        });
//    }
//
//    @Test
//    public void update_post_user_is_not_authorized_to_update_post_case() {
//        // Other user created a post
//        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
//        User newUser = addMockUserToDB(
//                "newUser@gmail.com",
//                "newUserLogin",
//                "newUserPassword",
//                LocalDateTime.now(),
//                userRole
//        );
//
//        Post post = addMockPostsToDB(
//                "Simple post name",
//                "Simple post text",
//                LocalDateTime.now(),
//                newUser
//        );
//
//        // And we're trying to change it with our token
//        String heading = "Updated post";
//        String text = "Updated posts text";
//
//        UpdatePostDto dto = new UpdatePostDto();
//        dto.setId(post.getId());
//        dto.setHeading(heading);
//        dto.setText(text);
//
//        assertThrows(ForbiddenException.class, () -> {
//            postService.updatePost(dto);
//        });
//    }

    @Test
    public void delete_post_normal_case() {
        postService.deletePost(1);

        assertThrows(NotFoundException.class, () -> {
            postService.getOnePostById(1);
        });
    }

    @Test
    public void delete_post_post_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> {
            postService.deletePost(1000);
        });
    }

//    @Test
//    public void delete_post_user_is_not_authorized_to_delete_post_case() {
//        // Other user created a post
//        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
//        User newUser = addMockUserToDB(
//                "newUser@gmail.com",
//                "newUserLogin",
//                "newUserPassword",
//                LocalDateTime.now(),
//                userRole
//        );
//
//        Post post = addMockPostsToDB(
//                "Simple post name",
//                "Simple post text",
//                LocalDateTime.now(),
//                newUser
//        );
//
//        // And we're trying to delete it with our token
//        assertThrows(ForbiddenException.class, () -> {
//            postService.deletePost(post.getId());
//        });
//    }

    @Test
    public void like_post_normal_case() {
        PostDto likedPost = postService.likePost(3);

        assertEquals(1, likedPost.getRate());
    }

    @Test
    public void like_post_post_has_users_like_already_case() {
        PostDto likedPost = postService.likePost(3);
        assertEquals(1, likedPost.getRate());

        PostDto doubleLikedPost = postService.likePost(3);
        assertEquals(0, doubleLikedPost.getRate());
    }

    @Test
    public void like_post_post_has_users_dislike_already_case() {
        PostDto dislikedPost = postService.dislikePost(3);
        assertEquals(-1, dislikedPost.getRate());

        PostDto likedPost = postService.likePost(3);
        assertEquals(1, likedPost.getRate());
    }

    @Test
    public void like_post_post_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> postService.likePost(1000));
    }

    @Test
    public void dislike_post_normal_case() {
        PostDto dislikedPost = postService.dislikePost(3);

        assertEquals(-1, dislikedPost.getRate());
    }

    @Test
    public void dislike_post_post_has_users_like_already_case() {
        PostDto likedPost = postService.likePost(3);
        assertEquals(1, likedPost.getRate());

        PostDto dislikedPost = postService.dislikePost(3);
        assertEquals(-1, dislikedPost.getRate());
    }

    @Test
    public void dislike_post_post_has_users_dislike_already_case() {
        PostDto dislikedPost = postService.dislikePost(3);
        assertEquals(-1, dislikedPost.getRate());

        PostDto doubleDislikedPost = postService.dislikePost(3);
        assertEquals(0, doubleDislikedPost.getRate());
    }

    @Test
    public void dislike_post_post_not_found_case() {
        assertThrows(NotFoundException.class, () -> postService.dislikePost(1000));
    }
}