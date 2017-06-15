import java.io.IOException;
import java.util.*;


/**
 * Created by romdhane on 21/05/17.
 */
public class VSMTFxIDFVersion {
    private List<Document> dotProduct = new ArrayList<>();

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
    public List<Double> getTF(String document, boolean normalised) throws IOException {
        List<Double> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(document);
        List<String> listOfWords = bagOfWords();

        for (String value : listOfWords) {
            double freq = (double) Collections.frequency(listOfWordsDoc, value);
              if(normalised == false){
            listTF.add(freq); }
              else {
                double k =0.4;
                double tf = (double)((k+1)*freq)/ (k +freq);
            listTF.add(tf);}
        }
        return listTF;
    }


    // computing the IDF of a document
    public List<Double> getIDF(Document[] docs) throws IOException {
        List<Double> listIDF = new ArrayList<>();

        List<String> bow = bagOfWords();
        for (String word : bow) {
            int nbdocs = 0;
            int i = 0;
            while (i < docs.length) {
                if (docs[i].getName().toLowerCase().indexOf(word.toLowerCase()) != -1)
                    nbdocs++;
                i++;
            }

            listIDF.add(Math.log(((double) (docs.length + 1) / nbdocs)));
        }
        return listIDF;
    }


    // computing ranking scores between the query and each one of the documents
    public List<Document> getRankingScores(Document query, Document[] docs) throws IOException {

        //System.out.println(resultdoc.getName());

        List<Integer> vectquery = indexVector(query.getName());

        if ("Binary".equals(version)) {

            for (Document doc : docs) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());


                List<Integer> vectdoc = indexVector(doc.getName());
                double sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i) * vectquery.get(i);
                    sum = sum + value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals("TF")) {

            List<Double> listvecTF = getTF(query.getName(), false);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (Document doc : docs) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(doc.getName(), false);
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

            List<Double> listqueryTF = getTF(query.getName(), false);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(docs);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : docs) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(doc.getName(), false);
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
        else if (version.equals("BM25")) {

            List<Double> listqueryTF = getTF(query.getName(), true);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(docs);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : docs) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(doc.getName(), true);
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

