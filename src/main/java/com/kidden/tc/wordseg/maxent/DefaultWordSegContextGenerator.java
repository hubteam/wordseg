package com.kidden.tc.wordseg.maxent;

import java.util.ArrayList;
import java.util.List;

/**
 * Default word segmentation context generator
 * 
 * Feature template: Cn(-2<=n<=2), c-1c1, cncn+1(-2<=n<=1)
 * 
 * @author kidden
 */
public class DefaultWordSegContextGenerator implements WordSegContextGenerator {

    public String[] getContext(int index, String[] sequence, String[] priorDecisions, Object[] additionalContext) {
        return getContext(index, sequence, priorDecisions);
    }
    

    public String[] getContext(int index, Object[] tokens, String[] tags) {
        String c1, c2, c0, c_1, c_2;
        c1 = c2 = c0 = c_1 = c_2 = null;
       
        c0 = tokens[index].toString();
         
        if (tokens.length > index + 1) {
            c1 = tokens[index + 1].toString();
                     
            if (tokens.length > index + 2) {
                c2 = tokens[index + 2].toString();             
            }
        }

        if (index - 1 >= 0) {
            c_1 = tokens[index - 1].toString();

            if (index - 2 >= 0) {
                c_2 = tokens[index - 2].toString();
            }
        }

        List<String> features = new ArrayList<String>();
        // add the word itself
        features.add("c0=" + c0);

        if (c_1 != null) {
            features.add("c_1=" + c_1);

            if (c_2 != null) {
                features.add("c_2" + c_2);
            }
        }

        if (c1 != null) {
            features.add("c1=" + c1);
            if (c2 != null) {
                features.add("c2=" + c2);
            }
        }

        if (c_2 != null && c_1 != null) {
            features.add("c_2c_1=" + c_2 + c_1);
        }

        if (c_1 != null) {
            features.add("c_1c0=" + c_1 + c0);
        }

        if (c1 != null) {
            features.add("c0c1=" + c0 + c1);
        }

        if (c1 != null && c2 != null) {
            features.add("c1c2=" + c1 + c2);
        }

        if (c_1 != null && c1 != null) {
            features.add("c_1c1=" + c_1 + c1);
        }

        String[] contexts = features.toArray(new String[features.size()]);

        return contexts;
    }

}
