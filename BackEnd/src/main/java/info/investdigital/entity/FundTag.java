package info.investdigital.entity;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-12-13 17:03
 * @name FundTag
 * @desc:
 */
@Entity
@Table(name = "fund_tag")
public class FundTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tagName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
