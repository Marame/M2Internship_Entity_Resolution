package Models;

import Entities.Document;
import Entities.EvaluationEntity;
import Utilities.Lemmatizer;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.util.*;

/**
 * Created by romdhane on 21/05/17.
 */
public class VSMTFxIDFVersion {
    private String version;
    private String nlp_method;
    private List<Document> dotProduct = new ArrayList<>();

    public VSMTFxIDFVersion() {
    }


    public VSMTFxIDFVersion(String version, String nlp_method) {
        this.version = version;
        this.nlp_method = nlp_method;
    }

    // retrieving the tokens of a single document
    public List<String> bagOfWordsByDoc(Document s) {
        List<String> bagOfWord = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getName());

        while (st.hasMoreTokens()) {
            if(nlp_method.equals("stemming")){
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(st.nextToken());
            stem.stem();
            String result = stem.getCurrent();
            bagOfWord.add(result);}
            else if(nlp_method.equals("lemmatizing")){
                Lemmatizer lem = new Lemmatizer();
                for (String st_lem: lem.lemmatize(st.nextToken())) {
                    bagOfWord.add(st_lem);
                }

            }
            else {
                bagOfWord.add(st.nextToken());
            }
        }
        return bagOfWord;
    }

    // retrieving the tokens of multiple documents
    public List<String> bagOfWords(EvaluationEntity e) throws IOException{
        List<String> bagOfWord = new ArrayList<>();
        String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all",
                "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an",
                "and", "another", "any", "anybody", "anyhow", "anyone", "anything","anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because",
                "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both",
                "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes","certain", "certainly", "changes", "clearly", "co", "com", "come","comes", "concerning", "consequently", "consider", "considering", "contain",
                "containing",    "contains","corresponding","could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere",
                "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed",
                "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
          List<String> liststopwords = new ArrayList<>(Arrays.asList(stopwords));
        for(Document d: e.getDocuments()){
            StringTokenizer st = new StringTokenizer(d.getName());
            while (st.hasMoreTokens()) {
                if (nlp_method.equals("stemming")) {
                    PorterStemmer stem = new PorterStemmer();
                    String s = st.nextToken();
                    if (!liststopwords.contains(s)) {
                        stem.setCurrent(s);
                        stem.stem();
                        String result = stem.getCurrent();
                        bagOfWord.add(result);
                    }
                }
                else if(nlp_method.equals("lemmatizing")){
                    Lemmatizer lem = new Lemmatizer();
                    for (String st_lem: lem.lemmatize(st.nextToken())) {
                        bagOfWord.add(st_lem);
                    }
                }
                else {
                bagOfWord.add(st.nextToken());}
            }
                }

        return bagOfWord;

    }

    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Integer> indexVector(EvaluationEntity e, Document d)throws IOException {
        List<Integer> vector = new ArrayList<>();
        List<String> listOfWordsDoc = bagOfWordsByDoc(d);
        List<String> bow = bagOfWords(e);

        for (String value : bow) {
            if (listOfWordsDoc.contains(value)) {
                vector.add(1);
            } else {
                vector.add(0);
            }
        }
        return vector;
    }

    // computing the TF of a document
    public List<Double> getTF(EvaluationEntity e, Document d, boolean normalised) throws IOException {
        List<Double> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(d);
        List<String> listOfWords = bagOfWords(e);
        if(nlp_method.equals("stemming")) {
        for (String value : listOfWords) {
            PorterStemmer stem = new PorterStemmer();
                stem.setCurrent(value);
                stem.stem();
                String resword = stem.getCurrent();
                double freq = (double) Collections.frequency(listOfWordsDoc,resword );
                if (normalised == false) {
                    listTF.add(freq);
                } else {
                    double k = 0.4;
                    double tf = (double) ((k + 1) * freq) / (k + freq);
                    listTF.add(tf);
                }
            }
        }
        else if(nlp_method.equals("lemmatizing")) {
            for (String value : listOfWords) {
                Lemmatizer lem = new Lemmatizer();
                String resword = lem.lemmatize(value).get(0);
                double freq = (double) Collections.frequency(listOfWordsDoc,resword );
                if (normalised == false) {
                    listTF.add(freq);
                } else {
                    double k = 0.4;
                    double tf = (double) ((k + 1) * freq) / (k + freq);
                    listTF.add(tf);
                }
            }
        }
        else {
            for (String value : listOfWords) {
                double freq = (double) Collections.frequency(listOfWordsDoc,value);
                if (normalised == false) {
                    listTF.add(freq);
                } else {
                    double k = 0.4;
                    double tf = (double) ((k + 1) * freq) / (k + freq);
                    listTF.add(tf);
                }
            }

        }

        return listTF;
    }


    // computing the IDF of a document
    public List<Double> getIDF(EvaluationEntity e) throws IOException {
        List<Double> listIDF = new ArrayList<>();

        List<String> bow = bagOfWords(e);
        for (String word : bow) {
            int nbdocs = 0;
            int i = 0;
            List<String> words = new ArrayList<>();
            while (i < e.getDocuments().size()) {
                if (nlp_method.equals("stemming")) {
                    StringTokenizer st = new StringTokenizer(e.getDocuments().get(i).getName().toLowerCase());
                    while (st.hasMoreTokens()) {
                        PorterStemmer stem = new PorterStemmer();
                        stem.setCurrent(st.nextToken());
                        stem.stem();
                        String result = stem.getCurrent();
                        words.add(result);
                    }
                    if (words.contains(word))
                        nbdocs++;
                    i++;
                } else if (nlp_method.equals("lemmatizing")) {
                    Lemmatizer lem = new Lemmatizer();
                    List<String> listlemmas = lem.lemmatize(e.getDocuments().get(i).getName().toLowerCase());
                    if (listlemmas.contains(word.toLowerCase()))
                        nbdocs++;
                    i++;
                }
                else {
                    if (e.getDocuments().get(i).getName().toLowerCase().indexOf(word)!=-1)
                        nbdocs++;
                    i++;
                }
                if (nbdocs != 0)
                    listIDF.add(Math.log(((e.getDocuments().size() + 1) / (double) (nbdocs))));
                else
                    listIDF.add(Math.log(((e.getDocuments().size() + 1) / (double) (nbdocs + 1))));

            }
        }
        return listIDF;
    }


    // computing ranking scores between the query and each one of the documents
    public List<Document> getRankingScoresVSM(EvaluationEntity e) throws IOException {

        List<Integer> vectquery = indexVector(e, e.getQuery());

        if ("Binary".equals(version)) {

            for (Document doc : e.getDocuments()) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());

                List<Integer> vectdoc = indexVector(e, doc);
                //System.out.println(vectdoc);
                double sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i) * vectquery.get(i);
                    sum = sum + value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals("TF")) {

            List<Double> listvecTF = getTF(e, e.getQuery(), false);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (Document doc : e.getDocuments()) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(e, doc, false);
                List<Double> docTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectTF.size(); i++) {
                    Double value = vectTF.get(i) * docTF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals("TF/IDF")) {

            List<Double> listqueryTF = getTF(e, e.getQuery(), false);

            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(e);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : e.getDocuments()) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(e, doc, false);
                List<Double> vectdocTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals("BM25")) {

            List<Double> listqueryTF = getTF(e, e.getQuery(), true);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(e);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : e.getDocuments()) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(e, doc, true);
                List<Double> vectdocTF = new Vector<>(listdocTF);

                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        }
        return dotProduct;
    }

}

