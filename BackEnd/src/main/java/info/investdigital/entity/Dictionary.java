package info.investdigital.entity;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author ccl
 * @time 2017-12-13 11:14
 * @name Dictionary
 * @desc:
 */
public class Dictionary {
    private Long id;
    private String dictName;

    @Transient
    private String colName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    private List<DictionaryItem> dictionaryItems;

    public List<DictionaryItem> getDictionaryItems() {
        return dictionaryItems;
    }

    public void setDictionaryItems(List<DictionaryItem> dictionaryItems) {
        this.dictionaryItems = dictionaryItems;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "id=" + id +
                ", dictName='" + dictName + '\'' +
                ", colName='" + colName + '\'' +
                ", dictionaryItems=" + dictionaryItems +
                '}';
    }
}
