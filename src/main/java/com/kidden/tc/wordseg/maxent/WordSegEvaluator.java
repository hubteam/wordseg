package com.kidden.tc.wordseg.maxent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.eval.Evaluator;

/**
 * The {@link WordSegEvaluator} measures the performance of the given
 * {@link WordSegmenterME} with the provided reference {@link WordSegSample}s.
 */
public class WordSegEvaluator extends Evaluator<WordSegSample> {

    private WordSegmenterME tagger;

    private WordSegMeasure measure = new WordSegMeasure();

    /**
     * Initializes the current instance.
     *
     * @param tagger
     * @param listeners an array of evaluation listeners
     */
    public WordSegEvaluator(WordSegmenterME tagger, WordSegEvaluationMonitor... listeners) {
        super(listeners);
        this.tagger = tagger;
    }

    /**
     * Evaluates the given reference {@link WordSegSample} object.
     *
     * This is done by tagging the sentence from the reference
     * {@link WordSegSample} with the {@link POSTagger}. The tags are then used
     * to update the word accuracy score.
     *
     * @param reference the reference {@link WordSegSample}.
     *
     * @return the predicted {@link WordSegSample}.
     */
    @Override
    protected WordSegSample processSample(WordSegSample reference) {

        String predictedTags[] = tagger.tag(reference.getSentence(), reference.getAddictionalContext());
        String referenceTags[] = reference.getTags();

        WordSegSample predictions = new WordSegSample(reference.getSentence(), predictedTags);

        measure.updateScores(reference.toWords(), predictions.toWords());

        return predictions;
    }

    public WordSegMeasure getMeasure() {
        return measure;
    }

    public static void eval(File modelFile, File goldFile, File errorFile, WordSegContextGenerator contextGenerator, String encoding) throws IOException {
        InputStream modelIn = new FileInputStream(modelFile);
        WordSegModel model = new WordSegModel(modelIn);
        WordSegmenterME tagger = new WordSegmenterME(model, contextGenerator);
        WordSegErrorPrinter errorMonitor = new WordSegErrorPrinter(new FileOutputStream(errorFile));
        WordSegEvaluator evaluator = new WordSegEvaluator(tagger, errorMonitor);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

        evaluator.evaluate(sampleStream);

        WordSegMeasure f = evaluator.getMeasure();
        System.out.println(f);
    }

    public static void eval(File modelFile, File goldFile, WordSegContextGenerator contextGenerator, String encoding) throws IOException {
        InputStream modelIn = new FileInputStream(modelFile);
        WordSegModel model = new WordSegModel(modelIn);
        WordSegmenterME tagger = new WordSegmenterME(model, contextGenerator);
        WordSegEvaluator evaluator = new WordSegEvaluator(tagger);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

        evaluator.evaluate(sampleStream);

        WordSegMeasure f = evaluator.getMeasure();
        System.out.println(f);
    }

    public static void main(String[] args) throws IOException {
        String modelFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-model")) {
                modelFile = args[i + 1];
                i++;
            } else if (args[i].equals("-gold")) {
                goldFile = args[i + 1];
                i++;
            } else if (args[i].equals("-error")) {
                errorFile = args[i + 1];
                i++;
            } else if (args[i].equals("-encoding")) {
                encoding = args[i + 1];
                i++;
            }
        }

        WordSegContextGenerator contextGenerator = new DefaultWordSegContextGenerator();

        if (errorFile != null) {
            eval(new File(modelFile), new File(goldFile), new File(errorFile), contextGenerator, encoding);
        }
        else
            eval(new File(modelFile), new File(goldFile), contextGenerator, encoding);
    }
}
