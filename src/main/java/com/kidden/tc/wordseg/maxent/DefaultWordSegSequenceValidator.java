package com.kidden.tc.wordseg.maxent;

import opennlp.tools.util.SequenceValidator;

/**
 * Validate word segmentation sequence
 *
 * @author kidden
 */
public class DefaultWordSegSequenceValidator implements SequenceValidator<String> {

    public boolean validSequence(int i, String[] inputSequence,
            String[] outcomesSequence, String outcome) {
//      System.out.println("" + i + Arrays.toString(inputSequence) + " " +
//      Arrays.toString(outcomesSequence) + " " + outcome);

        if (i == 0) {
            return outcome.equals("S") || outcome.equals("B");
        } else {
            if (outcome.equals("S")) {
                return outcomesSequence[i - 1].equals("S") || outcomesSequence[i - 1].equals("E");
            } else if (outcome.equals("B")) {
                return outcomesSequence[i - 1].equals("S") || outcomesSequence[i - 1].equals("E");
            } else if (outcome.equals("M")) {
                return outcomesSequence[i - 1].equals("B") || outcomesSequence[i - 1].equals("M");
            } else if (outcome.equals("E")) {
                return outcomesSequence[i - 1].equals("B") || outcomesSequence[i - 1].equals("M");
            }
        }

        return true;
    }
}
