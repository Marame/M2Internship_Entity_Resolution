package practice1.models;

import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.utilities.Lemmatizer;
import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by romdhane on 21/06/17.
 */
public class LanguageModel {
    private List<Document> listRankingResults = new ArrayList<>();
    private String smoothing_version;
    private String nlp_method;
    private double lambda = 0.4;
    private double mu = 2;
    private Lemmatizer lem;

    public LanguageModel(String smoothing_version, String nlp_method, Lemmatizer lem) {
        this.smoothing_version = smoothing_version;
        this.nlp_method = nlp_method;
        this.lem = lem;
    }

    public LanguageModel(String smoothing_version, String nlp_method) {
        this.smoothing_version = smoothing_version;
        this.nlp_method = nlp_method;
    }

    public List<String> docWords(Document s) {
        List<String> words = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getName());


        while (st.hasMoreTokens()) {
            if (nlp_method.equals("stemming")) {
                PorterStemmer stem = new PorterStemmer();
                stem.setCurrent(st.nextToken());
                stem.stem();
                String result = stem.getCurrent();
                words.add(result);
            } else if (nlp_method.equals("lemmatizing")) {

                for (String st_lem : lem.lemmatize(st.nextToken())) {
                    words.add(st_lem);
                }
            }
            else {
            words.add(st.nextToken());}
        }
        return words;
    }

    public double probWordCollection(String word, EvaluationEntity e) {
        double sumfreq = 0;
        double sumsize = 0;

        if (nlp_method.equals("stemming")) {
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(word);
            stem.stem();
            String resword = stem.getCurrent();
            for (Document doc : e.getDocuments()) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, resword);
                sumfreq += freq;
                sumsize += (double) doc.getName().length();
            }
        } else if (nlp_method.equals("lemmatizing")) {

            String resword = lem.lemmatize(word).get(0);
            for (Document doc : e.getDocuments()) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, resword);
                sumfreq += freq;
                sumsize += (double) doc.getName().length();
            }
        }
        else{
            for (Document doc : e.getDocuments()) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, word);
                sumfreq += freq;
                sumsize += (double) doc.getName().length();
            }

        }

        double prob = sumfreq / sumsize;

        return prob;
    }

    public double probWord(String word, Document doc, EvaluationEntity e, String smoothing_version) {
        double smoothedProb = 0;

        List<String> wordsDoc = docWords(doc);

        if (nlp_method.equals("stemming")) {
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(word);
            stem.stem();
            String resword = stem.getCurrent();
            double freq = (double) Collections.frequency(wordsDoc, resword);
            double prob = freq / (double) doc.getName().length();
            double probC = probWordCollection(resword, e);
            if (smoothing_version.equals("jelinek-mercer")) {
                smoothedProb = (((1 - lambda) / lambda) * prob) / probC;

            } else if (smoothing_version.equals("dirichlet-prior")) {
                smoothedProb = freq * (mu * probC);

            }
        } else if (nlp_method.equals("lemmatizing")) {

            String resword = lem.lemmatize(word).get(0);
            double freq = (double) Collections.frequency(wordsDoc, resword);
            double prob = freq / (double) doc.getName().length();
            double probC = probWordCollection(resword, e);
            if (smoothing_version.equals("jelinek-mercer")) {
                smoothedProb = (((1 - lambda) / lambda) * prob) / probC;

            } else if (smoothing_version.equals("dirichlet-prior")) {
                smoothedProb = freq * (mu * probC);
            }
        }
        else {
            double freq = (double) Collections.frequency(wordsDoc, word);
            double prob = freq / (double) doc.getName().length();
            double probC = probWordCollection(word, e);
            if (smoothing_version.equals("jelinek-mercer")) {
                smoothedProb = (((1 - lambda) / lambda) * prob) / probC;

            } else if (smoothing_version.equals("dirichlet-prior")) {
                smoothedProb = freq /(mu * probC);

            }
        }

        return smoothedProb;
    }

    public List<Document> getRankingScoresLM(EvaluationEntity e, String smoothing_version) {

        for (Document doc : e.getDocuments()) {
            Document resultdoc = new Document();
            double sum_jm = 0;
            double sum_dp = 0;
            List<String> listwordsQuery = docWords(e.getQuery());

                for (String word : listwordsQuery) {

                    if (doc.getName().toLowerCase().indexOf(word.toLowerCase()) != -1) {
                        double freqWordQuery = (double) Collections.frequency(listwordsQuery, word);

                        sum_dp += freqWordQuery * Math.log(1 + probWord(word, doc, e, smoothing_version));
                    }
                    if(smoothing_version.equals("dirichlet-prior")){
                        double smooth_factor = listwordsQuery.size() * (mu / (mu + docWords(doc).size()));
                       sum_dp+= smooth_factor;
                    }
                    resultdoc.setName(doc.getName());
                    resultdoc.setScore(sum_dp );

            }
            listRankingResults.add(resultdoc);
        }
        return listRankingResults;
    }

}