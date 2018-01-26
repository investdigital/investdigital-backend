package com.oxchains.comments.service;

import com.oxchains.comments.common.ConstEnum;
import com.oxchains.comments.common.RestResp;
import com.oxchains.comments.common.RestRespPage;
import com.oxchains.comments.common.WordFilter;
import com.oxchains.comments.entity.*;
import com.oxchains.comments.repo.CommentsFavorRepo;
import com.oxchains.comments.repo.CommentsReplyRepo;
import com.oxchains.comments.repo.CommentsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ccl
 * @time 2018-01-22 14:24
 * @name CommentsService
 * @desc:
 */

@Slf4j
@Service
public class CommentsService {

    @Resource
    private CommentsRepo commentsRepo;
    @Resource
    private CommentsReplyRepo commentsReplyRepo;
    @Resource
    private CommentsFavorRepo commentsFavorRepo;

    public RestResp addComments(Comments comments){
        try{
            if(WordFilter.isContainSensitiveWord(comments.getContents())){
                return RestResp.fail("您输入的字符包含敏感词汇，请重新输入");
            }
            comments.setCreateTime(new Date());
            comments = commentsRepo.save(comments);
            return RestResp.success("添加评论成功",comments);
        }catch (Exception e){
            log.error("添加评论失败",e);
            return RestResp.fail("添加评论失败");
        }
    }

    public RestResp deleteComments(Long commentsId){
        try{
            commentsRepo.delete(commentsId);
            return RestResp.success("评论删除成功");
        }catch (Exception e){
            log.error("添加删除失败",e);
            return RestResp.fail("添加删除失败");
        }
    }

    public RestResp getComments(String appKey, Long itemId, Integer pageSize, Integer pageNo){
        try{
            pageSize = pageSize == null ? 10 :pageSize;
            pageNo = pageNo == null ? 1 :pageNo;
            Pageable pageable = new PageRequest((pageNo-1)*pageSize, pageSize);
            Page<Comments> page = commentsRepo.findByAppKeyAndItemIdOrderByCreateTimeDesc(appKey,itemId,pageable);

            List<CommentsVO> list = new ArrayList<>();
            for(Comments comments : page.getContent()){
                String contents = comments.getContents();
                if(WordFilter.isContainSensitiveWord(contents)){
                    comments.setContents(WordFilter.replaceSensitiveWord(contents));
                }
                CommentsVO commentsVO = new CommentsVO(comments);
                List<CommentsReplyVO> relies = getCommentsReplyVO(comments.getId());
                commentsVO.setReplies(relies);
                list.add(commentsVO);
            }
            return RestRespPage.success(list,page.getTotalElements());
        }catch (Exception e){
            log.error("获取数据出错",e);
            return RestResp.fail("获取数据出错");
        }
    }

    public RestResp getComments(String appKey, Long itemId, Long userId){
        try{
            List<Comments> list = commentsRepo.findByAppKeyAndItemIdAndUserId(appKey,itemId,userId);
            if(null == list || list.size() <= 0){
                return RestResp.success("您暂无评论数据",null);
            }
            return RestResp.success("您已经添加了"+list.size()+"条评论信息!",list);
        }catch (Exception e){
            log.error("获取数据出错",e);
            return RestResp.fail("获取数据出错");
        }
    }

    /**
     *  添加回复
     */
    public RestResp addCommentReplay(CommentsReply reply){
        try {
            reply.setCreateTime(new Date());
            reply = commentsReplyRepo.save(reply);
            return RestResp.success("回复评论成功",reply);
        }catch (Exception e){
            log.error("回复评论失败",e);
        }
        return RestResp.fail("回复评论失败");
    }

    /**
     * 为评论点赞或取消点赞
     */
    public RestResp addCommentFavor(CommentsFavor favor){
        int approval = 0;
        int disapproval = 0;
        if(favor.getFavor() == 1){
            approval = 1;
        } else {
            approval = -1;
        }

        try {
            int type = favor.getType();
            switch (type){
                //评论
                case 1:
                    Comments comments = commentsRepo.findOne(favor.getCommentsId());
                    comments.setApproval(comments.getApproval()==null?approval: comments.getApproval() + approval);
                    commentsRepo.save(comments);
                    break;
                //回复
                case 2:
                    CommentsReply reply = commentsReplyRepo.findOne(favor.getCommentsReplyId());
                    reply.setApproval(reply.getApproval() == null ? approval : reply.getApproval() + approval);
                    commentsReplyRepo.save(reply);
                    break;
                default:
                    break;
            }

            CommentsFavor commentsFavor =
                    commentsFavorRepo.findByAppKeyAndItemIdAndCommentsIdAndUserIdAndCommentsReplyId(favor.getAppKey(),
                            favor.getItemId(),favor.getCommentsId(),favor.getUserId(),favor.getCommentsReplyId());
            if(null != commentsFavor){
                commentsFavor.setFavor(favor.getFavor());
                favor = commentsFavorRepo.save(commentsFavor);
            } else {
                favor.setCreateTime(new Date());
                favor = commentsFavorRepo.save(favor);
            }

            return RestResp.success("成功",favor);
        }catch (Exception e){
            log.error("回复评论失败",e);
        }
        return RestResp.fail();
    }

    public List<CommentsReplyVO> getCommentsReplyVO(Long commentsId){
        List<CommentsReply> replies = commentsReplyRepo.findByCommentsIdOrderByCreateTimeDesc(commentsId);
        List<CommentsReplyVO> res = null;
        if(null != replies && replies.size()>0){
            res = new ArrayList<>(replies.size());
            for (CommentsReply reply : replies){
                res.add(new CommentsReplyVO(reply));
            }
        }
        return res;
    }

}
