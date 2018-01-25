package com.oxchains.comments.repo;

import com.oxchains.comments.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2018-01-22 14:20
 * @name CommentsRepo
 * @desc:
 */
@Repository
public interface CommentsRepo extends CrudRepository<Comments, Long> {
    /**
     *
     */
    List<Comments> findByAppKeyAndItemIdAndUserId(String appKey, Long itemId, Long userId);

    /**
     *
     */
    Page<Comments> findByAppKeyAndItemIdOrderByCreateTimeDesc(String appKey,Long itemId, Pageable pageable);

    /**
     *
     */
    @Query(value = "select c from Comments  as c where (c.appKey = :appKey) and (c.itemId = :itemId) and (c.contents like :contents)")
    List<Comments> findByAppKeyAndItemIdAndContents(String appKey, Long itemId, String contents);
}
