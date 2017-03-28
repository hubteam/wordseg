package com.kidden.tc.wordseg;

/**
 * Interface for chinese word segmenter
 *
 * @author kidden
 */
public interface WordSegmenter {

    /**
     * Segment sentence into words array
     *
     * @param sentence raw text to be segmented
     * @return segmented words array
     */
    public String[] segment(String sentence);

}
