package com.kidden.tc.wordseg.maxent;

import opennlp.tools.util.eval.EvaluationMonitor;

public class WordSegEvaluationMonitor implements EvaluationMonitor<WordSegSample> {

    @Override
    public void correctlyClassified(WordSegSample reference, WordSegSample prediction) {
    }

    @Override
    public void missclassified(WordSegSample reference, WordSegSample prediction) {
    }
}
