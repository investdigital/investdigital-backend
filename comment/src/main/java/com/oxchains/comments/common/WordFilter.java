package com.oxchains.comments.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author oxchains
 * @time 2018-01-23 15:22
 * @name WordFilter
 * @desc:
 */
public class WordFilter {

    private static WordFilter wordFilter;

    private static Map sensitiveWordMap = null;

    static {
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 判断文字是否包含敏感字符
     *
     * @param txt 待判断字符串
     * @return
     */
    public static boolean isContainSensitiveWord(String txt) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {

            // 判断是否包含敏感字符
            int matchFlag = CheckSensitiveWord(txt, i);

            // 大于0存在，返回true
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt 待替换字符串
     * @return
     */
    public static String replaceSensitiveWord(String txt) {

        String replaceChar = "*";

        String resultTxt = txt;

        // 获取所有的敏感词
        Set<String> set = getSensitiveWord(txt);
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取替换字符串
     *
     * @param replaceChar
     * @param length
     * @return
     */
    private static String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt
     * @return
     */
    private static Set<String> getSensitiveWord(String txt) {
        Set<String> sensitiveWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {

            // 判断是否包含敏感字符
            int length = CheckSensitiveWord(txt, i);

            // 存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));

                // 减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     * 如果存在，则返回敏感词字符的长度，不存在返回0
     *
     * @param txt 待判断字符串
     * @param beginIndex
     * @return
     */
    private static int CheckSensitiveWord(String txt, int beginIndex) {

        // 敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;

        // 匹配标识数默认为0
        int matchFlag = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            char word = txt.charAt(i);

            // 获取指定key
            nowMap = (Map) nowMap.get(word);

            // 存在，则判断是否为最后一个
            if (nowMap != null) {

                // 找到相应key，匹配标识+1
                matchFlag++;

                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {

                    // 结束标志位为true
                    flag = true;

                    break;
                }
            }

            // 不存在，直接返回
            else {
                break;
            }
        }

        // 长度必须大于等于1，为词
        if (matchFlag < 2 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    public static void main(String[] args) {
        String txt = "XXX";
        boolean flag = WordFilter.isContainSensitiveWord(txt);
        System.out.println(flag);
        System.out.println(WordFilter.replaceSensitiveWord(txt));
    }
}
