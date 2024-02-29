package com.herman.postme.post.repository;

import com.herman.postme.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllByTagsOrderByCreatedAtDesc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllByTagsOrderByCreatedAtAsc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN p.comments c " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByTagsOrderByCommentsDesc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN p.comments c " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByTagsOrderByCommentsAsc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByTagsOrderByLikesDesc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN :tags " +
            "GROUP BY p.id " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByTagsOrderByLikesAsc(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllOrderByCommentsDesc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllOrderByCommentsAsc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "ORDER BY totalRate DESC")
    List<Post> findAllOrderByLikesDesc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "ORDER BY totalRate ASC")
    List<Post> findAllOrderByLikesAsc(Pageable pageable);

    List<Post> findAllByUserId(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.user.id = :userId " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByUserIdOrderByCommentCountDesc(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "WHERE p.user.id = :userId " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByUserIdOrderByCommentCountAsc(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "WHERE p.user.id = :userId " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByUserIdOrderByLikesDesc(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "WHERE p.user.id = :userId " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByUserIdOrderByLikesAsc(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithLikes(@Param("postId") long postId);
}
