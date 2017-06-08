import java.io.IOException;
import java.util.*;


/**
 * Created by romdhane on 21/05/17.
 */
public class VSMTFxIDFVersion {
    private Map<String, Double> dotProduct = new HashMap<>();

    public String version;

    public VSMTFxIDFVersion() {
    }

    public VSMTFxIDFVersion(String version) {
        this.version = version;
    }

    // retrieving the tokens of a single document
    public List<String> bagOfWordsByDoc(String s) {
        List<String> bagOfWord = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s);
        while (st.hasMoreTokens()) {
            bagOfWord.add(st.nextToken());
        }
        return bagOfWord;
    }

    // retrieving the tokens of multiple documents
    public List<String> bagOfWords() {
        /*listTokens.removeAll(listTokens);
        for (int i = 0; i < s.length; i++) {
            StringTokenizer st = new StringTokenizer(s[i]);
            while (st.hasMoreTokens()) {
                listTokens.add(st.nextToken());
            }
        }*/

        List<String> listTokens = Arrays.asList("news", "about", "presidential", "campaign", "food", "organic", "candidate");
        return listTokens;
    }

    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Integer> indexVector(String document) {
        List<Integer> vector = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(document);
        List<String> bow = bagOfWords();

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
    public List<Double> getTF(String document) throws IOException {
        List<Double> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(document);
        List<String> listOfWords = bagOfWords();

        for (String value : listOfWords) {
            double tf = (double) Collections.frequency(listOfWordsDoc, value);
            listTF.add(tf);
        }
        return listTF;
    }


    // computing the IDF of a document
    public List<Double> getIDF(String[] docs) throws IOException {
        List<Double> listIDF = new ArrayList<>();

        List<String> bow = bagOfWords();
        for (String word : bow) {
            int nbdocs = 0;
            int i = 0;
            while (i < docs.length) {
                if (docs[i].toLowerCase().indexOf(word.toLowerCase()) != -1)
                    nbdocs++;
                i++;
            }

            listIDF.add(Math.log(((double) (docs.length + 1) / nbdocs)));
        }
        return listIDF;
    }


    // computing ranking scores between the query and each one of the documents
    public Map<String, Double> getRankingScores(String query, String[] docs) throws IOException {

        List<Integer> vectquery = indexVector(query);
        if ("Binary".equals(version)) {
            for (String doc : docs) {
                List<Integer> vectdoc = indexVector(doc);
                double sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i) * vectquery.get(i);
                    sum = sum + value;
                }

                dotProduct.put(doc + " -> ", sum);
            }
        } else if (version.equals("TF")) {
            List<Double> listvecTF = getTF(query);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (String doc : docs) {

                List<Double> listdocTF = getTF(doc);
                List<Double> docTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectTF.size(); i++) {
                    Double value = vectTF.get(i) * docTF.get(i);
                    sum += value;
                }
                dotProduct.put(doc, sum);
            }
        } else if (version == "TF/IDF") {
            List<Double> listqueryTF = getTF(query);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(docs);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (String doc : docs) {
                List<Double> listdocTF = getTF(doc);
                List<Double> vectdocTF = new Vector<>(listdocTF);

                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                dotProduct.put(doc + " -> ", sum);
            }
        }
        return dotProduct;
    }

}

