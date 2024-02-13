package com.herman.postme.post.repository;

import com.herman.postme.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c) DESC")
    List<Post> findAllOrderByCommentsDesc(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c) ASC")
    List<Post> findAllOrderByCommentsAsc(Pageable pageable);

//    @Query("")
//    List<Post> findAllOrderByLikesDesc(Pageable pageable);
//
//    @Query("")
//    List<Post> findAllOrderByLikesAsc(Pageable pageable);

}
