package practice1.models;

import practice1.Index;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.utilities.StringUtilities;

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
    //public static List<String> vsm_versions = Arrays.asList(VSM_BINARY, VSM_TF, VSM_TFIDF, VSM_BM25);
    protected Index index;
    protected String version;
    protected static String nlp_method;


    public VectorSpaceModel(String version, String nlp_method, Index index) throws IOException {
        this.version = version;
        this.nlp_method = nlp_method;
        this.index = index;
    }

    public VectorSpaceModel() {
    }


    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Double> indexDocument(Document d) throws IOException {
        List<Double> vector = new ArrayList<>();
        List<String> documentTokens = index.getTokeniser().tokenise(d.getContent(), nlp_method);
        List<String> bow = index.getBowFor(nlp_method);

        for (String value : bow) {
            if (documentTokens.contains(value)) {
                vector.add(1.0);
            } else {
                vector.add(0.0);
            }
        }

        //The document vector must have the same length as the bag of word
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
                double k = 100;
                double b = 0.8;
                double avg = index.getAvgDocsLength();
                double tf = ((k + 1) * freq) / ((k * (1 - b + b * avg) + freq));
                listTF.add(tf);
            }
        }

        return listTF;
    }

    public List<Double> getIDF() throws IOException {

        List<Double> listIDF = new ArrayList<>();
        int nbDoc = index.getDocuments().size();
        int totalNbDocWordPresent = 0;

        for (String word : index.getBowFor(nlp_method)) {

            totalNbDocWordPresent = index.getWordToDocument().get(nlp_method).get(word).size();

            double idf = 0.0d;
            if (totalNbDocWordPresent != 0) {
                idf = Math.log((nbDoc + 1) / (double) (totalNbDocWordPresent));
            } else {
                idf = Math.log((nbDoc + 1) / (double) (totalNbDocWordPresent + 1));
            }

            listIDF.add(idf);
        }


        return listIDF;
    }

    public List<Double> dotProduct(List<Double> doc1, List<Double> doc2){
        List<Double> vect1 = new Vector<>(doc1);
        List<Double> vect2 = new Vector<>(doc2);
           List<Double> result = new ArrayList<>();

        for (int k = 0; k < doc1.size(); ++k){
            Double value =0.0d;
            value= vect1.get(k) * vect2.get(k) ;
        result.add(value);}
        return result;

    }
    //construct the term document matrix
    public double[][] termDocMatrix(String version) throws IOException {

        List<String> BOW = index.getBowFor(nlp_method);
        String[] TERMS = BOW.toArray(new String[index.getVocab_size()]);

        List<Document> DOCS = index.getDocuments();
        double[][] TERM_DOC_MATRIX
                = new double[BOW.size()][DOCS.size()];
        //documents
        for (int column = 0; column < DOCS.size(); column++) {
            List<Double> indexDoc = new ArrayList<>();
            switch (version) {
                case VSM_BINARY:
                    indexDoc = indexDocument(DOCS.get(column));
                    break;
                case VSM_TF:
                    indexDoc = getTF(DOCS.get(column), BOW, false);
                    break;
                case VSM_TFIDF:
                    indexDoc = dotProduct(getTF(DOCS.get(column), BOW, false), getIDF());
                    break;
                case VSM_BM25:
                    indexDoc = dotProduct(getTF(DOCS.get(column), BOW, true), getIDF());;
                    break;
            }

            for (int row = 0; row < BOW.size(); row++) {
                TERM_DOC_MATRIX[row][column] = indexDoc.get(row);

            }
        }
        System.out.println("term doc done");
        return TERM_DOC_MATRIX;
    }

    // computing ranking scores between the query and each one of the documents
    public List<Document> getRankingScoresVSM(EvaluationEntity e) throws IOException {
        StringUtilities su = new StringUtilities();
        List<Document> dotProduct = new ArrayList<>();

        final List<Document> documents = index.getDocuments();
        if (VSM_BINARY.equals(version)) {
            List<Double> listQuery =  indexDocument(e.getQuery());
            List<Double> vectQuery = new Vector<>(listQuery);

            for (Document doc : documents) {
                Document resultDoc = new Document();
                resultDoc.setId(doc.getId());
                resultDoc.setContent(doc.getContent());

//                if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                    doc.setContent(su.getAcronym(e.getQuery().getContent()));
//                }

                List<Double> listDoc =  indexDocument(doc);
                List<Double> vectDoc = new Vector<>(listDoc);

                double sum = 0;
                for (int i = 0; i < vectQuery.size(); i++) {
                    double value = vectDoc.get(i) * vectQuery.get(i);
                    sum = sum + value;
                }

                resultDoc.setScore(sum);
                dotProduct.add(resultDoc);
            }
        } else if (version.equals(VSM_TF)) {
            List<Double> listQueryTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
            List<Double> vectQueryTF = new Vector<>(listQueryTF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setContent(doc.getContent());
//                if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                    doc.setContent(su.getAcronym(e.getQuery().getContent())); }
                List<Double> listDocTF =  getTF(doc, index.getBowFor(nlp_method), false);
                List<Double> vectDocTF = new Vector<>(listDocTF);


                double sum = 0;
                for (int i = 0; i < vectQueryTF.size(); i++) {
                    Double value = vectQueryTF.get(i) * vectDocTF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);
                dotProduct.add(resultdoc);
            }
        } else if (version.equals(VSM_TFIDF)) {
            List<Double> listQueryTF = getTF(e.getQuery(), index.getBowFor(nlp_method), false);
            List<Double> vectQueryTF = new Vector<>(listQueryTF);

            List<Double> listIDF = getIDF();
            List<Double> vectIDF = new Vector<>(listIDF);


            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setContent(doc.getContent());

//                if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                    doc.setContent(su.getAcronym(e.getQuery().getContent()));}
                List<Double> listDocTF =  getTF(doc, index.getBowFor(nlp_method), false);
                List<Double> vectDocTF = new Vector<>(listDocTF);

                double sum = 0;
                for (int i = 0; i < vectQueryTF.size(); i++) {
                    Double value = vectQueryTF.get(i) * vectDocTF.get(i) * vectIDF.get(i);
                    sum += value;
                }

                resultdoc.setScore(sum);
                dotProduct.add(resultdoc);
            }
        } else if (version.equals(VSM_BM25)) {

            final Document query = e.getQuery();
            List<Double> listQueryTF = getTF(query, index.getBowFor(nlp_method), false);
            List<Double> vectQueryTF = new Vector<>(listQueryTF);
            List<Double> listIDF = getIDF();

            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setContent(doc.getContent());

//                if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                    Document newdoc = new Document();
//                    newdoc.setId(doc.getId());
//                    newdoc.setContent(su.getAcronym(doc.getContent()));
//                    List<Double> listDocTF = getTF(newdoc, index.getBowFor(nlp_method), true);
//                    List<Double> vectDocTF = new Vector<>(listDocTF);
//
//                    double sum = 0;
//                    for (int i = 0; i < vectQueryTF.size(); i++) {
//                        Double value = vectQueryTF.get(i) * vectDocTF.get(i) * vectIDF.get(i);
//                        sum += value;
//                    }
//                    resultdoc.setScore(sum);
//                    dotProduct.add(resultdoc);
//
//                } else {
                    List<Double> listDocTF = getTF(doc, index.getBowFor(nlp_method), true);
                    List<Double> vectDocTF = new Vector<>(listDocTF);


                    double sum = 0;
                    for (int i = 0; i < vectQueryTF.size(); i++) {
                        Double value = vectQueryTF.get(i) * vectDocTF.get(i) * vectIDF.get(i);
                        sum += value;
                    }

                    resultdoc.setScore(sum);
                    dotProduct.add(resultdoc);
//                }
            }
        }else {
            System.out.println("Something wrong dude!");
        }

        return dotProduct;
    }
}


