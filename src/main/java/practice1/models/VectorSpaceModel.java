package practice1.models;

import org.tartarus.snowball.ext.PorterStemmer;
import practice1.Index;
import practice1.entities.Concurrency;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.Lemmatizer;

import java.io.IOException;
import java.util.*;

import static practice1.Index.LEMMATIZING_NLP_METHOD;
import static practice1.Index.NO_NLP_METHOD;
import static practice1.Index.STEMMING_NLP_METHOD;

/**
 * Created by romdhane on 21/05/17.
 */
public class VectorSpaceModel {
    public final static String VSM_BINARY = "Binary";
    public final static String VSM_TF = "TF";
    public final static String VSM_TFIDF = "TF/IDF";
    public final static String VSM_BM25 = "BM25";

    // versions of VSM
    public static List<String> vsm_versions = Arrays.asList(VSM_BINARY, VSM_TF, VSM_TFIDF, VSM_BM25);
    private Index index;

    private String version;
    private String nlp_method;
    private List<Document> dotProduct = new ArrayList<>();

    public Lemmatizer lemm;

    public void setLemm(Lemmatizer lemm) {
        this.lemm = lemm;
    }

    public VectorSpaceModel(String version, String nlp_method, Lemmatizer lemm, Index index) throws IOException {
        this.version = version;
        this.nlp_method = nlp_method;
        this.lemm = lemm;
        this.index = index;
    }

    public VectorSpaceModel() {
    }

    // retrieving the tokens of a single document
    public List<String> tokenizeDoc(Document s) {
        List<String> bagOfWord = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getContent());

        while (st.hasMoreTokens()) {
            if (nlp_method.equals(STEMMING_NLP_METHOD)) {
                PorterStemmer stem = new PorterStemmer();
                stem.setCurrent(st.nextToken());
                stem.stem();
                String result = stem.getCurrent();
                bagOfWord.add(result);
            } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {
                for (String st_lem : lemm.lemmatize(st.nextToken())) {
                    bagOfWord.add(st_lem);
                }
            } else {
                bagOfWord.add(st.nextToken());
            }
        }
        return bagOfWord;
    }


    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Integer> indexDocument(Document d) throws IOException {
        List<Integer> vector = new ArrayList<>();
        List<String> documentTokens = tokenizeDoc(d);
        List<String> bow = index.getBowFor(nlp_method);

        for (String value : bow) {
            if (documentTokens.contains(value)) {
                vector.add(1);
            } else {
                vector.add(0);
            }
        }

        //The document vector must have the same lenght as the bag of word
        assert vector.size() == documentTokens.size();

        return vector;
    }

    // computing the TF of a document
    public List<Double> getTF(Document d, List<String> bow, boolean normalised) throws IOException {
        List<Double> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = tokenizeDoc(d);

        for (String value : bow) {

            double freq = (double) Collections.frequency(listOfWordsDoc, value);
            if (normalised == false) {
                listTF.add(freq);
            } else {
                double k = 0.4;
                double tf = (double) ((k + 1) * freq) / (k + freq);
                listTF.add(tf);
            }
        }

        return listTF;
    }

    public Double idf(String token, List<Document> docs) {

        int nbdocs = 0;
        int i = 0;
        Double idf;
        List<String> words = new ArrayList<>();

        while (i < docs.size()) {

            StringTokenizer st = new StringTokenizer(docs.get(i).getContent().toLowerCase());
            while (st.hasMoreTokens()) {

                words.add(st.nextToken());
            }
            if (words.contains(token)) {
                nbdocs++;
                i++;
            }
        }
        if (nbdocs != 0)
            idf = Math.log(((docs.size() + 1) / (double) (nbdocs)));
        else
            idf = Math.log(((docs).size() + 1) / (double) (nbdocs + 1));

        return idf;
    }



    public List<Double> getIDF() throws IOException {
        List<Double> listIDF = new ArrayList<>();

        for (String word : bow) {
            listIDF.add(idf(word, documents));
        }
        return listIDF;
    }

    // computing ranking scores between the query and each one of the documents
    public List<Document> getRankingScoresVSM(EvaluationEntity e) throws IOException {

        List<Integer> queryVector = indexDocument(e.getQuery());

        final List<Document> documents = index.getDocuments();
        if (VSM_BINARY.equals(version)) {

            for (Document doc : documents) {
                Document resultDoc = new Document();
                resultDoc.setName(doc.getContent());

                List<Integer> vectdoc = indexDocument(doc);

                double sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i) * queryVector.get(i);
                    sum = sum + value;
                }

                resultDoc.setScore(sum);

                dotProduct.add(resultDoc);
            }
        } else if (version.equals(VSM_TF)) {

            List<Double> listvecTF = getTF(documents, e.getQuery(), false);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(documents, doc, false);
                List<Double> docTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectTF.size(); i++) {
                    Double value = vectTF.get(i) * docTF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals(VSM_TFIDF)) {
            Concurrency con = new Concurrency();
            con.setE(e);
            con.setNormalised(false);
            System.out.println("TF of query");
            List<Double> listqueryTF = getTF(documents, e.getQuery(), false);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);
            System.out.println("TF of query done");

            System.out.println("IDF");
            List<Double> listIDF = getIDF();
            List<Double> vectIDF = new Vector<>(listIDF);

            System.out.println("IDF done");

            System.out.println("dot product");
            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(documents, doc, false);
                List<Double> vectdocTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
                System.out.println("dot product done");
            }
        } else if (version.equals(VSM_BM25)) {

            Concurrency con = new Concurrency();
            con.setE(e);
            con.setNormalised(true);

            List<Double> listqueryTF = getTF(documents, e.getQuery(), true);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF();
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(documents, doc, true);
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

