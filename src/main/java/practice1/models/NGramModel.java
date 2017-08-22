package practice1.models;

import practice1.Index;
import practice1.Lemmatizer;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.utilities.StringUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by romdhane on 20/07/17.
 */
public class NGramModel {
    static LinkedList<Document> sentences = new LinkedList<>();
    private List<Document> resultList = new ArrayList<>();
    private Lemmatizer lem;
    private String nlp_method;
    private Index index;
    private int vocab_size;
    private int ngram;


    public NGramModel(String nlp_method, Lemmatizer lem, Index index, int ngram) {
        this.nlp_method = nlp_method;
        this.lem = lem;
        this.index = index;
        this.ngram = ngram;
    }

    public void setN(int n) {
        this.ngram = n;
    }

    public List<String> ngrams(String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - ngram + 1; i++)
            ngrams.add(concat(words, i, i + ngram));

        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }



    public int computeIntersection(String querySample, String docSample) {

        List<String> ngramsQuery = ngrams(querySample);
        final List<String> ngramsDoc = ngrams(docSample);
        List<String> listIntersect = new ArrayList<>();
        int inter = 0;
        for (String q : ngramsQuery) {
            for (String d : ngramsDoc) {
                String[] tokens_d = d.split(" ");
                for (String st : tokens_d) {

                    if ((q.toLowerCase().indexOf(st.toLowerCase()) != -1) && (!listIntersect.contains(d)))
                        listIntersect.add(d);
                }
            }
        }

            inter = listIntersect.size();
        return inter;
    }

    public int computeUnion(String querySample, String docSample) {

        List<String> ngramsQuery = ngrams(querySample);
        List<String> ngramsDoc = ngrams(docSample);
        List<String> listUnion = new ArrayList<>();
        int union = 0;
        for (String q : ngramsQuery) {
            if (!listUnion.contains(q))
                listUnion.add(q);
        }
        for (String d : ngramsDoc) {
            if (!listUnion.contains(d))
                listUnion.add(d);
        }
        union = listUnion.size();

        return union;
    }

    public double computeJaccard(String querySample, String docSample) {

        int intersect = computeIntersection(querySample, docSample);
        int union = computeUnion(querySample, docSample);
        double jaccard =  intersect / (double) union;

        return jaccard;
     }
    public double computeDice(String querySample, String docSample) {

        int intersect = computeIntersection(querySample, docSample);
        String[]tokensq =  querySample.split(" ");
        String[]tokensd =  querySample.split(" ");
        double jaccard =  intersect / (double) (tokensq.length +tokensd.length);

        return jaccard;
    }

    /*public double computeProbability(String term, String sample) throws IOException {
        VectorSpaceModel vsm = new VectorSpaceModel();

        double prob = (computeFrequency(sample+"\\s"+term) + lambda) /(double)(computeFrequency(sample) + lambda*vocab_size) ;
        return prob;
    }

    public double computeProbSentence(String sentence)throws IOException{

        double prod = 1;
        String[] tokens = sentence.split(" ");
        String pred = "";
        for (int i=0; i<tokens.length; i++ ) {
            String succ = tokens[i];
            double p = computeProbability(succ, pred);
            prod*= p;
            String predSample ="";
            for(int j=1; j<i+1; j++){
                predSample= predSample+"\t"+tokens[j];
            }
            pred=predSample;
        }
        return prod;
    }*/

   /* public Map<Document, Map<String, Double>> computeFeatures(EvaluationEntity e, String similarity) throws IOException {
        Map<Document, Map<String, Double>> nGramsFeatures = new HashMap<Document, Map<String, Double>>();
        Map<Document, List<String>> ngrams = ngramSamples();

        for (Document st : ngrams.keySet()) {

            Map<String, Double> ngramMap = new HashMap<>();
            double sum =0;

            for (String sentence : ngrams.get(st)) {

                if(similarity.equals("Jaccard"))
                ngramMap.put(sentence, computeJaccard(e.getQuery().getContent(), sentence));
                else if(similarity.equals("Dice")) ngramMap.put(sentence, computeDice(e.getQuery().getContent(), sentence));
                sum += computeJaccard(e.getQuery().getContent(), sentence);
            }
            //average = sum/ngrams.size();
            nGramsFeatures.put(st, ngramMap);

            }

        return nGramsFeatures;
    }*/

    public List<Document> getRankingScoresNgram(EvaluationEntity e, String similarity) throws IOException {

        //Map<Document, Map<String, Double>> nGramsFeatures = computeFeatures(e, "Jaccard");
        StringUtilities su = new StringUtilities();
        for (Document st : index.nlpToDocs(index.getDocuments(), nlp_method)) {
            double score = 0.0;
            Document resultdoc = new Document();
            resultdoc.setId(st.getId());
            resultdoc.setContent(st.getContent());
            Document newQuery = index.nlpToDoc(e.getQuery(), nlp_method);
            if(su.hasOneToken(e.getQuery().getContent())==true){
            score = computeJaccard(newQuery.getContent(), su.getAcronym(st.getContent()));
            }
                else
            {
                score = computeJaccard(newQuery.getContent(),st.getContent());
          }
            //if(Double.isNaN(score)) score = 0.0;
            resultdoc.setScore(score);
            resultList.add(resultdoc);
            //System.out.println("average:" + "\t" + average);

        }
        return  resultList;


    }

}







