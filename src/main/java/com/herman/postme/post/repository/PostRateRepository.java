package com.herman.postme.post.repository;

import com.herman.postme.post.entity.PostRate;
import com.herman.postme.post.entity.PostRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRateRepository extends JpaRepository<PostRate, PostRateId> {
}
