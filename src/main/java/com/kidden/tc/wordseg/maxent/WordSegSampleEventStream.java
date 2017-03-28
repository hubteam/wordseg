package com.kidden.tc.wordseg.maxent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * This class reads the {@link WordSegSample}s from the given {@link Iterator}
 * and converts the {@link WordSegSample}s into {@link Event}s which can be used
 * by the maxent library for training.
 */
public class WordSegSampleEventStream extends AbstractEventStream<WordSegSample> {

    /**
     * The {@link WordSegContextGenerator} used to create the training
     * {@link Event}s.
     */
    private WordSegContextGenerator cg;

    /**
     * Initializes the current instance with the given samples and the given
     * {@link WordSegContextGenerator}.
     *
     * @param samples
     * @param cg
     */
    public WordSegSampleEventStream(ObjectStream<WordSegSample> samples, WordSegContextGenerator cg) {
        super(samples);

        this.cg = cg;
    }

    /**
     * Initializes the current instance with given samples and a
     * {@link DefaultWordSegContextGenerator}.
     *
     * @param samples
     */
    public WordSegSampleEventStream(ObjectStream<WordSegSample> samples) {
        this(samples, new DefaultWordSegContextGenerator());
    }

    @Override
    protected Iterator<Event> createEvents(WordSegSample sample) {
        String sentence[] = sample.getSentence();
        String tags[] = sample.getTags();
        Object ac[] = sample.getAddictionalContext();
        List<Event> events = generateEvents(sentence, tags, ac, cg);
        return events.iterator();
    }

    public static List<Event> generateEvents(String[] sentence, String[] tags,
            Object[] additionalContext, WordSegContextGenerator cg) {
        List<Event> events = new ArrayList<Event>(sentence.length);

        for (int i = 0; i < sentence.length; i++) {

            // it is safe to pass the tags as previous tags because
            // the context generator does not look for non predicted tags
            String[] context = cg.getContext(i, sentence, tags, additionalContext);

            events.add(new Event(tags[i], context));
        }
        return events;
    }

    public static List<Event> generateEvents(String[] sentence, String[] tags,
            WordSegContextGenerator cg) {
        return generateEvents(sentence, tags, null, cg);
    }
}
