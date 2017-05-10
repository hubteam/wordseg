package com.kidden.tc.wordseg.maxent;

import java.io.IOException;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

public class WordSegCrossValidator {

    private final String languageCode;

    private final TrainingParameters params;

    private WordSegEvaluationMonitor[] listeners;
    
    private WordSegMeasure measure = new WordSegMeasure();

    public WordSegCrossValidator(String languageCode,
            TrainingParameters trainParam,
            WordSegEvaluationMonitor... listeners) {
        this.languageCode = languageCode;
        this.params = trainParam;
        this.listeners = listeners;
    }

    /**
     * Starts the evaluation.
     *
     * @param samples the data to train and test
     * @param nFolds number of folds
     * @param contextGenerator feature generator for training and segmentation
     *
     * @throws IOException
     */
    public void evaluate(ObjectStream<WordSegSample> samples, int nFolds, 
            WordSegContextGenerator contextGenerator) throws IOException {

        CrossValidationPartitioner<WordSegSample> partitioner = new CrossValidationPartitioner<>(
                samples, nFolds);

        int run = 1;
        while (partitioner.hasNext()) {
            System.out.println("Run " + run + "...");
            CrossValidationPartitioner.TrainingSampleStream<WordSegSample> trainingSampleStream = partitioner.next();
            WordSegModel model = WordSegmenterME.train(languageCode, trainingSampleStream, params, contextGenerator);

            WordSegEvaluator evaluator = new WordSegEvaluator(new WordSegmenterME(model, contextGenerator), listeners);
            evaluator.setMeasure(measure);
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            
            run++;
        }
        
        System.out.println(measure);
    }

}
