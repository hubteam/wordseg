package com.kidden.tc.wordseg.maxent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.BaseModel;

/**
 * The {@link WordSegModel} is the model used by a learnable
 * {@link WordSegmenter}.
 *
 * @see WordSegmenterME
 */
public final class WordSegModel extends BaseModel {

    private static final String COMPONENT_NAME = "WordSegmenterME";

    public static final String WORDSEG_MODEL_ENTRY_NAME = "wordseg.model";

    public WordSegModel(String languageCode, SequenceClassificationModel<String> wordsegModel,
            Map<String, String> manifestInfoEntries) {

        super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);

        if (wordsegModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }

        artifactMap.put(WORDSEG_MODEL_ENTRY_NAME, wordsegModel);
        // TODO: This fails probably for the sequence model ... ?!
        // checkArtifactMap();
    }

    public WordSegModel(String languageCode, MaxentModel wordsegModel,
            Map<String, String> manifestInfoEntries) {
        this(languageCode, wordsegModel, WordSegmenterME.DEFAULT_BEAM_SIZE, manifestInfoEntries);
    }

    public WordSegModel(String languageCode, MaxentModel wordsegModel, int beamSize,
            Map<String, String> manifestInfoEntries) {

        super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);

        if (wordsegModel == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(BeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        artifactMap.put(WORDSEG_MODEL_ENTRY_NAME, wordsegModel);
        checkArtifactMap();
    }

    public WordSegModel(InputStream in) throws IOException, InvalidFormatException {
        super(COMPONENT_NAME, in);
    }

    public WordSegModel(File modelFile) throws IOException, InvalidFormatException {
        super(COMPONENT_NAME, modelFile);
    }

    public WordSegModel(URL modelURL) throws IOException, InvalidFormatException {
        super(COMPONENT_NAME, modelURL);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void createArtifactSerializers(
            Map<String, ArtifactSerializer> serializers) {

        super.createArtifactSerializers(serializers);
    }

    @Override
    protected void validateArtifactMap() throws InvalidFormatException {
        super.validateArtifactMap();

        if (!(artifactMap.get(WORDSEG_MODEL_ENTRY_NAME) instanceof MaxentModel)) {
            throw new InvalidFormatException("WordSeg model is incomplete!");
        }
    }

    /**
     * @deprecated use getWordSegSequenceModel instead. This method will be removed
     * soon.
     */
    @Deprecated

    public MaxentModel getWordSegModel() {
        if (artifactMap.get(WORDSEG_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(WORDSEG_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }

    public SequenceClassificationModel<String> getWordSegSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(WORDSEG_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(BeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = WordSegmenterME.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new BeamSearch<>(beamSize, (MaxentModel) artifactMap.get(WORDSEG_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(WORDSEG_MODEL_ENTRY_NAME) instanceof SequenceClassificationModel) {
            return (SequenceClassificationModel) artifactMap.get(WORDSEG_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }

}
