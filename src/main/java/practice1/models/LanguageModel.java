package practice1.models;

import org.tartarus.snowball.ext.PorterStemmer;
import practice1.Index;
import practice1.Lemmatizer;
import practice1.Main;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.utilities.StringUtilities;

import java.util.*;
import static practice1.Index.LEMMATIZING_NLP_METHOD;
import static practice1.Index.STEMMING_NLP_METHOD;

/**
 * Created by romdhane on 21/06/17.
 */
public class LanguageModel {
    private List<Document> listRankingResults = new ArrayList<>();
    public final static String JELINEK_SMOOTHING = "jelinek-mercer";
    public final static String DIRICHLET_SMOOTHING = "dirichlet-prior";

    public static List<String> smoothing_versions = Arrays.asList(JELINEK_SMOOTHING, DIRICHLET_SMOOTHING);
    private String smoothing_version;
    private String nlp_method;
    private double lambda = 0.4;
    private double mu = 2;

  
    public Lemmatizer lem;
    private Index index;

    public LanguageModel(String smoothing_version, String nlp_method, Lemmatizer lem, Index index) {
        this.smoothing_version = smoothing_version;
        this.nlp_method = nlp_method;
        this.lem = lem;
        this.index = index;
    }


    public List<String> docWords(Document s) {
        List<String> words = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getContent());


        while (st.hasMoreTokens()) {
            if (nlp_method.equals(STEMMING_NLP_METHOD)) {
                PorterStemmer stem = new PorterStemmer();
                stem.setCurrent(st.nextToken());
                stem.stem();
                String result = stem.getCurrent();
                words.add(result);
            } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {

                for (String st_lem : lem.lemmatize(st.nextToken())) {
                    words.add(st_lem);
                }
            }
            else {
            words.add(st.nextToken());}
        }
        return words;
    }

    public double probWordCollection(String word) {
        double sumfreq = 0;
        double sumsize = 0;
        final List<Document> documents = index.getDocuments();

        if (nlp_method.equals(STEMMING_NLP_METHOD)) {
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(word);
            stem.stem();
            String resword = stem.getCurrent();
            for (Document doc : index.nlpToDocs(documents, nlp_method)) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, resword);
                sumfreq += freq;
                sumsize += (double) doc.getContent().length();
            }
        } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {

            String resword = lem.lemmatize(word).get(0);
            for (Document doc : index.nlpToDocs(documents, nlp_method)) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, resword);
                sumfreq += freq;
                sumsize += (double) doc.getContent().length();
            }
        }
        else{
            for (Document doc : documents) {
                List<String> wordsDoc = docWords(doc);
                double freq = (double) Collections.frequency(wordsDoc, word);
                sumfreq += freq;
                sumsize += (double) doc.getContent().length();
            }
        }

        double prob = sumfreq / sumsize;
        //if(DIRICHLET_SMOOTHING.equals(smoothing_version)) System.out.println(prob);

        return prob;
    }

    public double probWord(String word, Document doc, String smoothing_version) {
        double smoothedProb = 0;

        List<String> wordsDoc = docWords(doc);

        if (nlp_method.equals(STEMMING_NLP_METHOD)) {
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(word);
            stem.stem();
            String resword = stem.getCurrent();
            double freq = (double) Collections.frequency(wordsDoc, resword);
            //double prob = freq / (double) doc.getContent().length();
            double probC = probWordCollection(resword);
            if (smoothing_version.equals(Main.JELINEK_SMOOTHING)) {
                smoothedProb = (((1 - lambda) / lambda) * freq) / probC;


            } else if (smoothing_version.equals(DIRICHLET_SMOOTHING)) {
                smoothedProb = freq /(mu * probC);
            }
        } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {

            String resword = lem.lemmatize(word).get(0);
            double freq = (double) Collections.frequency(wordsDoc, resword);
            //double prob = freq / (double) doc.getContent().length();
            double probC = probWordCollection(resword);
            if (smoothing_version.equals(Main.JELINEK_SMOOTHING)) {
                smoothedProb = (((1 - lambda) / lambda) * freq) / probC;

            } else if (smoothing_version.equals(Main.DIRICHLET_SMOOTHING)) {
                smoothedProb = freq /(mu * probC);

            }
        }
        else {
            double freq = (double) Collections.frequency(wordsDoc, word);
            double probC = probWordCollection(word);
            if (smoothing_version.equals(Main.JELINEK_SMOOTHING)) {
                smoothedProb = (((1 - lambda) / lambda) * freq) / probC;

            } else if (smoothing_version.equals(Main.DIRICHLET_SMOOTHING)) {
                smoothedProb = freq /(mu * probC);

            }
        }
        return smoothedProb;
    }

    public List<Document> getRankingScoresLM(EvaluationEntity e) {

        StringUtilities su = new StringUtilities();
        final List<Document> documents = index.getDocuments();

        List<String> listwordsQuery = docWords(e.getQuery());

        for (Document doc : index.nlpToDocs(documents, nlp_method)) {

            Document resultdoc = new Document();
            resultdoc.setId(doc.getId());
            resultdoc.setContent(doc.getContent());

            double sum_jmdp = 0.0;

                for (String word : listwordsQuery) {
                    double freqWordQuery = (double) Collections.frequency(listwordsQuery, word);

                    if (doc.getContent().toLowerCase().indexOf(word.toLowerCase()) != -1) {
                        if(su.hasOneToken(e.getQuery().getContent())==false) {
                           if(Double.isNaN(probWord(word, doc, smoothing_version))) continue;
                            sum_jmdp += freqWordQuery * Math.log(1 + probWord(word, doc, smoothing_version));
                        }
                        else {
                            doc.setContent(su.getAcronym(doc.getContent()));
                            sum_jmdp += freqWordQuery * Math.log(1 + probWord(word, doc, smoothing_version));
                        }
                        if (smoothing_version.equals(Main.DIRICHLET_SMOOTHING)) {
                            double smooth_factor = listwordsQuery.size() * (mu / (mu + docWords(doc).size()));
                            sum_jmdp += smooth_factor;
                        }
                    } else continue;
                }
                resultdoc.setScore(sum_jmdp);
                listRankingResults.add(resultdoc);
            }

            return listRankingResults;
        }

    }