package com.oxchains.comments.repo;

import com.oxchains.comments.entity.CommentsFavor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ccl
 * @time 2018-01-22 14:20
 * @name CommentsReplyRepo
 * @desc:
 */
@Repository
public interface CommentsFavorRepo extends CrudRepository<CommentsFavor, Long> {
    /**
     *
     */
    CommentsFavor findByAppKeyAndItemIdAndCommentsIdAndUserIdAndCommentsReplyId(String appKey, Long itemId, Long commentsId, Long userId, Long commentsReplyId);

    /**
     *
     */
    CommentsFavor findByAppKeyAndItemIdAndCommentsIdAndCommentsReplyId(String appKey, Long itemId, Long commentsId, Long commentsReplyId);
}
