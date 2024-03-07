package com.herman.postme.comment.repository;

import com.herman.postme.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.rates r " +
            "WHERE c.post.id = ?1 " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostIdWithRatesOrderByCreatedAtDesc(long postId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.rates r " +
            "WHERE c.post.id = ?1 " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findAllByPostIdWithRatesOrderByCreatedAtAsc(long postId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.rates r " +
            "WHERE c.id = ?1")
    Optional<Comment> findByIdWithRates(long commentId);

    @Query("SELECT c, (SELECT COALESCE(SUM(cr.rate), 0) FROM CommentRate cr WHERE cr.comment = c) AS totalRate " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.rates r " +
            "WHERE c.post.id = ?1 " +
            "ORDER BY totalRate DESC")
    List<Comment> findAllByPostIdWithRatesOrderByLikesDesc(long postId, Pageable pageable);

    @Query("SELECT c, (SELECT COALESCE(SUM(cr.rate), 0) FROM CommentRate cr WHERE cr.comment = c) AS totalRate " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.rates r " +
            "WHERE c.post.id = ?1 " +
            "ORDER BY totalRate ASC")
    List<Comment> findAllByPostIdWithRatesOrderByLikesAsc(long postId, Pageable pageable);
}
