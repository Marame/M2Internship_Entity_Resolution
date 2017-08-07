package practice1.models;

import practice1.Index;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.IOException;
import java.util.*;

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

    public VectorSpaceModel(String version, String nlp_method, Index index) throws IOException {
        this.version = version;
        this.nlp_method = nlp_method;
        this.index = index;
    }

    public VectorSpaceModel() {
    }


    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Integer> indexDocument(Document d) throws IOException {
        List<Integer> vector = new ArrayList<>();
        List<String> documentTokens = index.getTokeniser().tokenise(d.getContent(), nlp_method);
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

        List<String> documentTokens = index.getTokeniser().tokenise(d.getContent(), nlp_method);

        for (String value : bow) {
            double freq = (double) Collections.frequency(documentTokens, value);
            if (normalised == false) {
                listTF.add(freq);
            } else {
                double k = 0.9;
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
        int nbDoc = index.getDocuments().size();
        int totalNbDocWordPresent = 0;
        for (String word : index.getBowFor(nlp_method)) {
            int nbDocWordPresent = index.getWordToDocument().get(nlp_method).get(word).size();

            double idf = 0.0f;
            if (nbDoc != 0) {
                idf = Math.log(((nbDoc + 1) / (double) (totalNbDocWordPresent)));
            } else {
                idf = Math.log((nbDoc + 1) / (double) (totalNbDocWordPresent + 1));
            }

            listIDF.add(idf);
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
                resultDoc.setId(doc.getId());
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

            List<Double> listvecTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
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
            List<Double> listqueryTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF();
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
                List<Double> vectdocTF = new Vector<>(listdocTF);
                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals(VSM_BM25)) {

            List<Double> listqueryTF = getTF(e.getQuery(), index.getBowFor(nlp_method), true);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF();
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setName(doc.getContent());
                List<Double> listdocTF = getTF(e.getQuery(), index.getBowFor(nlp_method), true);
                List<Double> vectdocTF = new Vector<>(listdocTF);

                double sum = 0;
                for (int i = 0; i < vectqueryTF.size(); i++) {
                    Double value = vectqueryTF.get(i) * vectdocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);
                dotProduct.add(resultdoc);
            }

        } else {
            System.out.println("Something wrong dude!");
        }
        return dotProduct;
    }

}