package Models;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by romdhane on 20/07/17.
 */
public class NGram {
    static LinkedList<String> sentences = new LinkedList<String>();




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


    public Map<String, List<String>> ngramSamples(String filenameQueries, String filenameDocs) throws IOException {

        FileReader frd = new FileReader(filenameDocs);
        CSVReader brd = new CSVReader(frd);
        FileReader frq = new FileReader(filenameQueries);
        CSVReader brq = new CSVReader(frq);
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

    public int computeFrequency(String filename, String sample) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(filename);
        CSVReader br = new CSVReader(fr);
        int count = 0;
        String[] line = null;
        while ((line = br.readNext()) != null) {
            if (line[1].indexOf(sample)!=-1) count++;
        }
        return count;

    }



    public double computeProbability(String term, String sample, String filenameQueries) throws FileNotFoundException, IOException {

        double prob = computeFrequency(filenameQueries, sample) /(double)computeFrequency( filenameQueries, term);
        return prob;

    }
    public Map<String, Map<String, Double>> computeFeature(String term, String filenameQueries, String filenameDocs) throws IOException {
        Map<String, Map<String, Double>> nGramsFeature = new HashMap<String, Map<String, Double>>();
        Map<String, List<String>> ngrams = ngramSamples(filenameQueries, filenameDocs);


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
                ngramMap.put(sentence, computeProbability(term, sentence, filenameQueries));}
                nGramsFeature.put(st, ngramMap);
            }

        return nGramsFeature;
    }


}







