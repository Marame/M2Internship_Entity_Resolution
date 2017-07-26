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
    private double lambda = 0.5; ;
   private int vocab_size;
   private String filenameQueries;
    private String filenameDocuments;
    private int n;

    public void setN(int n) {
        this.n = n;
    }



    public void setFilenameDocuments(String filenameDocuments) {
        this.filenameDocuments = filenameDocuments;
    }

    public void setVocab_size(int vocab_size) {
        this.vocab_size = vocab_size;
    }

    public static List<String> ngrams(int n, String str) {
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
            sentences.add("<S> " + lined[1] + " </S>");
        }
        for (String sentence : sentences) {
            //Map<String, Integer> ngramMap = new HashMap<>();
            //nGrams set
            nGrams.put(sentence, ngrams(n, sentence));
        }
        return nGrams;
    }

    public int computeFrequency( String sample) throws IOException {
        FileReader fr = new FileReader(filenameDocuments);
        CSVReader br = new CSVReader(fr);
        int count = 0;
        String[] line = null;
        while ((line = br.readNext()) != null) {
            if (line[1].toLowerCase().indexOf(sample.toLowerCase())!=-1) count++;
        }
        return count;

    }


    public double computeProbability(String term, String sample) throws IOException {
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
    }

    public Map<String, Map<String, Double>> computeFeatures(String term) throws IOException {
        Map<String, Map<String, Double>> nGramsFeatures = new HashMap<String, Map<String, Double>>();
        Map<String, List<String>> ngrams = ngramSamples();

        for (String st : ngrams.keySet()) {
            Map<String, Double> ngramMap = new HashMap<>();
            for (String sentence : ngrams.get(st)) {

                //String[] tokens = sentence.split(" ");

                /*for (String token : tokens) {
                    if (!token.equals("<S>") && !token.equals("</S>")) {
                        ngramMap.put(token, Collections.frequency(Arrays.asList(tokens), token));
                    } else {
                        ngramMap.put(token, 0);
                    }
                }*/
                ngramMap.put(sentence, computeProbSentence(sentence+"\t"+term));}
                nGramsFeatures.put(st, ngramMap);
            }

        return nGramsFeatures;
    }



}







