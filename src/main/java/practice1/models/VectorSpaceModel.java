package practice1.models;

import org.tartarus.snowball.ext.PorterStemmer;
import practice1.Main;
import practice1.entities.Concurrency;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.Lemmatizer;

import java.io.IOException;
import java.util.*;

import static practice1.Main.*;

/**
 * Created by romdhane on 21/05/17.
 */
public class VectorSpaceModel {
    private String version;
    private String nlp_method;
    private List<Document> dotProduct = new ArrayList<>();
    private  List<Document> documents;
    private List<String> bow;
    public Lemmatizer lemm ;

    public List<String> getBow() {
        return bow;
    }

    public void setBow(List<String> bow) {
        this.bow = bow;
    }

    public void setLemm(Lemmatizer lemm) {
        this.lemm = lemm;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public VectorSpaceModel(String version, String nlp_method, Lemmatizer lemm, List<String> bow, List<Document> documents) throws IOException {
        this.version = version;
        this.nlp_method = nlp_method;
        this.lemm = lemm;
        this.bow = bow;
        this.documents= documents;
    }

    public VectorSpaceModel() {
    }


    // retrieving the tokens of a single document
    public List<String> bagOfWordsByDoc(Document s) {
        List<String> bagOfWord = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(s.getName());

        while (st.hasMoreTokens()) {
            if(nlp_method.equals(Main.STEMMING_NLP_METHOD)){
            PorterStemmer stem = new PorterStemmer();
            stem.setCurrent(st.nextToken());
            stem.stem();
            String result = stem.getCurrent();
            bagOfWord.add(result);}
            else if(nlp_method.equals(Main.LEMMATIZING_NLP_METHOD)){
                for (String st_lem: lemm.lemmatize(st.nextToken())) {
                    bagOfWord.add(st_lem);
                }
            }
            else {
                bagOfWord.add(st.nextToken());
            }
        }
        return bagOfWord;
    }



    //computing the binary vector of a document, indicating if each term is present in the bag of words
    public List<Integer> indexVector(Document d)throws IOException {
        List<Integer> vector = new ArrayList<>();
        List<String> listOfWordsDoc = bagOfWordsByDoc(d);

        for (String value :bow) {
            if (listOfWordsDoc.contains(value)) {
                vector.add(1);
            } else {
                vector.add(0);
            }
        }
        return vector;
    }

    // computing the TF of a document
    public List<Double> getTF(List<Document> docs, Document d, boolean normalised) throws IOException {
        List<Double> listTF = new ArrayList<>();

        List<String> listOfWordsDoc = bagOfWordsByDoc(d);

        if(nlp_method.equals(STEMMING_NLP_METHOD)) {
        for (String value : bow) {
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
        else if(nlp_method.equals(LEMMATIZING_NLP_METHOD)) {
            for (String value : bow) {
                String resword = lemm.lemmatize(value).get(0);
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
            for (String value : bow) {
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
    public List<Double> getIDF(List<Document> docs) throws IOException {
        List<Double> listIDF = new ArrayList<>();

        for (String word : bow) {
            int nbdocs = 0;
            int i = 0;
            List<String> words = new ArrayList<>();
            while (i < docs.size()) {
                if (nlp_method.equals(STEMMING_NLP_METHOD)) {
                    StringTokenizer st = new StringTokenizer(docs.get(i).getName().toLowerCase());
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
                } else if (nlp_method.equals(LEMMATIZING_NLP_METHOD)) {

                    List<String> listlemmas = lemm.lemmatize(docs.get(i).getName().toLowerCase());
                    if (listlemmas.contains(word.toLowerCase()))
                        nbdocs++;
                    i++;
                }
                else {
                    if (docs.get(i).getName().toLowerCase().indexOf(word)!=-1)
                        nbdocs++;
                    i++;
                }
                if (nbdocs != 0)
                    listIDF.add(Math.log(((docs.size() + 1) / (double) (nbdocs))));
                else
                    listIDF.add(Math.log(((docs).size() + 1) / (double) (nbdocs + 1)));

            }
        }
        return listIDF;
    }


    // computing ranking scores between the query and each one of the documents
    public List<Document> getRankingScoresVSM(EvaluationEntity e) throws IOException {

        List<Integer> vectquery = indexVector(e.getQuery());

        if (VSM_BINARY.equals(version)) {

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());

                List<Integer> vectdoc = indexVector(doc);

                double sum = 0;

                for (int i = 0; i < vectdoc.size(); i++) {
                    int value = vectdoc.get(i) * vectquery.get(i);
                    sum = sum + value;
                }

                resultdoc.setScore(sum);

                dotProduct.add(resultdoc);
            }
        } else if (version.equals(VSM_TF)) {

            List<Double> listvecTF = getTF(documents, e.getQuery(), false);
            //Transforming to vector
            List<Double> vectTF = new Vector<>(listvecTF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
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

            List<Double> listqueryTF = getTF(documents, e.getQuery(), false);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(documents);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : documents) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
                List<Double> listdocTF = getTF(documents, doc, false);
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

            Concurrency con = new Concurrency();
            con.setE(e);
            con.setNormalised(true);

            List<Double> listqueryTF = getTF(documents, e.getQuery(), true);
            List<Double> vectqueryTF = new Vector<>(listqueryTF);

            List<Double> listIDF = getIDF(documents);
            List<Double> vectIDF = new Vector<>(listIDF);

            for (Document doc : e.getDocuments()) {
                Document resultdoc = new Document();
                resultdoc.setName(doc.getName());
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

