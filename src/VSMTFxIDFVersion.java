import java.io.IOException;
import java.util.*;


/**
 * Created by romdhane on 21/05/17.
 */
public class VSMTFxIDFVersion {
    private Map<String, Float> dotProduct = new HashMap<String, Float>();

    String query = "news about presidential campaign";
    String doc1 = "news about organic food campaign";
    String doc2 = "news of presidential campaign ";
    String doc3 = "news of presidential campaign presidential candidate";
    String[] docs = {doc1, doc2, doc3};


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
            for (String valueDoc : listOfWordsDoc) {

                if (bow.contains(valueDoc)) {
                    vector.add(1);
                } else {
                    vector.add(0);
                }
            }
        }
        return vector;
    }

    // computing the TF of a document
    public List<Float> getTF(String document) throws IOException {
        List<Float> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(document);
        List<String> listOfWords = bagOfWords();

        for (String value : listOfWords) {
            float tf = (float) Collections.frequency(listOfWordsDoc, value);
            listTF.add(tf);
        }
        return listTF;
    }


    // computing the IDF of a document
    public List<Float> getIDF() throws IOException {
        List<Float> listIDF = new ArrayList<>();

        List<String> listOfWords = bagOfWords();
        for (String word : listOfWords) {
            int nbdocs = 0;
            int i = 0;
            while (i < docs.length) {
                if (docs[i].toLowerCase().indexOf(word.toLowerCase()) != -1)
                    nbdocs++;
                i++;
            }

            listIDF.add(new Float(Math.log((docs.length + 1 / nbdocs))));
            //System.out.println(new Float(Math.log((docs.length + 2 / nbdocs))));
        }
//        listOfWords.removeAll(listOfWords);
        return listIDF;
    }


    // computing ranking scores between the query and each one of the documents
    public Map<String, Float> getRankingScores(String query, String[] docs) throws IOException {

        if ("VSM Binary".equals(version)) {
            for (String doc : docs) {
                List<Integer> vectdoc = indexVector(doc);
                System.out.println(doc + " \t" + Arrays.toString(vectdoc.toArray()));
                int sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i);
                    sum += value;
                }
                vectdoc.removeAll(vectdoc);
                dotProduct.put(doc + "\n", (float) sum);
            }
        } else if (version.equals("VSM with TF")) {
            List<Float> listvecTF = getTF(query);
            //Transforming to vector
            List<Float> vectTF = new Vector<Float>(listvecTF);

            for (String doc : docs) {

                List<Float> listdocTF = getTF(doc);
                List<Float> docTF = new Vector<Float>(listdocTF);
                float sum = 0;
                for (int i = 0; i < vectTF.size(); i++) {
                    Float value = vectTF.get(i) * docTF.get(i);
                    sum += value;
                }
                dotProduct.put(doc, sum);
            }
        } else if (version == "Improved VSM with IDF") {
            List<Float> listqueryTF = getTF(query);
            List<Float> queryTF = new Vector<Float>(listqueryTF);
            List<Float> listdocIDF = getIDF();
            List<Float> docIDF = new Vector<Float>(listdocIDF);

            for (String doc : docs) {
                List<Float> listdocTF = getTF(doc);
                List<Float> docTF = new Vector<Float>(listdocTF);
                float sum = 0;
                for (int i = 0; i < queryTF.size(); i++) {
                    Float value = queryTF.get(i) * docIDF.get(i) * docTF.get(i);
                    sum += value;
                }

                dotProduct.put(doc + "\n", sum);
            }
        } else if (version == "Solving problem Presidential vs About") {
            List<Integer> queryIndex = indexVector(query);
            List<Integer> vectqueryIndex = new Vector<Integer>(queryIndex);

            List<Float> listdocIDF = getIDF();
            List<Float> vectdocIDF = new Vector<Float>(listdocIDF);


            for (String doc : docs) {
                List<Integer> docIndex = indexVector(doc);
                List<Integer> vectdocIndex = new Vector<Integer>(docIndex);

                float sum = 0;
                for (int i = 0; i < vectqueryIndex.size(); i++) {
                    Float value = vectqueryIndex.get(i) * vectdocIDF.get(i) * vectdocIndex.get(i);
                    sum += value;
                }

                dotProduct.put(doc + "\n", sum);
            }
        } else if (version == "VSM with TFxIDF") {
            List<Float> listqueryTF = getTF(query);
            List<Float> vectqueryTF = new Vector<Float>(listqueryTF);

            List<Float> listIDF = getIDF();
            List<Float> vectIDF = new Vector<Float>(listIDF);

            for (String doc : docs) {
                List<Float> listdocTF = getTF(doc);
                List<Float> vectdocTF = new Vector<Float>(listdocTF);

                float sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Float value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                dotProduct.put(doc + "\n", sum);
            }
        }
        return dotProduct;
    }

}

