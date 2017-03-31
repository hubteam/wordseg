package com.kidden.tc.wordseg.maxent;

import com.kidden.tc.wordseg.WordSegmenter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventModelSequenceTrainer;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 * A chinese word segmenter that uses maximum entropy.
 *
 */
public class WordSegmenterME implements WordSegmenter {

    public static final int DEFAULT_BEAM_SIZE = 3;

    private WordSegModel modelPackage;

    /**
     * The feature context generator.
     */
    protected WordSegContextGenerator contextGen;

    /**
     * Says whether a filter should be used to check whether a tag assignment is
     * to a word outside of a closed class.
     */
    protected boolean useClosedClassTagsFilter = false;

    /**
     * The size of the beam to be used in determining the best sequence of pos
     * tags.
     */
    protected int size;

    private Sequence bestSequence;

    private SequenceClassificationModel<String> model;

    private SequenceValidator<String> sequenceValidator;

    public WordSegmenterME(WordSegModel model) {
        this(model, new DefaultWordSegContextGenerator());
    }

    /**
     * Initializes the current instance with the provided model and the default
     * beam size of 3.
     *
     * @param model
     */
    public WordSegmenterME(WordSegModel model, WordSegContextGenerator contextGenerator) {

        int beamSize = WordSegmenterME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGen = contextGenerator;
        size = beamSize;

        sequenceValidator = new DefaultWordSegSequenceValidator();

        if (model.getWordSegSequenceModel() != null) {
            this.model = model.getWordSegSequenceModel();
        } else {
            this.model = new opennlp.tools.ml.BeamSearch<String>(beamSize,
                    model.getWordSegModel(), 0);
        }

    }

    /**
     * Retrieves an array of all possible segmentation tags from the segmenter.
     *
     * @return
     */
    public String[] getAllPosTags() {
        return model.getOutcomes();
    }

    public String[] tag(String[] sentence) {
        return this.tag(sentence, null);
    }

    public String[] tag(String sentence) {
        String[] chars = new String[sentence.length()];

        for (int i = 0; i < sentence.length(); i++) {
            chars[i] = sentence.charAt(i) + "";
        }

        return tag(chars);
    }

    @Override
    public String[] segment(String text) {
        String[] tags = tag(text);

        String word = new String();
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < tags.length; i++) {
            word += text.charAt(i);

            if (tags[i].equals("S") || tags[i].equals("E")) {
                words.add(word);
                word = "";
            }

        }
        
        if(word.length() > 0)
            words.add(word);

        return words.toArray(new String[words.size()]);
    }

    public String[] tag(String[] sentence, Object[] additionaContext) {
        bestSequence = model.bestSequence(sentence, additionaContext, contextGen, sequenceValidator);
        List<String> t = bestSequence.getOutcomes();
        return t.toArray(new String[t.size()]);
    }

    /**
     * Returns at most the specified number of taggings for the specified
     * sentence.
     *
     * @param numTaggings The number of tagging to be returned.
     * @param sentence An array of tokens which make up a sentence.
     *
     * @return At most the specified number of taggings for the specified
     * sentence.
     */
    public String[][] tag(int numTaggings, String[] sentence) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, sentence, null,
                contextGen, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
        }
        return tags;
    }

    public Sequence[] topKSequences(String[] sentence) {
        return this.topKSequences(sentence, null);
    }

    public Sequence[] topKSequences(String[] sentence, Object[] additionaContext) {
        return model.bestSequences(size, sentence, additionaContext, contextGen, sequenceValidator);
    }

    /**
     * Populates the specified array with the probabilities for each tag of the
     * last tagged sentence.
     *
     * @param probs An array to put the probabilities into.
     */
    public void probs(double[] probs) {
        bestSequence.getProbs(probs);
    }

    /**
     * Returns an array with the probabilities for each tag of the last tagged
     * sentence.
     *
     * @return an array with the probabilities for each tag of the last tagged
     * sentence.
     */
    public double[] probs() {
        return bestSequence.getProbs();
    }

    public String[] getOrderedTags(List<String> words, List<String> tags, int index) {
        return getOrderedTags(words, tags, index, null);
    }

    public String[] getOrderedTags(List<String> words, List<String> tags, int index, double[] tprobs) {

        if (modelPackage.getWordSegModel() != null) {

            MaxentModel posModel = modelPackage.getWordSegModel();

            double[] probs = posModel.eval(contextGen.getContext(index,
                    words.toArray(new String[words.size()]),
                    tags.toArray(new String[tags.size()]), null));

            String[] orderedTags = new String[probs.length];
            for (int i = 0; i < probs.length; i++) {
                int max = 0;
                for (int ti = 1; ti < probs.length; ti++) {
                    if (probs[ti] > probs[max]) {
                        max = ti;
                    }
                }
                orderedTags[i] = posModel.getOutcome(max);
                if (tprobs != null) {
                    tprobs[i] = probs[max];
                }
                probs[max] = 0;
            }
            return orderedTags;
        } else {
            throw new UnsupportedOperationException("This method can only be called if the "
                    + "classifcation model is an event model!");
        }
    }

    public static WordSegModel train(String languageCode,
            ObjectStream<WordSegSample> samples, TrainingParameters trainParams) throws IOException {
        return train(languageCode, samples, trainParams, new DefaultWordSegContextGenerator());
    }

    public static WordSegModel train(String languageCode,
            ObjectStream<WordSegSample> samples, TrainingParameters trainParams,
            WordSegContextGenerator contextGenerator) throws IOException {
        String beamSizeString = trainParams.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

        int beamSize = WordSegmenterME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        //WordSegContextGenerator contextGenerator = new DefaultWordSegContextGenerator();
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();

        TrainerType trainerType = TrainerFactory.getTrainerType(trainParams.getSettings());

        MaxentModel posModel = null;
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
            ObjectStream<Event> es = new WordSegSampleEventStream(samples, contextGenerator);

            EventTrainer trainer = TrainerFactory.getEventTrainer(trainParams.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);
        } else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)) {
            WordSegSampleSequenceStream ss = new WordSegSampleSequenceStream(samples, contextGenerator);
            EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(trainParams.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(ss);
        } else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType)) {
            SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(
                    trainParams.getSettings(), manifestInfoEntries);

            // TODO: This will probably cause issue, since the feature generator uses the outcomes array
            WordSegSampleSequenceStream ss = new WordSegSampleSequenceStream(samples, contextGenerator);
            seqPosModel = trainer.train(ss);
        } else {
            throw new IllegalArgumentException("Trainer type is not supported: " + trainerType);
        }

        if (posModel != null) {
            return new WordSegModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new WordSegModel(languageCode, seqPosModel, manifestInfoEntries);
        }
    }

}
