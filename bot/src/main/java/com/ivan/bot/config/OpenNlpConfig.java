package com.ivan.bot.config;

import com.ivan.bot.util.ModelLoader;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.TokenizerME;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

@Slf4j
@Configuration
public class OpenNlpConfig {

    @Bean
    public TokenizerME opennlpTokenizer() throws IOException {
        return new TokenizerME(ModelLoader.loadTokenizerModel("en-token.bin"));
    }

    @Bean
    public NameFinderME locationFinder() throws IOException {
        return new NameFinderME(ModelLoader.loadNerModel("en-ner-location.bin"));
    }
}