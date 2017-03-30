package com.kidden.tc.wordseg.maxent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kidden
 */
public class DefaultWordSegSequenceValidatorTest {

    public DefaultWordSegSequenceValidatorTest() {
    }

    /**
     * Test of validSequence method, of class DefaultWordSegSequenceValidator.
     */
    @Test
    public void testValidSequence() {
        System.out.println("validSequence");
        String[] inputSequence = {"c1", "c2", "c3", "c4", "c5"};
        String[] outcomesSequence1 = {"S", "B", "E", "S", "S"};
        String outcome = "";
        DefaultWordSegSequenceValidator instance = new DefaultWordSegSequenceValidator();

        for (int i = 0; i < inputSequence.length; i++) {
            boolean expResult = true;
            outcome = outcomesSequence1[i];
            boolean result = instance.validSequence(i, inputSequence, outcomesSequence1, outcome);
            assertEquals(expResult, result);
        }

        String[] outcomesSequence2 = {"S", "B", "B", "M", "S"};
        boolean expResult = false;
        int j = 2;
        outcome = outcomesSequence1[j];
        boolean result = instance.validSequence(j, inputSequence, outcomesSequence1, outcome);
        assertNotEquals(expResult, result);
        
        j = 4;
        outcome = outcomesSequence1[j];
        result = instance.validSequence(j, inputSequence, outcomesSequence1, outcome);
        assertNotEquals(expResult, result);
    }

}
