package com.kidden.tc.wordseg.maxent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;

/**
 * Represents a segmented sentence.
 * 
 * Tag set includes S, B, M, E.
 */
public class WordSegSample {

    private List<String> sentence;

    private List<String> tags;

    private final String[][] additionalContext;

    /**
     * 
     * @param sentence characters list
     * @param tags tags for each characer in sentence
     */
    public WordSegSample(String sentence[], String tags[]) {
        this(sentence, tags, null);
    }

    public WordSegSample(List<String> sentence, List<String> tags) {
        this(sentence, tags, null);
    }

    public WordSegSample(List<String> sentence, List<String> tags,
            String[][] additionalContext) {
        this.sentence = Collections.unmodifiableList(sentence);
        this.tags = Collections.unmodifiableList(tags);

        checkArguments();
        String[][] ac;
        if (additionalContext != null) {
            ac = new String[additionalContext.length][];

            for (int i = 0; i < additionalContext.length; i++) {
                ac[i] = new String[additionalContext[i].length];
                System.arraycopy(additionalContext[i], 0, ac[i], 0,
                        additionalContext[i].length);
            }
        } else {
            ac = null;
        }
        this.additionalContext = ac;
    }

    public WordSegSample(String sentence[], String tags[],
            String[][] additionalContext) {
        this(Arrays.asList(sentence), Arrays.asList(tags), additionalContext);
    }

    private void checkArguments() {
        if (sentence.size() != tags.size()) {
            throw new IllegalArgumentException(
                    "There must be exactly one tag for each token. tokens: " + sentence.size()
                    + ", tags: " + tags.size());
        }

        if (sentence.contains(null)) {
            throw new IllegalArgumentException("null elements are not allowed in sentence tokens!");
        }
        if (tags.contains(null)) {
            throw new IllegalArgumentException("null elements are not allowed in tags!");
        }
    }

    public String[] getSentence() {
        return sentence.toArray(new String[sentence.size()]);
    }

    public String[] getTags() {
        return tags.toArray(new String[tags.size()]);
    }

    public String[][] getAddictionalContext() {
        return this.additionalContext;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < getSentence().length; i++) {
            result.append(getSentence()[i]);
            result.append('_');
            result.append(getTags()[i]);
            result.append(' ');
        }

        if (result.length() > 0) {
            // get rid of last space
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 
     * @param sentenceString the segmented sentence which is seperated with whitespace
     * @return
     * @throws InvalidFormatException 
     */
    public static WordSegSample parse(String sentenceString) throws InvalidFormatException {

        String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentenceString);

        ArrayList<String> sentence = new ArrayList<String>();
        ArrayList<String> tags = new ArrayList<String>();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.length() == 1) {
                sentence.add(word);
                tags.add("S");
                continue;
            }

            for (int j = 0; j < word.length(); j++) {
                char c = word.charAt(j);
                if (j == 0) {
                    sentence.add(c + "");
                    tags.add("B");
                } else if (j == word.length() - 1) {
                    sentence.add(c + "");
                    tags.add("E");
                } else {
                    sentence.add(c + "");
                    tags.add("M");
                }
            }
        }

        return new WordSegSample(sentence, tags);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof WordSegSample) {
            WordSegSample a = (WordSegSample) obj;

            return Arrays.equals(getSentence(), a.getSentence())
                    && Arrays.equals(getTags(), a.getTags());
        } else {
            return false;
        }
    }
}
