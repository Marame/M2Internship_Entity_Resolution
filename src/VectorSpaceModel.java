import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * Created by romdhane on 21/05/17.
 */
public class VectorSpaceModel {


    ArrayList<String> listTokens = new ArrayList<>();
    ArrayList<String> listTokensByDoc = new ArrayList<>();
    ArrayList<Float> listScores = new ArrayList<>();
    ArrayList<Float> listTF = new ArrayList<>();
    ArrayList<Float> listIDF = new ArrayList<>();
    ArrayList<Float> normalisedvectIndex = new ArrayList<>();
    HashMap<String, Float> dotProduct = new HashMap<String, Float>();
    ArrayList<Integer> indexVect = new ArrayList<>();
     String doc1 = "Test space model example";
     String doc2 = "Test model";
     String[] docs = {doc1, doc2};

  // retrieving the tokens of a single document
    public ArrayList<String> bagOfWordsByDoc(String s) {

        StringTokenizer st = new StringTokenizer(s);
        while (st.hasMoreTokens()) {
            listTokensByDoc.add(st.nextToken());
        }
        return listTokensByDoc;
    }
    // retrieving the tokens of multiple documents
    public ArrayList<String> bagOfWords(String[] s) {

        for (int i = 0; i < s.length; i++) {
            StringTokenizer st = new StringTokenizer(s[i]);
            while (st.hasMoreTokens()) {
                listTokens.add(st.nextToken());
            }
        }
        return listTokens;
    }
     // computing the TF of a document
    public ArrayList<Float> getTF(String document) throws IOException {
        ArrayList<String> listOfWordsDoc = bagOfWordsByDoc(document);

        for (String value : listOfWordsDoc) {
            float tf = (float) Collections.frequency(listOfWordsDoc, value)/listOfWordsDoc.size();
            listTF.add(tf);
        }
        return listTF;
    }
    // computing the IDF of a document
    public ArrayList<Float> getIDF(String document) throws IOException {

        ArrayList<String> listOfWordsDoc = bagOfWordsByDoc(document);
        int nbdocs = 0;
        int i = 1;
        for (String word : listOfWordsDoc) {
            while (i < docs.length) {
                if (docs[i].contains(word)) {
                    nbdocs++;
                    i++;
                }
            }
            listIDF.add(new Float(Math.log((docs.length + 1) / nbdocs)));
        }

        return listIDF;
    }

    // computing TFxIDF scores
    public ArrayList<Float> getTFxIDFScores(String document) throws IOException {
        ArrayList<Float> listTFs = getTF(document);
        ArrayList<Float> listIDFs = getIDF(document);

        for (int i = 0; i < listTFs.size(); i++) {
            Float p = listTFs.get(i) * listIDFs.get(i);
            listScores.add(p);
        }
        return listScores;
    }

    //computing the binary vector of a document, indicating ifeach term is present in the bag of words
    public ArrayList<Integer> indexVector(String document) {
        ArrayList<String> listOfWords = bagOfWords(docs);
        ArrayList<String> listOfWordsDoc = bagOfWordsByDoc(document);
        for (String val: listOfWordsDoc) {
            System.out.println(val);
        }

        for (String value : listOfWordsDoc) {

            if (listOfWords.contains(value.toString())) {
                indexVect.add(1);
            } else {
                indexVect.add(0);
            }
        }
        return indexVect;
    }

    // computing the normalised vector, which is the dot product between TFxIDF vector and the index vector
    public ArrayList<Float> normalisedVector(String d) throws IOException {

        ArrayList<Integer> vectIndex = indexVector(d);
        ArrayList<Float> scores = getTFxIDFScores(d);

        for (int i = 0; i < vectIndex.size(); i++) {
            float value = vectIndex.get(i)* scores.get(i);
            normalisedvectIndex.add((float) value);

        }

        return normalisedvectIndex;
    }
    // computing ranking scores between the query and each one of the documents
    public HashMap<String, Float> getRankingScores(String query, String[] docs) throws IOException {

        ArrayList<Float> vectquery = normalisedVector(query);

        for (String doc : docs) {
           ArrayList<Float> vectdoc = normalisedVector(doc);

            float sum = 0;
            for (int i = 0; i < vectquery.size(); i++) {
                float value = ((float) vectquery.get(i)) * ((float) vectdoc.get(i));
                sum += value;
            }

            dotProduct.put(doc + "\n", sum);
        }
        return dotProduct;
    }


}