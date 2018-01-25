package com.oxchains.comments.common;

/**
 * @author zhulei
 * @time 2018-01-23 15:22
 * @name SensitiveWordInit
 * @desc:
 */

import java.io.*;
import java.util.*;

/**
 * 初始化敏感词库<br>
 * 将敏感词加入到HashMap中<br>
 * 构建DFA算法模型
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SensitiveWordInit {

    private static String wordPath;

    // 字符编码
    private String ENCODING = "UTF-8";

    static {
        InputStream in = SensitiveWordInit.class.getClassLoader().getResourceAsStream("application.properties");
        Properties p = new Properties();
        try {
            p.load(in);
            wordPath = p.getProperty("sensitiveword.file");
        } catch (IOException e) {
            System.err.println("load application.properties error: "+e);
        }
    }

    /**
     * 初始化敏感字库
     *
     * @return
     */
    public Map initKeyWord() {

        // 读取敏感词库
        Set<String> wordSet = readSensitiveWordFile();

        // 将敏感词库加入到HashMap中
        return addSensitiveWordToHashMap(wordSet);
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
     * 中 = { isEnd = 0 国 = {<br>
     * isEnd = 1 人 = {isEnd = 0 民 = {isEnd = 1} } 男 = { isEnd = 0 人 = { isEnd =
     * 1 } } } } 五 = { isEnd = 0 星 = { isEnd = 0 红 = { isEnd = 0 旗 = { isEnd = 1
     * } } } }
     *
     */
    private Map addSensitiveWordToHashMap(Set<String> wordSet) {

        // 初始化敏感词容器，减少扩容操作
        Map wordMap = new HashMap(wordSet.size());

        for (String word : wordSet) {
            Map nowMap = wordMap;
            for (int i = 0; i < word.length(); i++) {

                // 转换成char型
                char keyChar = word.charAt(i);

                // 获取
                Object tempMap = nowMap.get(keyChar);

                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }

                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {

                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");

                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }

                // 最后一个
                if (i == word.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }

        return wordMap;
    }

    /**
     * 读取敏感词库中的内容，将内容添加到set集合中
     *
     * @return
     * @throws Exception
     */
    private Set<String> readSensitiveWordFile() {

        Set<String> wordSet = null;
        try {
            // 读取文件
            File file = new File( wordPath);

            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);

            // 文件流是否存在
            if (file.isFile() && file.exists()) {

                wordSet = new HashSet<String>();
                StringBuffer sb = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(read);
                String txt = null;

                // 读取文件，将文件内容放入到set中
                while ((txt = bufferedReader.readLine()) != null) {
                    //sb.append(txt);
                    wordSet.add(txt.trim());
                }
                bufferedReader.close();

               /* String str = sb.toString();
                String[] ss = str.split("#");
                for (String s : ss) {
                    wordSet.add(s);
                }*/
            }

            // 关闭文件流
            read.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return wordSet;
    }
}

