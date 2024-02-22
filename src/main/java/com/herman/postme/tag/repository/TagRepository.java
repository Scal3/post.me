package com.herman.postme.tag.repository;

import com.herman.postme.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findOneByName(String name);

    @Query("SELECT t " +
            "FROM Tag " +
            "t LEFT JOIN t.posts p " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(t) DESC")
    List<Tag> find10TagsByAmountOfUsage(Pageable pageable);
}
