package com.herman.postme.comment_rate.repository;

import com.herman.postme.comment_rate.entity.CommentRate;
import com.herman.postme.comment_rate.entity.CommentRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRateRepository extends JpaRepository<CommentRate, CommentRateId> {
}
