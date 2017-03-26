package com.kidden.tc.wordseg.maxent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kidden
 */
public class WordSegSampleTest {
    
    public WordSegSampleTest() {
    }
    
    @Test
    public void testParse() throws Exception {
        String sentenceString = "我 喜欢 自然语言处理 。";
        
        String[] sentence = {"我", "喜", "欢", "自", "然", "语", "言", "处", "理", "。"};
        String[] tags = {"S", "B", "E", "B", "M", "M", "M", "M", "E", "S"};
        WordSegSample expResult = new WordSegSample(sentence, tags);
        
        WordSegSample result = WordSegSample.parse(sentenceString);
        assertEquals(expResult, result);
    }
    
}
