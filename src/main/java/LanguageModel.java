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
    private double lambda = 0.4;
    private double mu = 2;

    public LanguageModel(String smoothing_version) {
        this.smoothing_version = smoothing_version;
    }

    public List<String> docWords(Document s) {
        List<String> words = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getName());
        while (st.hasMoreTokens()) {

            words.add(st.nextToken());
        }
        return words;
    }

    public double probWordCollection(String word, EvaluationEntity e) {
        double sumfreq = 0;
        double sumsize = 0;

        for (Document doc : e.getDocuments()) {

            List<String> wordsDoc = docWords(doc);
            double freq = (double) Collections.frequency(wordsDoc, word);
            sumfreq += freq;
            sumsize += (double) doc.getName().length();
        }
        double prob = sumfreq / sumsize;

        return prob;
    }

    public double probWord(String word, Document doc, EvaluationEntity e, String smoothing_version) {
        double smoothedProb = 0;

        List<String> wordsDoc = docWords(doc);
        double freq = (double) Collections.frequency(wordsDoc, word);
        double prob = freq / (double) doc.getName().length();
        double probC = probWordCollection(word, e);
        if (smoothing_version == "jelinek-mercer") {
            smoothedProb = (((1 - lambda) / lambda) * prob) / probC;

        } else if (smoothing_version == "dirichlet-prior") {
            smoothedProb = freq * (mu * probC);

        }
        return smoothedProb;
    }

    public List<Document> getRankingScoresLM(EvaluationEntity e, String smoothing_version) {

        for (Document doc : e.getDocuments()) {
            Document resultdoc = new Document();
            //resultdoc.setName(doc.getName());
            double sum_jm = 0;
            double sum_dp = 0;
            List<String> listwordsQuery = docWords(e.getQuery());
            if (smoothing_version.equals("jelinek-mercer")) {
                for (String word : listwordsQuery) {

                    if (doc.getName().toLowerCase().indexOf(word.toLowerCase()) != -1) {
                        double freqWordQuery = (double) Collections.frequency(listwordsQuery, word);

                        sum_jm += freqWordQuery * Math.log(1 + probWord(word, doc, e, "jelinek-mercer"));
                    }

                    resultdoc.setName(doc.getName());
                    resultdoc.setScore(sum_jm);
                }
            } else if (smoothing_version.equals("dirichlet-prior")) {
                for (String word : listwordsQuery) {

                    if (doc.getName().toLowerCase().indexOf(word.toLowerCase()) != -1) {
                        double freqWordQuery = (double) Collections.frequency(listwordsQuery, word);

                        sum_dp += freqWordQuery * Math.log(1 + probWord(word, doc, e, "dirichlet-prior"));
                    }
                    double smooth_factor = (listwordsQuery.size() * mu) / (mu + docWords(doc).size());
                    resultdoc.setName(doc.getName());
                    resultdoc.setScore(sum_dp + smooth_factor);
                }
            }
            listRankingResults.add(resultdoc);
        }
        return listRankingResults;
    }

}