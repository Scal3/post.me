package com.herman.postme.post_rate.repository;

import com.herman.postme.post_rate.entity.PostRate;
import com.herman.postme.post_rate.entity.PostRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRateRepository extends JpaRepository<PostRate, PostRateId> {
}
