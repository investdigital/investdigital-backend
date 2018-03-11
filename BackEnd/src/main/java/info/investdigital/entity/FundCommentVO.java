package info.investdigital.entity;

import info.investdigital.entity.FundComment;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

/**
 * @author oxchains
 * @time 2017-12-15 17:33
 * @name FundComment
 * @desc:
 */

public class FundCommentVO extends FundComment {
    public FundCommentVO() {
    }

    private String username;
    private MultipartFile file;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public FundCommentVO(FundComment comment) {
        setId(comment.getId());
        setFundId(comment.getFundId());
        setUserId(comment.getUserId());
        setImages(comment.getImages());
        setDate(comment.getDate());
        setComments(comment.getComments());
    }

    public FundComment vo2FundComment(){
        FundComment comment = new FundComment();
        comment.setId(this.getId());
        comment.setFundId(this.getFundId());
        comment.setUserId(this.getUserId());
        comment.setImages(this.getImages());
        comment.setDate(this.getDate());
        comment.setComments(this.getComments());
        return comment;
    }
}
