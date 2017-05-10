package com.kidden.tc.wordseg.sighan;

import com.kidden.tc.wordseg.maxent.DefaultWordSegContextGenerator;
import com.kidden.tc.wordseg.maxent.FileInputStreamFactory;
import com.kidden.tc.wordseg.maxent.WordSegContextGenerator;
import com.kidden.tc.wordseg.maxent.WordSegContextGeneratorClosed;
import com.kidden.tc.wordseg.maxent.WordSegContextGeneratorConf;
import com.kidden.tc.wordseg.maxent.WordSegCrossValidator;
import com.kidden.tc.wordseg.maxent.WordSegErrorPrinter;
import com.kidden.tc.wordseg.maxent.WordSegEvaluator;
import com.kidden.tc.wordseg.maxent.WordSegMeasure;
import com.kidden.tc.wordseg.maxent.WordSegModel;
import com.kidden.tc.wordseg.maxent.WordSegSample;
import com.kidden.tc.wordseg.maxent.WordSegmenterME;
import com.kidden.tc.wordseg.maxent.WordTagSampleStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Experiment utility class for SIGHAN Bakeoff
 *
 * @author kidden
 */
public class SighanRun {

    public static class Corpus {

        public String name;
        public String encoding;
        public String trainFile;
        public String goldFile;
        public String modelFile;
        public String errorFile;
    }

    private static String[] corpusName = {"pku", "msr", "cityu", "as"};

    public static Corpus[] getCorporaFromConf(Properties config) throws IOException {
//        Properties config = new Properties();
//        InputStream configStream = SighanRun.class.getClassLoader().getResourceAsStream("com/kidden/tc/wordseg/sighan/corpus.properties");
//        config.load(configStream);

        Corpus[] corpora = new Corpus[corpusName.length];
        for (int i = 0; i < corpusName.length; i++) {
            String name = corpusName[i];
            String encoding = config.getProperty(name + "." + "corpus.encoding");
            String corpusFile = config.getProperty(name + "." + "corpus.train.file");
//        String dictFile = config.getProperty(corpus + "." + "corpus.dict.file");
//        String testFile = config.getProperty(corpus + "." + "corpus.test.file");
            String goldFile = config.getProperty(name + "." + "corpus.gold.file");
            String modelFile = config.getProperty(name + "." + "corpus.model.file");
            String errorFile = config.getProperty(name + "." + "corpus.error.file");
//        String resultFile = config.getProperty(corpus + "." + "corpus.test.result.file");
            Corpus corpus = new Corpus();
            corpus.name = name;
            corpus.encoding = encoding;
            corpus.trainFile = corpusFile;
            corpus.goldFile = goldFile;
            corpus.modelFile = modelFile;
            corpus.errorFile = errorFile;

            corpora[i] = corpus;
        }

        return corpora;
    }

    public static Corpus[] getCorporaFromConf() throws IOException {
        Properties config = new Properties();
        InputStream configStream = SighanRun.class.getClassLoader().getResourceAsStream("com/kidden/tc/wordseg/sighan/corpus.properties");
        config.load(configStream);

        Corpus[] corpora = new Corpus[corpusName.length];
        for (int i = 0; i < corpusName.length; i++) {
            String name = corpusName[i];
            String encoding = config.getProperty(name + "." + "corpus.encoding");
            String corpusFile = config.getProperty(name + "." + "corpus.train.file");
//        String dictFile = config.getProperty(corpus + "." + "corpus.dict.file");
//        String testFile = config.getProperty(corpus + "." + "corpus.test.file");
            String goldFile = config.getProperty(name + "." + "corpus.gold.file");
            String modelFile = config.getProperty(name + "." + "corpus.model.file");
            String errorFile = config.getProperty(name + "." + "corpus.error.file");
//        String resultFile = config.getProperty(corpus + "." + "corpus.test.result.file");
            Corpus corpus = new Corpus();
            corpus.name = name;
            corpus.encoding = encoding;
            corpus.trainFile = corpusFile;
            corpus.goldFile = goldFile;
            corpus.modelFile = modelFile;
            corpus.errorFile = errorFile;

            corpora[i] = corpus;
        }

        return corpora;
    }

    public static Corpus getCorpus(Corpus[] corpora, String name) {
        for (Corpus c : corpora) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Training and evluating on corpus using some features set
     *
     * @param contextGenerator
     */
    public static void runFeatureOnCorpus(WordSegContextGenerator contextGenerator, Corpus corpus, TrainingParameters params) throws IOException {
        System.out.println("ContextGenerator: " + contextGenerator);
        System.out.println("Training on " + corpus.name + "...");
        WordSegModel model = WordSegmenterME.train(new File(corpus.trainFile), params, contextGenerator, corpus.encoding);

        System.out.println("Building dictionary on " + corpus.name + "...");
        HashSet<String> dict = WordSegmenterME.buildDictionary(new File(corpus.trainFile), corpus.encoding);

        System.out.println("Evaluating on " + corpus.name + "...");
        WordSegmenterME tagger = new WordSegmenterME(model, contextGenerator);
        WordSegMeasure measure = new WordSegMeasure(dict);
        WordSegErrorPrinter errorMonitor = null;
        WordSegEvaluator evaluator = null;
        if (corpus.errorFile != null) {
            System.out.println("Print error to file " + corpus.errorFile);
            errorMonitor = new WordSegErrorPrinter(new FileOutputStream(corpus.errorFile));
            evaluator = new WordSegEvaluator(tagger, errorMonitor);
        } else {
            evaluator = new WordSegEvaluator(tagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.goldFile)), corpus.encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);
        evaluator.evaluate(sampleStream);
        WordSegMeasure f = evaluator.getMeasure();
        System.out.println(f);

    }

    /**
     * Training and evluating on corpora using some features set
     *
     * @param contextGenerator
     */
    public static void runFeatureOnCorpora(WordSegContextGenerator contextGenerator, Corpus[] corpora, TrainingParameters params) throws IOException {
        for (Corpus corpus : corpora) {
            runFeatureOnCorpus(contextGenerator, corpus, params);
        }
    }

    private static WordSegContextGenerator getContextGenerator(Properties conf) throws IOException {
//        Properties featureConf = new Properties();
//        InputStream featureStream = SighanRun.class.getClassLoader().getResourceAsStream("com/kidden/tc/wordseg/maxent/sighan/feature.properties");
//        featureConf.load(featureStream);
//        WordSegContextGeneratorConf contextGen = new WordSegContextGeneratorConf(featureConf);

        String featureClass = conf.getProperty("feature.class");
        if(featureClass.equals("com.kidden.tc.wordseg.maxent.WordSegContextGeneratorConf"))
            return  new WordSegContextGeneratorConf(conf);
        else
            return new WordSegContextGeneratorClosed();
    }

    /**
     * Cross validate on given corpus
     *
     * @param corpusName
     * @throws IOException
     */
    private static void crossValidate(String corpusName) throws IOException {
        Properties config = new Properties();
        InputStream configStream = SighanRun.class.getClassLoader().getResourceAsStream("com/kidden/tc/wordseg/maxent/sighan/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);
        Corpus corpus = getCorpus(corpora, corpusName);

        WordSegContextGenerator contextGen = getContextGenerator(config);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));

        WordSegCrossValidator crossValidator = new WordSegCrossValidator("zh", params);

        System.out.println(contextGen);
        crossValidator.evaluate(sampleStream, 10, contextGen);
    }

    public static void main(String[] args) throws IOException {
        String cmd = args[0];
        if (cmd.equals("-run")) {
            runFeature();
        } else if (cmd.equals("-cross")) {
            String corpus = args[1];

            crossValidate(corpus);
        }
    }

    /**
     * Traing and test on 4 corpors using one feature set
     *
     * @throws IOException
     */
    private static void runFeature() throws IOException {
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));

        Properties config = new Properties();
        InputStream configStream = SighanRun.class.getClassLoader().getResourceAsStream("com/kidden/tc/wordseg/maxent/sighan/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);

        WordSegContextGenerator contextGen = getContextGenerator(config);

        runFeatureOnCorpora(contextGen, corpora, params);
    }
}
