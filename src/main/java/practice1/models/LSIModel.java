package practice1.models;

import com.aliasi.matrix.SvdMatrix;
import practice1.Index;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.utilities.StringUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LSIModel extends VectorSpaceModel {
    static final int NUM_FACTORS = 17;
    static final List<Object> parametersSVD = new ArrayList<>();


    public LSIModel(String version, String nlp_method, Index index) {
        this.version = version;
        this.index = index;
        this.nlp_method = nlp_method;

    }

    public LSIModel() {
    }

    public double[][] transpose(double[][] array) {
        if (array == null || array.length == 0)//empty or unset array, nothing do to here
            return array;

        int width = array.length;
        int height = array[0].length;

        double[][] array_new = new double[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                array_new[y][x] = array[x][y];
            }
        }
        return array_new;
    }

    //construct the query matrix
    public double[] query_doc_Matrix(Document query, String vsm_version) throws IOException {
        List<String> BOW = index.getBowFor(nlp_method);
        // String[] TERMS = BOW.toArray(new String[index.getVocab_size()]);


        double[] query_matrix
                = new double[BOW.size()];
        List<Double> vectQuery = new ArrayList<>();
        switch (vsm_version) {
            case VSM_BINARY:
                vectQuery = indexDocument(query);
                break;
            case VSM_TF:
                vectQuery = getTF(query, BOW, false);
                break;
            case VSM_TFIDF:
                vectQuery = getTF(query, BOW, false);
                break;
            case VSM_BM25:
                vectQuery = getTF(query, BOW, false);
                break;
        }

        //terms of vocabulary
        for (int row = 0; row < BOW.size(); row++) {
            query_matrix[row] = vectQuery.get(row);
        }
        return query_matrix;
    }


    public double[] vect_mat_Product(double vect[], double mat[][]) {

        double productArray[] = new double[mat[0].length];

        for (int i = 0; i < mat[0].length; i++) {
            //for (int k = 0; k < vect.length; k++) {
            for (int k = 0; k < mat.length; k++) {
                productArray[i] += mat[k][i] * vect[k];
            }
        }
        return productArray;
    }

    public double[] vectProduct(double a[], double b[]) {
        double[] c = new double[NUM_FACTORS];
        for (int i = 0; i < NUM_FACTORS; i++) {
            c[i] = a[i] * b[i];
        }
        return c;
    }

    public double[] reduceDoc(double[][] V, int index) {
        double[] vect = new double[NUM_FACTORS];

        for (int col = 0; col < NUM_FACTORS; col++) {
            vect[col] = V[col][index];
        }
        return vect;

    }

    public double[] reduceQuery(double[] query, double[][] U, double[] S) {

        double[] vect_mat = vect_mat_Product(query, U);
        double[] reducedDoc = vectProduct(vect_mat, S);

        return reducedDoc;
    }

    public List<Object> computeSVD() throws IOException {
        List<Object> parametersSVD = new ArrayList<>();

        double featureInit = 0.01;
        double initialLearningRate = 0.01;
        int annealingRate = 2000;
        double regularization = 0.00;
        double minImprovement = 0.0000;
        int minEpochs = 10;
        int maxEpochs = 50000;
        System.out.println("SVD matrix");

        SvdMatrix matrix
                = SvdMatrix.svd(transpose(termDocMatrix(VSM_BM25)),
                NUM_FACTORS,
                featureInit,
                initialLearningRate,
                annealingRate,
                regularization,
                null,
                minImprovement,
                minEpochs,
                maxEpochs);

        double[] S = matrix.singularValues();

        double[][] V = matrix.leftSingularVectors();

        double[][] U = matrix.rightSingularVectors();

        int row = V.length;
        int col = V[0].length;

        int row1 = termDocMatrix(VSM_BM25).length;
        int col1 = termDocMatrix(VSM_BM25)[0].length;
        System.out.println("rows of U:" + row + "cols of U:" + col);

        this.parametersSVD.add(U);
        this.parametersSVD.add(S);
        this.parametersSVD.add(V);
        //this.parametersSVD = parametersSVD;

        return parametersSVD;
    }


    public double dotProduct(double[] query, double[] doc) {

        double sum = 0.0;
        for (int k = 0; k < query.length; ++k) {
            sum += query[k] * doc[k];
        }
        return sum;

    }

    public List<Document> getRankingScoresLSI(EvaluationEntity e) throws IOException {


        StringUtilities su = new StringUtilities();
        List<Document> dotProduct = new ArrayList<>();
        List<Document> DOCS = index.getDocuments();
        int indexDoc = 0;

        double[][] U = (double[][]) parametersSVD.get(0);
        double[] S = (double[]) parametersSVD.get(1);
        double[][] V = (double[][]) parametersSVD.get(2);

        if (VSM_BINARY.equals(version)) {
            double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(), VSM_BINARY), U, S);
            //System.out.println("finish");

            for (Document doc : DOCS) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                if (su.hasOneToken(e.getQuery().getContent()) == true) {
                    doc.setContent(su.getAcronym(e.getQuery().getContent()));
                }
                resultdoc.setContent(doc.getContent());

                double[] docMat = reduceDoc(V, indexDoc);
                Double score = dotProduct(queryMat, docMat);
                resultdoc.setScore(score);
                dotProduct.add(resultdoc);
            }
            indexDoc++;
        } else if (VSM_TF.equals(version)) {

            double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(), VSM_TF), U, S);


            for (Document doc : DOCS) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
//            if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                doc.setContent(su.getAcronym(e.getQuery().getContent()));
//            }
                resultdoc.setContent(doc.getContent());

                double[] docMat = reduceDoc(V, indexDoc);
                Double score = dotProduct(queryMat, docMat);
                resultdoc.setScore(score);
                dotProduct.add(resultdoc);
            }
            indexDoc++;
        } else if (VSM_TFIDF.equals(version)) {

            double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(), VSM_TFIDF), U, S);

            for (Document doc : DOCS) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
//            if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                doc.setContent(su.getAcronym(e.getQuery().getContent()));
//            }
                resultdoc.setContent(doc.getContent());

                double[] docMat = reduceDoc(V, indexDoc);
                Double score = dotProduct(queryMat, docMat);
                resultdoc.setScore(score);
                dotProduct.add(resultdoc);
            }
            indexDoc++;
        } else if (VSM_BM25.equals(version)) {

            double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(), VSM_BM25), U, S);

            for (Document doc : DOCS) {
                Document resultdoc = new Document();
                resultdoc.setId(doc.getId());
                resultdoc.setContent(doc.getContent());
//            if (su.hasOneToken(e.getQuery().getContent()) == true) {
//                Document newdoc = new Document();
//                newdoc.setId(doc.getId());
//                newdoc.setContent(su.getAcronym(doc.getContent()));
//                double[] docMat = reduceQuery(query_doc_Matrix(newdoc,VSM_BM25), U, S);
//
//                double score = dotProduct(queryMat, docMat);
//                resultdoc.setScore(score);
//
//                dotProduct.add(resultdoc);
//                indexDoc++;
//            }
//            else{
                //double[] docMat = reduceQuery(query_doc_Matrix(doc, VSM_BM25), U, S);
                double[] docMat = reduceDoc(transpose(V), indexDoc);

                double score = dotProduct(queryMat, docMat);
                resultdoc.setScore(score);

                dotProduct.add(resultdoc);
                indexDoc++;
//            }
            }

        } else {
            System.out.println("Something wrong dude!");
        }

        return dotProduct;

    }

}























