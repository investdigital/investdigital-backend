package info.investdigital.common;

import info.investdigital.entity.Dictionary;
import info.investdigital.entity.DictionaryItem;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ccl
 * @time 2017-12-13
 * @desc:
 */
public class DictionaryUtils {
    public static List<Dictionary> getDictionarys(){
        String path = DictionaryUtils.class.getClassLoader().getResource("dictionary.xml").getPath();
        Document document = XmlUtils.getXMLByFilePath(path);
        Element nodeElement = document.getRootElement();
        List dictNodes = nodeElement.elements();
        Iterator<Element> rootIt = dictNodes.iterator();
        List<Dictionary> dictionaries = dictionaries = new ArrayList<>();;
        while (rootIt.hasNext()){
            Dictionary dictionary = new Dictionary();
            Element dictElem = (Element) rootIt.next();
            Long dictId = Long.valueOf(dictElem.attributeValue("id"));
            String dictName = dictElem.attributeValue("name");
            String colName = dictElem.attributeValue("col-name");
            dictionary.setId(dictId);
            dictionary.setDictName(dictName);
            dictionary.setColName(colName);
            List dictItemNodes = dictElem.elements();
            List<DictionaryItem> dictionaryItems = null;
            if(null != dictItemNodes && dictItemNodes .size()>0){
                dictionaryItems = new ArrayList<>();
                for (Iterator itemIt = dictItemNodes.iterator(); itemIt.hasNext(); ) {
                    Element itemElem = (Element) itemIt.next();
                    DictionaryItem dictionaryItem = new DictionaryItem();
                    Long itemId = Long.valueOf(itemElem.attributeValue("id"));
                    String itemName = itemElem.attributeValue("name");

                    dictionaryItem.setDictId(dictId);
                    dictionaryItem.setId(itemId);
                    dictionaryItem.setDictItemName(itemName);

                    dictionaryItems.add(dictionaryItem);

                }
                dictionary.setDictionaryItems(dictionaryItems);
            }

            dictionaries.add(dictionary);
        }

        return dictionaries;
    }
}
