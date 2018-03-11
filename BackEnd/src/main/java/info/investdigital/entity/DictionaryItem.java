package info.investdigital.entity;

/**
 * @author ccl
 * @time 2017-12-13 11:14
 * @name DictionaryItem
 * @desc:
 */
public class DictionaryItem {
    private Long id;
    private Long dictId;
    private String dictItemName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getDictItemName() {
        return dictItemName;
    }

    public void setDictItemName(String dictItemName) {
        this.dictItemName = dictItemName;
    }

    @Override
    public String toString() {
        return "DictionaryItem{" +
                "id=" + id +
                ", dictId=" + dictId +
                ", dictItemName='" + dictItemName + '\'' +
                '}';
    }
}
