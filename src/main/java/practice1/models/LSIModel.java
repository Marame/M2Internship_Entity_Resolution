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
    static final int NUM_FACTORS = 2;
    static final List<Object> parametersSVD = new ArrayList<>();


    public LSIModel(String version, Index index, String nlp_method) {
        this.version = version;
        this.index = index;
        this.nlp_method = nlp_method;

    }

    public LSIModel() {
    }

    public double[] idf_Matrix() throws IOException {
        List<String> BOW = index.getBowFor(nlp_method);
        String[] TERMS = BOW.toArray(new String[index.getVocab_size()]);
        double[] idf_matrix
                = new double[index.getVocab_size()];

        List<Double> listIDF = getIDF();

        for (int row = 0; row < BOW.size() - 1; row++) {
            idf_matrix[row] = listIDF.get(row);
        }
        return idf_matrix;
    }

    //construct the query matrix
    public double[] query_doc_Matrix(Document query, String vsm_version) throws IOException {
        List<String> BOW = index.getBowFor(nlp_method);
        String[] TERMS = BOW.toArray(new String[index.getVocab_size()]);


        double[] query_matrix
                = new double[TERMS.length];
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
                vectQuery = getTF(query, BOW, true);
                break;
        }

        //terms of vocabulary
        for (int row = 0; row < BOW.size(); row++) {
            query_matrix[row] = vectQuery.get(row);
        }
        return query_matrix;
    }


    public double[]  vect_mat_Product (double vect[], double mat[][])
    {
        int row, col;
        double productArray[]=new double[index.getDocuments().size()];
        for (row =0;  row<mat.length ; row++)
        {
            for(col=0; col< NUM_FACTORS; col++)
            {
                productArray[row]=mat[row][col] * vect[row];
            }
        }
        return productArray;
    }
    public double[] vectProduct(double a[], double b[]){
      double[] c = new double[NUM_FACTORS];
      for(int i = 0; i < NUM_FACTORS; i++){
            c[i] = a[i] * b[i];
          }
       return c;
    }

    public double[] reduceDoc(double[][] V, int index){
        double[] vect = new double[NUM_FACTORS];

        for(int col =0; col<NUM_FACTORS; col++)
        {
            vect[col]=V[index][col];
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
                = SvdMatrix.svd(termDocMatrix(VSM_BM25),
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
        System.out.println("SVD done");
        this.parametersSVD.add(U);
        this.parametersSVD.add(S);
        this.parametersSVD.add(V);
        //this.parametersSVD = parametersSVD;

        return parametersSVD;
    }


    public double dotProduct(double[] query, double[] doc){

        double sum = 0.0;
        for (int k = 0; k < query.length; ++k)
            sum += query[k] * doc[k] ;
        return sum;

    }
    public double dotProductIDF(double[] query, double[] doc, double[] idf){

        double sum = 0.0;
        for (int k = 0; k < query.length; ++k)
            sum += query[k] * doc[k]*idf[k] ;
        return sum;

    }

    public List<Document> getRankingScoresLSI(EvaluationEntity e) throws IOException{
        StringUtilities su = new StringUtilities();
        List<Document> dotProduct = new ArrayList<>();
        List<Document> DOCS = index.getDocuments();


        double[][] U = (double[][])parametersSVD.get(0);
        double[] S = (double[])parametersSVD.get(1);
        double[][] V = (double[][])parametersSVD.get(2);



    if(VSM_BINARY.equals(version)) {
        double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(),VSM_BINARY), U, S);
        //System.out.println("finish");
        int indexDoc = 0;
        for (Document doc : DOCS) {
            Document resultdoc = new Document();
            resultdoc.setId(doc.getId());
            if (su.hasOneToken(e.getQuery().getContent()) == true) {
                doc.setContent(su.getAcronym(e.getQuery().getContent()));
            }
            resultdoc.setContent(doc.getContent());

            double[] docMat = reduceDoc(V,indexDoc );
            Double score = dotProduct(queryMat, docMat);
            resultdoc.setScore(score);
            dotProduct.add(resultdoc);
        }
        indexDoc ++;
    }

        else if(VSM_TF.equals(version)) {

        double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(),VSM_TF), U, S);

        int indexDoc = 0;
        for (Document doc : DOCS) {
            Document resultdoc = new Document();
            resultdoc.setId(doc.getId());
            if (su.hasOneToken(e.getQuery().getContent()) == true) {
                doc.setContent(su.getAcronym(e.getQuery().getContent()));
            }
            resultdoc.setContent(doc.getContent());

            double[] docMat = reduceDoc(V,indexDoc);
            Double score = dotProduct(queryMat, docMat);
            resultdoc.setScore(score);
            dotProduct.add(resultdoc);
        }
        indexDoc ++;
    }
    else if(VSM_TFIDF.equals(version)) {

        double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(),VSM_TFIDF), U, S);
        double[] idfMat = reduceQuery(idf_Matrix(),U, S);
        int indexDoc = 0;
        for (Document doc : DOCS) {
            Document resultdoc = new Document();
            resultdoc.setId(doc.getId());
            if (su.hasOneToken(e.getQuery().getContent()) == true) {
                doc.setContent(su.getAcronym(e.getQuery().getContent()));
            }
            resultdoc.setContent(doc.getContent());

           double[] docMat  = reduceDoc(V,indexDoc);
            Double score = dotProductIDF(queryMat, docMat,idfMat);
            resultdoc.setScore(score);
            dotProduct.add(resultdoc);
        }
        indexDoc ++;
    }
    else if(VSM_BM25.equals(version)) {

         double[] queryMat = reduceQuery(query_doc_Matrix(e.getQuery(),VSM_BM25), U, S);
        double[] idfMat = reduceQuery(idf_Matrix(),U, S);
        int indexDoc = 0;
        for (Document doc : DOCS) {
            Document resultdoc = new Document();
            resultdoc.setId(doc.getId());
            if (su.hasOneToken(e.getQuery().getContent()) == true) {
                doc.setContent(su.getAcronym(e.getQuery().getContent()));
            }
            resultdoc.setContent(doc.getContent());

            double[] docMat = reduceDoc(V,indexDoc);
            Double score = dotProductIDF(queryMat, docMat,idfMat);
            resultdoc.setScore(score);
            dotProduct.add(resultdoc);
        }
        indexDoc ++;
    }
    else {
        System.out.println("Something wrong dude!");
    }

        return dotProduct;

    }

    }























