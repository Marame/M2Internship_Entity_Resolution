package practice1;

import org.apache.commons.lang3.StringUtils;
import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static practice1.Index.LEMMATIZING_NLP_METHOD;
import static practice1.Index.STEMMING_NLP_METHOD;

public class Tokenizer {

    private Lemmatizer lem;
    private PorterStemmer stemmer;

    public static final String punctuations = " •*,:;?.!";

    public Tokenizer(Lemmatizer lem, PorterStemmer stemmer) {
        this.lem = lem;
        this.stemmer = stemmer;
    }

    public List<String> tokenise(String input, String nlp_method) {
        List<String> tokenised = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(input);

        while (st.hasMoreTokens()) {
            if (nlp_method.equals(STEMMING_NLP_METHOD)) {
                stemmer.setCurrent(st.nextToken());
                stemmer.stem();
                String result = stemmer.getCurrent();
                tokenised.add(result);
            } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {
                for (String st_lem : lem.lemmatize(st.nextToken())) {
                    tokenised.add(st_lem);
                }
            } else {
                String token = st.nextToken();
                String cleanToken = token.replaceAll(punctuations, "");
                if(isNotEmpty(cleanToken)) {
                    tokenised.add(StringUtils.lowerCase(cleanToken));
                }
            }
        }
        return tokenised;

    }
}