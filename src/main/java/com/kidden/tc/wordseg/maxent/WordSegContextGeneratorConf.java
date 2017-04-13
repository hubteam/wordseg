package com.kidden.tc.wordseg.maxent;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Generate features from configuration file
 *
 * @author kidden
 */
public class WordSegContextGeneratorConf implements WordSegContextGenerator {

    private boolean c_2Set;
    private boolean c_1Set;
    private boolean c0Set;
    private boolean c1Set;
    private boolean c2Set;
    private boolean c_2c_1Set;
    private boolean c_1c0Set;
    private boolean c0c1Set;
    private boolean c1c2Set;
    private boolean c_1c1Set;
    private boolean t_2Set;
    private boolean t_1Set;

    private boolean c_2c0set;
    private boolean c_1c0c1set;
    private boolean coprefix;

    public WordSegContextGeneratorConf(Properties config) {
        c_2Set = (config.getProperty("feature.c_2", "true").equals("true"));
        c_1Set = (config.getProperty("feature.c_1", "true").equals("true"));
        c0Set = (config.getProperty("feature.c0", "true").equals("true"));
        c1Set = (config.getProperty("feature.c1", "true").equals("true"));
        c2Set = (config.getProperty("feature.c2", "true").equals("true"));

        c_2c_1Set = (config.getProperty("feature.c_2c_1", "true").equals("true"));
        c_1c0Set = (config.getProperty("feature.c_1c0", "true").equals("true"));
        c0c1Set = (config.getProperty("feature.c0c1", "true").equals("true"));
        c1c2Set = (config.getProperty("feature.c1c2", "true").equals("true"));

        c_1c1Set = (config.getProperty("feature.c_1c1", "true").equals("true"));

        t_2Set = (config.getProperty("feature.t_2", "true").equals("true"));
        t_1Set = (config.getProperty("feature.t_1", "true").equals("true"));

        c_2c0set = (config.getProperty("feature.c_2c0", "true").equals("true"));
        c_1c0c1set = (config.getProperty("feature.c_1c0c1", "true").equals("true"));
        // TODO
        coprefix = (config.getProperty("feature.c0pre", "true").equals("true"));
    }

    public String[] getContext(int index, String[] sequence, String[] priorDecisions, Object[] additionalContext) {
        return getContext(index, sequence, priorDecisions);
    }

    public String[] getContext(int index, Object[] tokens, String[] tags) {
        String c1, c2, c0, c_1, c_2;
        c1 = c2 = c0 = c_1 = c_2 = null;

        String t_1 = null;
        String t_2 = null;

        c0 = tokens[index].toString();
        if (tokens.length > index + 1) {
            c1 = tokens[index + 1].toString();
            if (tokens.length > index + 2) {
                c2 = tokens[index + 2].toString();
            }
        }

        if (index - 1 >= 0) {
            c_1 = tokens[index - 1].toString();

            t_1 = tags[index - 1];

            if (index - 2 >= 0) {
                c_2 = tokens[index - 2].toString();

                t_2 = tags[index - 2];
            }
        }

        List<String> features = new ArrayList<String>();
        // add the word itself

        if (c0Set) {
            features.add("c0=" + c0);
        }

        if (c_1 != null) {
            if (c_1Set) {
                features.add("c_1=" + c_1);
            }

            if (t_1Set) {
                features.add("t_1=" + t_1);
            }

            if (c_2 != null) {
                if (c_2Set) {
                    features.add("c_2" + c_2);
                }

                if (t_2Set) {
                    features.add("t_2=" + t_2 + "," + t_1);
                }
                
                if(c_2c0set){
                    features.add("c_2c0="+c_2+c0);
                }
            }
        }

        if (c1 != null) {
            if (c1Set) {
                features.add("c1=" + c1);
            }
            if (c2 != null) {
                if (c2Set) {
                    features.add("c2=" + c2);
                }
            }
        }

        if (c_2 != null && c_1 != null) {
            if (c_2c_1Set) {
                features.add("c_2c_1=" + c_2 + c_1);
            }
        }

        if (c_1 != null) {
            if (c_1c0Set) {
                features.add("c_1c0=" + c_1 + c0);
            }
        }

        if (c1 != null) {
            if (c0c1Set) {
                features.add("c0c1=" + c0 + c1);
            }
        }

        if (c1 != null && c2 != null) {
            if (c1c2Set) {
                features.add("c1c2=" + c1 + c2);
            }
        }

        if (c_1 != null && c1 != null) {
            if (c_1c1Set) {
                features.add("c_1c1=" + c_1 + c1);
            }
            
            if(c_1c0c1set){
                features.add("c_1c0c1="+c_1+c0+c1);
            }
        }

        String[] contexts = features.toArray(new String[features.size()]);

        return contexts;
    }

    @Override
    public String toString() {
        return "WordSegContextGeneratorTemplate{" + "c_2Set=" + c_2Set + ", c_1Set=" + c_1Set + ", c0Set=" + c0Set + ", c1Set=" + c1Set + ", c2Set=" + c2Set + ", c_2c_1Set=" + c_2c_1Set + ", c_1c0Set=" + c_1c0Set + ", c0c1Set=" + c0c1Set + ", c1c2Set=" + c1c2Set + ", c_1c1Set=" + c_1c1Set + ", t_2Set=" + t_2Set + ", t_1Set=" + t_1Set + '}';
    }

}
