package com.kidden.tc.wordseg.maxent;

import opennlp.tools.util.BeamSearchContextGenerator;

/**
 * The interface for a context generator for the word segmenter.
 */
public interface WordSegContextGenerator extends BeamSearchContextGenerator<String> {

    /**
     * Returns the context for making a word segmentation tag decision at the
     * specified token index given the specified tokens and previous tags.
     *
     * @param index The index of the token for which the context is provided.
     * @param tokens The tokens in the sentence.
     * @param tags The tags assigned to the previous characters in the sentence.
     * @param ac the additioanl context
     *
     * @return The context for making a word segmentation tag decision at the
     * specified token index given the specified tokens and previous tags.
     */
    public String[] getContext(int index, String[] tokens, String[] tags, Object[] ac);

}
