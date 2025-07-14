package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while((keyword = reader.readLine())!=null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败:"+e.getMessage());
        }
    }

    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subnode = tempNode.getSubNode(c);
            if(subnode == null){
                // 初始化子节点
                subnode = new TrieNode();
                tempNode.addSubNode(c,subnode);
            }

            tempNode = subnode;
            tempNode.setKeywordEnd(i == keyword.length()-1);
        }
    }

/*
 * 过滤敏感词
 * @param text
 * @return
 */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while(position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                // 忽略符号
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 跳过
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd){
                // 发现敏感词，将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                begin = position + 1;
                position = begin;
                tempNode = rootNode;
            }else{
                position++;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否跳过符号
    private boolean isSymbol(Character c){
        return !Character.isLetterOrDigit(c)&&(c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode{

        private boolean isKeywordEnd = false;
        // 子节点
        private Map<Character,TrieNode> subNodes= new HashMap<>();


        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
