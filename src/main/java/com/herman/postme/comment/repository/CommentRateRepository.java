package com.herman.postme.comment.repository;

import com.herman.postme.comment.entity.CommentRate;
import com.herman.postme.comment.entity.CommentRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRateRepository extends JpaRepository<CommentRate, CommentRateId> {
}
