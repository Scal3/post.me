package com.herman.postme.comment.repository;

import com.herman.postme.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(@Param("postId") long postId, Pageable pageable);

//    @Query("")
//    List<Comment> findAllByPostIdOrderByLikesDesc(@Param("postId") long postId, Pageable pageable);
//
//    @Query("")
//    List<Comment> findAllByPostIdOrderByLikesAsc(@Param("postId") long postId, Pageable pageable);
}
