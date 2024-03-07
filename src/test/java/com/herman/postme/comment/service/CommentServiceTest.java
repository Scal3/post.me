package com.herman.postme.comment.service;

import com.herman.postme.comment.dto.CommentDto;
import com.herman.postme.comment.dto.CreateCommentDto;
import com.herman.postme.comment.entity.Comment;
import com.herman.postme.comment.enums.CommentSortOrder;
import com.herman.postme.comment.repository.CommentRepository;
import com.herman.postme.exception.exceptionimp.ForbiddenException;
import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.post.entity.Post;
import com.herman.postme.post.repository.PostRepository;
import com.herman.postme.role.entity.Role;
import com.herman.postme.role.service.RoleService;
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
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// I added H2 in maven test scope for tests
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceTest {

    private static final String MOCK_USER_EMAIL = "user1@user.com";

    private static final String MOCK_USER_LOGIN = "user12345";

    private static final String MOCK_USER_PASSWORD = "user12345";

    private static final String MOCK_TAG_FIRST = "first";

    private static final String MOCK_POST_FIRST_HEADING = "post number 1";

    private static final String MOCK_COMMENT_FIRST_TEXT = "comment number 1";

    private static final String MOCK_COMMENT_SECOND_TEXT = "comment number 2";

    private static final String MOCK_COMMENT_THIRD_TEXT = "comment number 3";

    private final PostRepository postRepository;

    private final CommentService commentService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final CommentRepository commentRepository;

    @BeforeEach
    public void setup(TestInfo info) {
        boolean isExcludeSetup = info.getTags()
                .stream()
                .anyMatch((tag) -> tag.equals("excludeBeforeEach"));

        if (isExcludeSetup) return;

        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User mockUser = addMockUserToDB(
                MOCK_USER_EMAIL,
                MOCK_USER_LOGIN,
                MOCK_USER_PASSWORD,
                LocalDateTime.now(),
                userRole
        );

        LocalDateTime postWasCreatedFirst =
                LocalDateTime.of(2020, Month.JULY, 20, 12, 30);

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);

        Post mockPost = addMockPostsToDB(
                MOCK_POST_FIRST_HEADING, "post number 1", postWasCreatedFirst, mockUser, firstTagSet
        );

        LocalDateTime commentWasCreatedFirst = postWasCreatedFirst.plusDays(1);
        LocalDateTime commentWasCreatedSecond = postWasCreatedFirst.plusDays(2);
        LocalDateTime commentWasCreatedThird = postWasCreatedFirst.plusDays(3);

        addMockCommentToDB(MOCK_COMMENT_FIRST_TEXT, commentWasCreatedFirst, mockPost, mockUser);
        addMockCommentToDB(MOCK_COMMENT_SECOND_TEXT, commentWasCreatedSecond, mockPost, mockUser);
        addMockCommentToDB(MOCK_COMMENT_THIRD_TEXT, commentWasCreatedThird, mockPost, mockUser);

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

        post.setTags(tags);

        return postRepository.save(post);
    }

    private Comment addMockCommentToDB(String text, LocalDateTime createdAt, Post post, User user) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreatedAt(createdAt);
        comment.setPost(post);
        comment.setUser(user);

        return commentRepository.save(comment);
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
    public void getAllPostComments_date_fresher_sort_case() {
        List<CommentDto> comments =
                commentService.getAllPostComments(1, 0 , 15, CommentSortOrder.DATE_FRESHER);

        assertEquals(3, comments.size());
        assertEquals(MOCK_COMMENT_THIRD_TEXT, comments.get(0).getText());
        assertEquals(MOCK_COMMENT_SECOND_TEXT, comments.get(1).getText());
        assertEquals(MOCK_COMMENT_FIRST_TEXT, comments.get(2).getText());
    }

    @Test
    public void getAllPostComments_date_older_sort_case() {
        List<CommentDto> comments =
                commentService.getAllPostComments(1, 0 , 15, CommentSortOrder.DATE_OLDER);

        assertEquals(3, comments.size());
        assertEquals(MOCK_COMMENT_FIRST_TEXT, comments.get(0).getText());
        assertEquals(MOCK_COMMENT_SECOND_TEXT, comments.get(1).getText());
        assertEquals(MOCK_COMMENT_THIRD_TEXT, comments.get(2).getText());
    }

    @Test
    public void getAllPostComments_likes_more_sort_case() {
        commentService.likeComment(1);

        List<CommentDto> comments =
                commentService.getAllPostComments(1, 0 , 15, CommentSortOrder.LIKES_MORE);

        assertEquals(3, comments.size());
        assertEquals(MOCK_COMMENT_FIRST_TEXT, comments.get(0).getText());
    }

    @Test
    public void getAllPostComments_likes_less_sort_case() {
        commentService.likeComment(1);

        List<CommentDto> comments =
                commentService.getAllPostComments(1, 0 , 15, CommentSortOrder.LIKES_LESS);

        assertEquals(3, comments.size());
        assertEquals(MOCK_COMMENT_FIRST_TEXT, comments.get(2).getText());
    }

    @Test
    public void getAllPostComments_post_id_is_not_found_case() {
        assertThrows(NotFoundException.class,
                () -> commentService.getAllPostComments(1000, 0 , 15, CommentSortOrder.DATE_OLDER));
    }

    @org.junit.jupiter.api.Tag("excludeBeforeEach")
    @Test
    public void getAllPostComments_no_comments_in_db_case() {
        Tag tagFirst = addMockTagToDB(MOCK_TAG_FIRST);
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

        Set<Tag> firstTagSet = new HashSet<>();
        firstTagSet.add(tagFirst);

        Post mockPost =
                addMockPostsToDB(MOCK_POST_FIRST_HEADING, "post number 1", wasCreatedFirst, mockUser, firstTagSet);

        List<CommentDto> comments = commentService.getAllPostComments(
                mockPost.getId(), 0 , 15, CommentSortOrder.DATE_FRESHER);

        assertEquals(0, comments.size());
    }

    @Test
    public void createComment_normal_case() {
        String commentText = "new comment wow!!!";
        long postId = 1;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);
        createCommentDto.setPostId(postId);

        CommentDto commentDto = commentService.createComment(createCommentDto);

        assertEquals(commentText, commentDto.getText());
        assertEquals(MOCK_USER_LOGIN, commentDto.getUsername());

    }

    @Test
    public void createComment_post_is_not_found_case() {
        String commentText = "new comment wow!!!";
        long notFoundPostId = 1000;

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText(commentText);
        createCommentDto.setPostId(notFoundPostId);

        assertThrows(NotFoundException.class, () -> commentService.createComment(createCommentDto));
    }

    @Test
    public void deleteComment_normal_case() {
        long mockCommentFirstId = 1;

        commentService.deleteComment(mockCommentFirstId);

        List<CommentDto> comments =
                commentService.getAllPostComments(1, 0 , 15, CommentSortOrder.DATE_FRESHER);

        int sizeAfterDeleting = 2;

        assertEquals(sizeAfterDeleting, comments.size());
    }

    @Test
    public void deleteComment_comment_is_not_found_case() {
        assertThrows(NotFoundException.class, () -> commentService.deleteComment(1000));
    }

    @Test
    public void deleteComment_comment_user_is_not_authorized_to_delete_comment_case() {
        // Other user created a post and a comment
        Role userRole = modelMapper.map(roleService.getUserRole(), Role.class);
        User newUser = addMockUserToDB(
                "newUser@gmail.com",
                "newUserLogin",
                "newUserPassword",
                LocalDateTime.now(),
                userRole
        );

        Tag tag = addMockTagToDB("wow");

        Post newPost = addMockPostsToDB(
                "Simple post name",
                "Simple post text",
                LocalDateTime.now(),
                newUser,
                Set.of(tag)
        );

        Comment newComment =
                addMockCommentToDB("My new amazing comment!", LocalDateTime.now(), newPost, newUser);

        // And we're trying to delete the comment with our token
        assertThrows(ForbiddenException.class, () -> {
            commentService.deleteComment(newComment.getId());
        });
    }

    @Test
    public void likeComment_normal_case() {
        long postId = 1;
        int like = 1;
        CommentDto likedComment = commentService.likeComment(postId);

        assertEquals(like, likedComment.getRate());
    }

    @Test
    public void likeComment_comment_has_users_like_already_case() {
        long postId = 1;
        int like = 1;
        int noRate = 0;

        CommentDto likedComment = commentService.likeComment(postId);
        assertEquals(like, likedComment.getRate());

        CommentDto doubleLikedComment = commentService.likeComment(postId);
        assertEquals(noRate, doubleLikedComment.getRate());
    }

    @Test
    public void likeComment_comment_has_users_dislike_already_case() {
        long postId = 1;
        int like = 1;
        int dislike = -1;

        CommentDto dislikedComment = commentService.dislikeComment(postId);
        assertEquals(dislike, dislikedComment.getRate());

        CommentDto doubleLikedComment = commentService.likeComment(postId);
        assertEquals(like, doubleLikedComment.getRate());
    }

    @Test
    public void likeComment_comment_is_not_found_case() {
        long notFoundPostId = 1000;
        assertThrows(NotFoundException.class, () -> commentService.likeComment(notFoundPostId));
    }

    @Test
    public void dislikeComment_normal_case() {
        long postId = 1;
        int dislike = -1;

        CommentDto dislikedComment = commentService.dislikeComment(postId);
        assertEquals(dislike, dislikedComment.getRate());
    }

    @Test
    public void dislikeComment_comment_has_users_dislike_already_case() {
        long postId = 1;
        int dislike = -1;
        int noRate = 0;

        CommentDto dislikedComment = commentService.dislikeComment(postId);
        assertEquals(dislike, dislikedComment.getRate());

        CommentDto doubleDislikedComment = commentService.dislikeComment(postId);
        assertEquals(noRate, doubleDislikedComment.getRate());
    }

    @Test
    public void dislikeComment_comment_has_users_like_already_case() {
        long postId = 1;
        int like = 1;
        int dislike = -1;

        CommentDto doubleLikedComment = commentService.likeComment(postId);
        assertEquals(like, doubleLikedComment.getRate());

        CommentDto dislikedComment = commentService.dislikeComment(postId);
        assertEquals(dislike, dislikedComment.getRate());
    }

    @Test
    public void dislikeComment_comment_is_not_found_case() {
        long notFoundPostId = 1000;
        assertThrows(NotFoundException.class, () -> commentService.dislikeComment(notFoundPostId));
    }
}