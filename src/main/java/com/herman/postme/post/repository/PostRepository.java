package com.herman.postme.post.repository;

import com.herman.postme.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c) DESC")
    List<Post> findAllOrderByCommentsDesc(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c) ASC")
    List<Post> findAllOrderByCommentsAsc(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.rates r GROUP BY p.id ORDER BY COUNT(r) DESC")
    List<Post> findAllOrderByLikesDesc(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.rates r GROUP BY p.id ORDER BY COUNT(r) ASC")
    List<Post> findAllOrderByLikesAsc(Pageable pageable);

    List<Post> findAllByUserId(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c WHERE p.user.id = :userId GROUP BY p.id ORDER BY COUNT(c) DESC")
    List<Post> findAllByUserIdOrderByCommentCountDesc(@Param("userId") long userId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c WHERE p.user.id = :userId GROUP BY p.id ORDER BY COUNT(c) ASC")
    List<Post> findAllByUserIdOrderByCommentCountAsc(@Param("userId") long userId, Pageable pageable);

//    @Query("")
//    List<Post> findAllByUserIdOrderByLikesDesc(Pageable pageable);
//
//    @Query("")
//    List<Post> findAllByUserIdOrderByLikesAsc(Pageable pageable);
}
