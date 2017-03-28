package com.kidden.tc.wordseg.maxent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;

/**
 * A stream filter which reads a sentence per line which contains words
 * seperated by whitespaces and outputs a {@link WordSegSample} objects.
 */
public class WordTagSampleStream extends FilterObjectStream<String, WordSegSample> {

    private static Logger logger = Logger.getLogger(WordTagSampleStream.class.getName());

    public WordTagSampleStream(ObjectStream<String> sentences) {
        super(sentences);
    }

    /**
     * Parses the next sentence and return the next {@link WordSegSample}
     * object.
     *
     * If an error occurs an empty {@link WordSegSample} object is returned and
     * an warning message is logged. Usually it does not matter if one of many
     * sentences is ignored.
     *
     */
    public WordSegSample read() throws IOException {
        String sentence = samples.read();

        if (sentence != null) {
            WordSegSample sample;
            try {
                sample = WordSegSample.parse(sentence);
            } catch (InvalidFormatException e) {

                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
                }

                sample = new WordSegSample(new String[]{}, new String[]{});
            }

            return sample;
        } else {
            // sentences stream is exhausted
            return null;
        }
    }
}
