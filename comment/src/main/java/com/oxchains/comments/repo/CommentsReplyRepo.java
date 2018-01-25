package com.oxchains.comments.repo;

import com.oxchains.comments.entity.CommentsReply;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2018-01-22 14:20
 * @name CommentsReplyRepo
 * @desc:
 */
@Repository
public interface CommentsReplyRepo extends CrudRepository<CommentsReply, Long> {


    /**
     *
     */
    //Page<Comments> findByAppKeyAndItemIdOrderByCreateTimeDesc(String appKey, Long itemId, Pageable pageable);


}
