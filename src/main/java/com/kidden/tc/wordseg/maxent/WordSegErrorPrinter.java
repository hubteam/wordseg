
package com.kidden.tc.wordseg.maxent;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author kidden
 */
public class WordSegErrorPrinter extends WordSegEvaluationMonitor {
    
    private PrintStream errOut;
    
    public WordSegErrorPrinter(OutputStream out){
        errOut = new PrintStream(out);
    }
    
    @Override
    public void missclassified(WordSegSample reference, WordSegSample prediction) {
        errOut.println(reference.toSample());
        errOut.println("[*]" + prediction.toSample());
    }
}
