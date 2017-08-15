package practice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice1.entities.Document;

import java.util.*;

/**
 * Created by romdhane on 03/08/17.
 */
public class Index {

    private static Logger LOGGER = LoggerFactory.getLogger(Index.class);

    public final static String NO_NLP_METHOD = "nothing";
    public final static String STEMMING_NLP_METHOD = "stemming";
    public final static String LEMMATIZING_NLP_METHOD = "lemmatizing";

    //Versions of smoothing in Language Model
    public static List<String> nlp_methods = Arrays.asList(NO_NLP_METHOD,STEMMING_NLP_METHOD, LEMMATIZING_NLP_METHOD);
    private Index index;


    public static String[] STOP_WORDS = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all",
            "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an",
            "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because",
            "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both",
            "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain",
            "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere",
            "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed",
            "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};

    private Map<String, List<String>> bow = new HashMap<>();

    public Index() {
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
    private String version;
    private String nlp_method;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Index(Tokenizer tokeniser) {
        this.tokeniser = tokeniser;
    }




    public void indexAll() {
        //LOGGER.info("Start indexing...");

        for (String nlp_method : nlp_methods) {
            LOGGER.info(nlp_method);
            bow.put(nlp_method, bagOfWords(nlp_method));
            wordToDocument.put(nlp_method, generateWordToDocument(nlp_method, tokeniser));
        }

        //LOGGER.info("Finished indexing...");
    }

    public int getDocsPresent(String token, List<Document> documents) {
        int nbdocs = 0;
        int i = 0;
        while (i < documents.size()) {

            String[] tokens = documents.get(i).getContent().split(" ");
            if (Arrays.asList(tokens).contains(token)) {
                nbdocs++;
                i++;
            }
        }
        return nbdocs;
    }


    public Map<String, List<Integer>> generateWordToDocument(String method, Tokenizer tokeniser) {
        Map<String, List<Integer>> wordToDocs = new HashMap<>();

        for (String word : getBowFor(method)) {
            int nbDocs = 0;
            for (Document document : documents) {
                List<String> tokens = tokeniser.tokenise(document.getContent(), method);
                if (tokens.contains(word)) {
                    if (wordToDocs.get(word) == null) {
                        nbDocs ++;
                        wordToDocs.put(word, new ArrayList<Integer>());
                    }

                    wordToDocs.get(word).add(document.getId());
                }
            }
        }
        return wordToDocs;
    }


    public List<String> bagOfWords(String nlp_method) {

        List<String> bagOfWord = new ArrayList<>();
        List<String> stopWords = new ArrayList<>(Arrays.asList(STOP_WORDS));

        for (Document d : documents) {
            final List<String> tokens = tokeniser.tokenise(d.getContent(), nlp_method);

            for (String token : tokens) {
                if (stopWords.contains(token)) {
                    continue;
                } else {
                    bagOfWord.add(token);
                }
            }
        }

        return bagOfWord;

    }

    public List<Document> nlpToDocs(List<Document> documents, String nlp_method){
        List<Document> newDocs = new ArrayList<>();

        for(Document d: documents){
            final List<String> tokens= tokeniser.tokenise(d.getContent(), nlp_method);
            Iterator<String> iter = tokens.iterator();
            StringBuilder builder = new StringBuilder(iter.next());
            while( iter.hasNext() )
            {
                builder.append(" ").append(iter.next());
            }
            d.setContent(builder.toString());
            newDocs.add(d);
        }
        return  newDocs;
    }

    public Document nlpToDoc(Document doc, String nlp_method){

        final List<String> tokens= tokeniser.tokenise(doc.getContent(), nlp_method);
        Iterator<String> iter = tokens.iterator();
        StringBuilder builder = new StringBuilder(iter.next());
        while( iter.hasNext() )
        {
            builder.append(" ").append(iter.next());
        }
        doc.setContent(builder.toString());


        return  doc;
    }


    public List<String> getBowFor(String method) {
        return bow.get(method);
    }
}