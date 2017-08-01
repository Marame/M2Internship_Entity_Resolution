package Models;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by romdhane on 20/07/17.
 */
public class NGram {
    static LinkedList<String> sentences = new LinkedList<String>();
    //smoothing_factor
    private double lambda = 0.5;
    ;
    private int vocab_size;
    private String filenameQueries;
    private String filenameDocuments;
    private int n;
    private double average;
    public void setN(int n) {
        this.n = n;
    }

    public double getAverage() {
        return average;
    }

    public void setFilenameDocuments(String filenameDocuments) {
        this.filenameDocuments = filenameDocuments;
    }

    public void setVocab_size(int vocab_size) {
        this.vocab_size = vocab_size;
    }

    public List<String> ngrams(String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i + n));
        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }


    public Map<String, List<String>> ngramSamples() throws IOException {

        FileReader frd = new FileReader(filenameDocuments);
        CSVReader brd = new CSVReader(frd);
        //FileReader frq = new FileReader(filenameQueries);
        //CSVReader brq = new CSVReader(frq);
        Map<String, List<String>> nGrams = new HashMap<String, List<String>>();
        int n = 3;

        String[] lined = null;
        while ((lined = brd.readNext()) != null) {
            sentences.add(lined[1]);
        }
        for (String sentence : sentences) {
            //Map<String, Integer> ngramMap = new HashMap<>();
            //nGrams set
            nGrams.put(sentence, ngrams(sentence));
        }
        return nGrams;
    }

    public int computeFrequency(String sample) throws IOException {
        FileReader fr = new FileReader(filenameDocuments);
        CSVReader br = new CSVReader(fr);
        int count = 0;
        String[] line = null;
        while ((line = br.readNext()) != null) {
            if (line[1].toLowerCase().indexOf(sample.toLowerCase()) != -1) count++;
        }
        return count;

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

                    if ((q.toLowerCase().indexOf(st.toLowerCase()) != -1) && (!listIntersect.contains(st)))
                        listIntersect.add(st);
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

    public Map<String, Map<String, Double>> computeFeatures(String query, String similarity) throws IOException {
        Map<String, Map<String, Double>> nGramsFeatures = new HashMap<String, Map<String, Double>>();
        Map<String, List<String>> ngrams = ngramSamples();
        double average = 0;
        for (String st : ngrams.keySet()) {
            Map<String, Double> ngramMap = new HashMap<>();
            double sum =0;

            for (String sentence : ngrams.get(st)) {

                //String[] tokens = sentence.split(" ");

                /*for (String token : tokens) {
                    if (!token.equals("<S>") && !token.equals("</S>")) {
                        ngramMap.put(token, Collections.frequency(Arrays.asList(tokens), token));
                    } else {
                        ngramMap.put(token, 0);
                    }
                }*/
                if(similarity.equals("Jaccard"))
                ngramMap.put(sentence, computeJaccard(query, sentence));
                if(similarity.equals("Dice")) ngramMap.put(sentence, computeDice(query, sentence));
                sum += computeJaccard(query, sentence);
            }
            average = sum/ngrams.size();
            System.out.println(average);
            nGramsFeatures.put(st, ngramMap);

            }

        return nGramsFeatures;
    }



}







