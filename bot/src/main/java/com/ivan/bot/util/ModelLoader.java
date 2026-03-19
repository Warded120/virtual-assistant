package com.ivan.bot.util;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ModelLoader {

    public static TokenizerModel loadTokenizerModel(String filename) throws IOException {
        try (InputStream in = open(filename)) {
            log.info("Loading tokenizer model: {}", filename);
            return new TokenizerModel(in);
        }
    }

    public static TokenNameFinderModel loadNerModel(String filename) throws IOException {
        try (InputStream in = open(filename)) {
            log.info("Loading NER model: {}", filename);
            return new TokenNameFinderModel(in);
        }
    }

    private static InputStream open(String filename) throws IOException {
        InputStream cp = ModelLoader.class.getResourceAsStream("/models/" + filename);
        if (cp != null) {
            log.debug("Model '{}' loaded from classpath", filename);
            return cp;
        }

        throw new IOException(
            "OpenNLP model '" + filename + "' not found.\n" +
            "Download it from https://opennlp.sourceforge.net/models-1.5/\n" +
            "and place it in src/main/resources/models"
        );
    }
}