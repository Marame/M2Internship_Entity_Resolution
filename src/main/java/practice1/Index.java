package practice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice1.entities.Document;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by romdhane on 03/08/17.
 */
public class Index {

    private static Logger LOGGER = LoggerFactory.getLogger(Index.class);

    public final static String NO_NLP_METHOD = "nothing";
    public final static String STEMMING_NLP_METHOD = "stemming";
    public final static String LEMMATIZING_NLP_METHOD = "lemmatizing";

    public List<String> stopWords = new ArrayList<>();

    //Versions of smoothing in Language Model
<<<<<<< HEAD
    public static List<String> nlp_methods = Arrays.asList(NO_NLP_METHOD, STEMMING_NLP_METHOD);
=======
    public static List<String> nlp_methods = Arrays.asList(NO_NLP_METHOD);
>>>>>>> 508e40c4bbe3b7607d8e6a0e1958038cdd36f8b7
    private int vocab_size;


    private Map<String, List<String>> bow = new HashMap<>();

    public Index() {
        stopWords = readStopWords();
    }

    public Map<String, Map<String, List<Integer>>> getWordToDocument() {
        return wordToDocument;
    }

    private Map<String, Map<String, List<Integer>>> wordToDocument = new HashMap<>();

    public Tokenizer getTokeniser() {
        return tokeniser;
    }

    private Tokenizer tokeniser;
    private List<Document> documents;
    public double avgDocsLength;
    private String version;
    private String nlp_method;

    public int getVocab_size() {
        return vocab_size;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Index(Tokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }

    public double getAvgDocsLength() {
        return avgDocsLength;
    }

    public void indexAll() throws FileNotFoundException {
        //LOGGER.info("Start indexing...");

        for (String nlp_method : nlp_methods) {
            LOGGER.info(nlp_method);
            bow.put(nlp_method, bagOfWords(nlp_method));
            wordToDocument.put(nlp_method, generateWordToDocument(nlp_method, tokeniser));
        }
        //LOGGER.info("Finished indexing...");
    }


    public void getAvgLength(List<Document> docs) {
        double avg = 0;
        double average;
        for (Document doc : documents) {
            String[] tokens = doc.getContent().split(" ");
            avg += tokens.length;

        }
        average = avg / (double) docs.size();
        this.avgDocsLength = average;

    }

    public Map<String, List<Integer>> generateWordToDocument(String method, Tokenizer tokeniser) {
        Map<String, List<Integer>> wordToDocs = new HashMap<>();

        for (String word : getBowFor(method)) {
            int nbDocs = 0;
            for (Document document : documents) {
                List<String> tokens = tokeniser.tokenise(document.getContent(), method);
                if (tokens.contains(word)) {
                    if (wordToDocs.get(word) == null) {
                        wordToDocs.put(word, new ArrayList<Integer>());
                    }
                    wordToDocs.get(word).add(document.getId());
                } else wordToDocs.put(word, new ArrayList<Integer>());
            }

        }
        return wordToDocs;
    }

    public List<String> readStopWords() {
        List<String> stopWords = new ArrayList<>();
        
        BufferedReader br = null;
        InputStreamReader fr = null;
        
        try {
            fr = new InputStreamReader(this.getClass().getResourceAsStream("/stop_word_list.txt"));
            br = new BufferedReader(fr);

            String sCurrentLine;
            int i = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                stopWords.add(sCurrentLine.trim());
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return stopWords;
    }


    public List<String> bagOfWords(String nlp_method) throws FileNotFoundException {

        Set<String> bagOfWord = new HashSet<>();
        int num_tokens = 0;
        for (Document d : documents) {
            final List<String> tokens = tokeniser.tokenise(d.getContent(), nlp_method);

            for (String token : tokens) {
                if (stopWords.contains(token)) {
                    continue;
                } else {
                    bagOfWord.add(token);
                    num_tokens++;
                }
            }
        }
        this.vocab_size = num_tokens;
        return new ArrayList<>(bagOfWord);

    }

    public List<Document> nlpToDocs(List<Document> documents, String nlp_method) {
        List<Document> newDocs = new ArrayList<>();

        for (Document d : documents) {
            final List<String> tokens = tokeniser.tokenise(d.getContent(), nlp_method);
            Iterator<String> iter = tokens.iterator();
            StringBuilder builder = new StringBuilder(iter.next());
            while (iter.hasNext()) {
                builder.append(" ").append(iter.next());
            }
            d.setContent(builder.toString());
            newDocs.add(d);
        }
        return newDocs;
    }

    public Document nlpToDoc(Document doc, String nlp_method) {

        final List<String> tokens = tokeniser.tokenise(doc.getContent(), nlp_method);
        Iterator<String> iter = tokens.iterator();
        StringBuilder builder = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            builder.append(" ").append(iter.next());
        }
        doc.setContent(builder.toString());


        return doc;
    }


    public List<String> getBowFor(String method) {
        return bow.get(method);
    }
}