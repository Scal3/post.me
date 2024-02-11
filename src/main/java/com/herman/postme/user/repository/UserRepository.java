package com.herman.postme.user.repository;

import com.herman.postme.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

//    List<User> findAllByOrderByPostsQuantityDesc(Pageable pageable);
//
//    List<User> findAllByOrderByCommentsQuantityDesc(Pageable pageable);
//
//    List<User> findAllByOrderByLikesQuantityDesc(Pageable pageable);
}
