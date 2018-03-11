package info.investdigital.entity;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

/**
 * @author oxchains
 * @time 2017-12-15 17:33
 * @name FundComment
 * @desc:
 */
@Entity
@Table(name = "fund_comment")
public class FundComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long fundId;
    private Long userId;

    @Transient
    private String username;

    @Column(name = "date_")
    private String date;
    private String comments;
    private String images;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

}
