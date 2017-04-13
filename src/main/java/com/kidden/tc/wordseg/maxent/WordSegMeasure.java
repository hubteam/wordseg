package com.kidden.tc.wordseg.maxent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class WordSegMeasure {

    /**
     * |selected| = true positives + false positives <br>
     * the count of selected (or retrieved) items.
     */
    private long selected;

    /**
     * |target| = true positives + false negatives <br>
     * the count of target (or correct) items.
     */
    private long target;

    /**
     * Storing the number of true positives found.
     */
    private long truePositive;
    
    private long sentences;
    private long sentencesOK;
    
    private HashSet<String> dictionary;
    
    private long targetIV;
    private long targetOOV;
    
    private long truePositiveIV;
    private long truePositiveOOV;
    
    public WordSegMeasure(HashSet<String> dict){
        this.dictionary = dict;
    }
    
    public WordSegMeasure(){
        
    }

    /**
     * Retrieves the arithmetic mean of the precision scores calculated for each
     * evaluated sample.
     *
     * @return the arithmetic mean of all precision scores
     */
    public double getPrecisionScore() {
        return selected > 0 ? (double) truePositive / (double) selected : 0;
    }

    /**
     * Retrieves the arithmetic mean of the recall score calculated for each
     * evaluated sample.
     *
     * @return the arithmetic mean of all recall scores
     */
    public double getRecallScore() {
        return target > 0 ? (double) truePositive / (double) target : 0;
    }
    
    public double getRecallScoreIV() {
        return targetIV > 0 ? (double) truePositiveIV / (double) targetIV : 0;
    }
    
    public double getRecallScoreOOV() {
        return targetOOV > 0 ? (double) truePositiveOOV / (double) targetOOV : 0;
    }
    
    public double getSentenceAccuracy(){
        return sentences > 0 ? (double) sentencesOK / (double) sentences : 0;
    }

    /**
     * Retrieves the f-measure score.
     *
     * f-measure = 2 * precision * recall / (precision + recall)
     *
     * @return the f-measure or -1 if precision + recall &lt;= 0
     */
    public double getMeasure() {

        if (getPrecisionScore() + getRecallScore() > 0) {
            return 2 * (getPrecisionScore() * getRecallScore())
                    / (getPrecisionScore() + getRecallScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }

    /**
     * Updates the score based on the number of true positives and the number of
     * predictions and references.
     *
     * @param references the provided references
     * @param predictions the predicted spans
     */
    public void updateScores(final String[] references, final String[] predictions) {
        sentences++;
        
        if(references.length == predictions.length){
            boolean okSent = true;
            for(int i=0; i<references.length; i++){
                if(!references[i].equals(predictions[i]))
                    okSent = false;
            }
            
            if(okSent)
                sentencesOK++;
        }

        truePositive += countTruePositivesWithDictionary(references, predictions);
        selected += predictions.length;
        target += references.length;
    }

    /**
     * Merge results into fmeasure metric.
     *
     * @param measure the fmeasure
     */
    public void mergeInto(final WordSegMeasure measure) {
        this.selected += measure.selected;
        this.target += measure.target;
        this.truePositive += measure.truePositive;
        
        this.sentences += measure.sentences;
        this.sentencesOK += measure.sentencesOK;
        
        this.targetIV += measure.targetIV;
        this.truePositiveIV += measure.truePositiveIV;
        
        this.targetOOV += measure.targetOOV;
        this.truePositiveOOV += measure.truePositiveOOV;
    }

    /**
     * Creates a human read-able {@link String} representation.
     *
     * @return the results
     */
    @Override
    public String toString() {
        return "Precision: " + Double.toString(getPrecisionScore()) + "\n"
                + "Recall: " + Double.toString(getRecallScore()) + "\n" + "F-Measure: "
                + Double.toString(getMeasure()) + "\n"
                + "RIV: " + Double.toString(getRecallScoreIV()) + "\n"
                + "ROOV: " + Double.toString(getRecallScoreOOV()) + "\n"
                + "SentenceAccuray: " + Double.toString(getSentenceAccuracy());
    }
    
    private int countTruePositivesWithDictionary(final String[] references, final String[] predictions) {

        List<String> predListSpans = new ArrayList<String>(predictions.length);
        Collections.addAll(predListSpans, predictions);
        int truePositives = 0;
        Object matchedItem = null;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++) {
            String referenceName = references[referenceIndex];
            
            boolean isIV = true;
            
            if(dictionary!=null){
                isIV = dictionary.contains(referenceName);
                
                if(isIV)
                    targetIV++;
                else
                    targetOOV++;
            }

            for (int predIndex = 0; predIndex < predListSpans.size(); predIndex++) {

                if (referenceName.equals(predListSpans.get(predIndex))) {
                    matchedItem = predListSpans.get(predIndex);
                    truePositives++;
                    
                    if(dictionary!=null){
                        if(isIV)
                            truePositiveIV++;
                        else
                            truePositiveOOV++;
                    }
                    
                    break;
                }
            }
            
            if (matchedItem != null) {
                predListSpans.remove(matchedItem);
                
                matchedItem = null;
            }
        }
        return truePositives;
    }

    /**
     * This method counts the number of objects which are equal and occur in the
     * references and predictions arrays. Matched items are removed from the
     * prediction list.
     *
     * @param references the gold standard
     * @param predictions the predictions
     * @return number of true positives
     */
    static int countTruePositives(final String[] references, final String[] predictions) {

        List<String> predListSpans = new ArrayList<String>(predictions.length);
        Collections.addAll(predListSpans, predictions);
        int truePositives = 0;
        Object matchedItem = null;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++) {
            String referenceName = references[referenceIndex];

            for (int predIndex = 0; predIndex < predListSpans.size(); predIndex++) {

                if (referenceName.equals(predListSpans.get(predIndex))) {
                    matchedItem = predListSpans.get(predIndex);
                    truePositives++;
                    break;
                }
            }
            
            if (matchedItem != null) {
                predListSpans.remove(matchedItem);
                
                matchedItem = null;
            }
        }
        return truePositives;
    }

    /**
     * Calculates the precision score for the given reference and predicted
     * spans.
     *
     * @param references the gold standard spans
     * @param predictions the predicted spans
     * @return the precision score or NaN if there are no predicted spans
     */
    public static double precision(final String[] references, final String[] predictions) {

        if (predictions.length > 0) {
            return countTruePositives(references, predictions)
                    / (double) predictions.length;
        } else {
            return Double.NaN;
        }
    }

    /**
     * Calculates the recall score for the given reference and predicted spans.
     *
     * @param references the gold standard spans
     * @param predictions the predicted spans
     *
     * @return the recall score or NaN if there are no reference spans
     */
    public static double recall(final String[] references, final String[] predictions) {

        if (references.length > 0) {
            return countTruePositives(references, predictions)
                    / (double) references.length;
        } else {
            return Double.NaN;
        }
    }
}
