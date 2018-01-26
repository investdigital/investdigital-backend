package com.oxchains.comments.repo;

import com.oxchains.comments.entity.CommentsReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
     * @param commentsId
     * @return
     */
    Page<CommentsReply> findByCommentsIdOrderByCreateTimeDesc(Long commentsId, Pageable pageable);



    /**
     *
     * @param commentsId
     * @return
     *
     *  //@Query(value = "select c from CommentsReply as c where (c.commentsId = :commentsId) order by c.createTime desc")
     */
    List<CommentsReply> findByCommentsIdOrderByCreateTimeDesc(Long commentsId);

}
