package practice1;

import org.tartarus.snowball.ext.PorterStemmer;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static practice1.Index.*;

/**
 * Created by romdhane on 07/07/17.
 */

public class Main {
    public final static String VSM_BINARY = "Binary";
    public final static String VSM_TF = "TF";
    public final static String VSM_TFIDF = "TF/IDF";
    public final static String VSM_BM25 = "BM25";


    public final static String JELINEK_SMOOTHING = "jelinek-mercer";
    public final static String DIRICHLET_SMOOTHING = "dirichlet-prior";

    // versions of VSM
    List<String> vsm_versions = Arrays.asList(VSM_BINARY, VSM_TF, VSM_TFIDF, VSM_BM25);

    //Versions of smoothing in Language Model
    List<String> smoothing_versions = Arrays.asList(JELINEK_SMOOTHING, DIRICHLET_SMOOTHING);

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.startTesting(args[0], args[1], args[2]);

    }


    public void startTesting(String newfilenameQueries, String filenameDocs, String Ngram) throws IOException {
        //practice1.Aggregating training = new practice1.Aggregating();
        //training.train(filenameQueries, filenameDocs, newfilenameQueries);

        //Our initial Evaluation entities


        Lemmatizer lem = new Lemmatizer();
        lem.initializeCoreNLP();
        PorterStemmer porterStemmer = new PorterStemmer();
        final Tokenizer tokeniser = new Tokenizer(lem, porterStemmer);

        System.out.println(lem.toString());
        ParseFiles pf = new ParseFiles();

        Index index = new Index(tokeniser);
        List<Document> documents = pf.retrieveDocuments(filenameDocs);
        index.setDocuments(documents);
        index.indexAll();

        //List<Document> queries = pf.retrieveDocuments(newfilenameQueries);
        //index.setQueries(queries);

        //Getting all the indexes
//        index.indexAllBows();

        //index TF for queries
//        index.indexAll_not_normalisedTF();
//        index.indexAll_normalisedTF();

        //index TF for documents
//        index.indexAll_not_normalisedTF();
//        index.indexAll_normalisedTF();*/

        List<EvaluationEntity> ee = pf.parseArgs(newfilenameQueries, filenameDocs);
        int ngram = Integer.parseInt(Ngram);

/*        for (EvaluationEntity e : ee) {
            System.out.println("**Query**");
            System.out.println(e.getQuery().getContent());
            System.out.println("**Relevant documents**");
            for (Document d : e.getRelevant_documents()) {
                System.out.println(d.getId() + "->" + d.getContent());
            }

        }*/


        System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb.append(String.format("============================================================\n"));

        for (String version : vsm_versions) {

            System.out.println("*********No NLP METHOD************");
            Evaluation eval = new Evaluation();

            eval.final_evaluation(ee, "", version, NO_NLP_METHOD, lem, index, -1);


            System.out.println("*********STEMMING NLP METHOD************");
            Evaluation eval1 = new Evaluation();

            eval1.final_evaluation(ee, "", version, STEMMING_NLP_METHOD, lem, index, -1);


            //System.out.println("*********LEMMATIZING NLP METHOD************");
            Evaluation eval2 = new Evaluation();

            eval2.final_evaluation(ee, "", version, LEMMATIZING_NLP_METHOD, lem, index, -1);


            sb.append("************** MAP values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));

            sb.append("************** Micro average precision values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_precision() + "||", eval1.getMicro_average_precision() + "||", eval2.getMicro_average_precision() + "\n"));
            sb.append("************** Micro average recall values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_recall() + "||", eval1.getMicro_average_recall() + "||", eval2.getMicro_average_recall() + "\n"));
            sb.append("************** Macro average precision values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_precision() + "||", eval1.getMacro_average_precision() + "||", eval2.getMacro_average_precision() + "\n"));
            sb.append("************** Macro average recall values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_recall() + "||", eval1.getMacro_average_recall() + "||", eval2.getMacro_average_recall() + "\n"));
            sb.append("************** Micro average F1 values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_F1() + "||", eval1.getMicro_average_F1() + "||", eval2.getMicro_average_F1() + "\n"));
            sb.append("************** Macro average F1 values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_F1() + "||", eval1.getMacro_average_F1() + "||", eval2.getMacro_average_F1() + "\n"));
        }
        System.out.println(sb.toString());


        System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(String.format("%-10s%-30s%-30s%-30s\n", "version", NO_NLP_METHOD, STEMMING_NLP_METHOD, LEMMATIZING_NLP_METHOD + "\n"));
        sb1.append(String.format("==========================================================\n"));

        for (String version : smoothing_versions) {

            Evaluation eval = new Evaluation();
            eval.final_evaluation(ee, version, "", NO_NLP_METHOD, lem, index, -1);

            Evaluation eval1 = new Evaluation();
            eval1.final_evaluation(ee, version, "", STEMMING_NLP_METHOD, lem, index, -1);

            Evaluation eval2 = new Evaluation();
            eval2.final_evaluation(ee, version, "", LEMMATIZING_NLP_METHOD, lem, index, -1);

            sb1.append("************** MAP values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
            sb1.append("************** Micro average precision values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_precision() + "||", eval1.getMicro_average_precision() + "||", eval2.getMicro_average_precision() + "\n"));
            sb1.append("************** Micro average recall values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_recall() + "||", eval1.getMicro_average_recall() + "||", eval2.getMicro_average_recall() + "\n"));
            sb1.append("************** Macro average precision values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_precision() + "||", eval1.getMacro_average_precision() + "||", eval2.getMacro_average_precision() + "\n"));
            sb1.append("************** Macro average recall values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_recall() + "||", eval1.getMacro_average_recall() + "||", eval2.getMacro_average_recall() + "\n"));
            sb1.append("************** Micro average F1 values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMicro_average_F1() + "||", eval1.getMicro_average_F1() + "||", eval2.getMicro_average_F1() + "\n"));
            sb1.append("************** Macro average F1 values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_F1() + "||", eval1.getMacro_average_F1() + "||", eval2.getMacro_average_F1() + "\n"));

        }
//        System.out.println(sb1.toString());


        System.out.println("$$$$$$$$$$$$ Results for NGram Model $$$$$$$$$$$$$");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb2.append(String.format("============================================================\n"));


        // System.out.println("*********No NLP METHOD************");

        Evaluation eval = new Evaluation();

        eval.final_evaluation(ee, "", "", NO_NLP_METHOD, lem, index, ngram);


        // System.out.println("*********STEMMING NLP METHOD************");

        Evaluation eval1 = new Evaluation();


        eval1.final_evaluation(ee, "", "", STEMMING_NLP_METHOD, lem, index, ngram);


        //System.out.println("*********LEMMATIZING NLP METHOD************");

        Evaluation eval2 = new Evaluation();

        eval2.final_evaluation(ee, "", "", LEMMATIZING_NLP_METHOD, lem, index, ngram);


        sb2.append("************** MAP values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
        sb2.append("************** Micro average precision values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMicro_average_precision() + "||", eval1.getMicro_average_precision() + "||", eval2.getMicro_average_precision() + "\n"));
        sb2.append("************** Micro average recall values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMicro_average_recall() + "||", eval1.getMicro_average_recall() + "||", eval2.getMicro_average_recall() + "\n"));
        sb2.append("************** Macro average precision values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMacro_average_precision() + "||", eval1.getMacro_average_precision() + "||", eval2.getMacro_average_precision() + "\n"));
        sb2.append("************** Macro average recall values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMacro_average_recall() + "||", eval1.getMacro_average_recall() + "||", eval2.getMacro_average_recall() + "\n"));
        sb2.append("************** Micro average F1 values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMicro_average_F1() + "||", eval1.getMicro_average_F1() + "||", eval2.getMicro_average_F1() + "\n"));
        sb2.append("************** Macro average F1 values ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMacro_average_F1() + "||", eval1.getMacro_average_F1() + "||", eval2.getMacro_average_F1() + "\n"));
        //System.out.println(sb2.toString());

    }

}



