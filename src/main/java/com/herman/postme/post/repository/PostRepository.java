package com.herman.postme.post.repository;

import com.herman.postme.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllByTagsWithCommentsOrderByCreatedAtDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllByTagsWithRatesOrderByCreatedAtDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllByTagsWithCommentsOrderByCreatedAtAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllByTagsWithRatesOrderByCreatedAtAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByTagsWithCommentsOrderByCommentsDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByTagsWithRatesOrderByCommentsDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByTagsWithCommentsOrderByCommentsAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "INNER JOIN p.tags t " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByTagsWithRatesOrderByCommentsAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByTagsWithCommentsOrderByLikesDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByTagsWithRatesOrderByLikesDesc(List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByTagsWithCommentsOrderByLikesAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "INNER JOIN p.tags t " +
            "WHERE t.name IN ?1 " +
            "GROUP BY p.id " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByTagsWithRatesOrderByLikesAsc(List<String> tags, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllWithCommentsOrderByCommentsDesc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllWithRatesOrderByCommentsDesc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllWithCommentsOrderByCommentsAsc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllWithRatesOrderByCommentsAsc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "ORDER BY totalRate DESC")
    List<Post> findAllWithCommentsOrderByLikesDesc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "LEFT JOIN p.comments c " +
            "ORDER BY totalRate DESC")
    List<Post> findAllWithRatesOrderByLikesDesc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "ORDER BY totalRate ASC")
    List<Post> findAllWithCommentsOrderByLikesAsc(Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "LEFT JOIN p.comments c " +
            "ORDER BY totalRate ASC")
    List<Post> findAllWithRatesOrderByLikesAsc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllWithCommentsOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllWithRatesOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllWithCommentsOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllWithRatesOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllByUserIdWithCommentsOrderByCreatedAtDesc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllByUserIdWithRatesOrderByCreatedAtDesc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllByUserIdWithCommentsOrderByCreatedAtAsc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY p.createdAt ASC")
    List<Post> findAllByUserIdWithRatesOrderByCreatedAtAsc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByUserIdWithCommentsOrderByCommentCountDesc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) DESC")
    List<Post> findAllByUserIdWithRatesOrderByCommentCountDesc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByUserIdWithCommentsOrderByCommentCountAsc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN p.comments c " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(c) ASC")
    List<Post> findAllByUserIdWithRatesOrderByCommentCountAsc(long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByUserIdWithCommentsOrderByLikesDesc(long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "ORDER BY totalRate DESC")
    List<Post> findAllByUserIdWithRatesOrderByLikesDesc(long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.user.id = ?1 " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByUserIdWithCommentsOrderByLikesAsc(long userId, Pageable pageable);

    @Query("SELECT p, (SELECT COALESCE(SUM(pr.rate), 0) FROM PostRate pr WHERE pr.post = p) AS totalRate " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.user.id = ?1 " +
            "ORDER BY totalRate ASC")
    List<Post> findAllByUserIdWithRatesOrderByLikesAsc(long userId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN FETCH p.rates r " +
            "WHERE p.id = ?1")
    Optional<Post> findByIdWithRates(long postId);
}
