package practice1;

import practice1.entities.EvaluationEntity;
import practice1.models.NGram;
import practice1.utilities.Lemmatizer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by romdhane on 07/07/17.
 */

public class Main {
    public final static String VSM_BINARY = "Binary";
    public final static String VSM_TF = "TF";
    public final static String VSM_TFIDF = "TF/IDF";
    public final static String VSM_BM25 = "BM25";
    
    public final static String NO_NLP_METHOD = "nothing";
    public final static String STEMMING_NLP_METHOD = "stemming";
    public final static String LEMMATIZING_NLP_METHOD = "lemmatizing";

    public final static String JELINEK_SMOOTHING = "jelinek-mercer";
    public final static String DIRICHLET_SMOOTHING = "dirichlet-prior";

    // versions of VSM
    List<String> vsm_versions = Arrays.asList(VSM_BINARY, VSM_TF, VSM_TFIDF, VSM_BM25);
    
    //Versions of smoothing in Language Model
    List<String> nlp_methods = Arrays.asList(STEMMING_NLP_METHOD, LEMMATIZING_NLP_METHOD, NO_NLP_METHOD);
    List<String> smoothing_versions = Arrays.asList(JELINEK_SMOOTHING, DIRICHLET_SMOOTHING);

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.startTesting(args[0], args[1]);
        //main.startTestingNGram(args[1], args[2]);
    }


    public void startTesting(String filenameQueries, String filenameDocs) throws IOException {

        //Our initial Evaluation entities
//            for (EvaluationEntity e : ee) {
//                System.out.println("query: " + e.getQuery().getName());
//                int i = 1;
//                for (Document doc : e.getDocuments()) {
//                    System.out.println("d" + i + ": " + doc.getName());
//                    i++;
//                }
//            }

        Lemmatizer lem = new Lemmatizer();
        lem.initializeCoreNLP();
        ParseFiles pf = new ParseFiles();

        List<EvaluationEntity> ee = pf.parseArgs(filenameQueries, filenameDocs);
        System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb.append(String.format("============================================================\n"));
        for (String version : vsm_versions) {

            Evaluation eval = new Evaluation();
            long startTime = System.currentTimeMillis();
            eval.final_evaluation(ee, "", version, NO_NLP_METHOD, lem);
            long endTime = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            // System.out.print("Execution time for nothing  is " + formatter.format((endTime - startTime) / 1000d) + " seconds"+"\n");

            Evaluation eval1 = new Evaluation();
            long startTime1 = System.currentTimeMillis();
            eval1.final_evaluation(ee, "", version, STEMMING_NLP_METHOD, lem);
            long endTime1 = System.currentTimeMillis();
            NumberFormat formatter1 = new DecimalFormat("#0.00000");
            //System.out.print("Execution time for stemming  is " + formatter1.format((endTime1 - startTime1)/ 1000d) + " seconds"+"\n");
            Evaluation eval2 = new Evaluation();
            long startTime2 = System.currentTimeMillis();
            eval2.final_evaluation(ee, "", version, LEMMATIZING_NLP_METHOD, lem);
            long endTime2 = System.currentTimeMillis();
            NumberFormat formatter2 = new DecimalFormat("#0.00000");
            //System.out.print("Execution time for lemmatizing  is " + formatter2.format((endTime2 - startTime2)/ 1000d) + " seconds"+"\n");
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
            eval.final_evaluation(ee, version, "", NO_NLP_METHOD, lem);

            Evaluation eval1 = new Evaluation();
            eval1.final_evaluation(ee, version, "", STEMMING_NLP_METHOD, lem);
            Evaluation eval2 = new Evaluation();
            eval2.final_evaluation(ee, version, "", LEMMATIZING_NLP_METHOD, lem);

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
        //System.out.println(sb1.toString());
       /*Training training = new Training();
        training.train(filenameQueries, filenameDocs);*/

    }




        /*for (EvaluationEntity e : ee) {
            System.out.println("**Query**");
            System.out.println(e.getQuery().getName());
            System.out.println("**Relevant documents**");
            for (Document d : e.getRelevant_documents()) {
                System.out.println(d.getId() + "->" + d.getName());
            }
            System.out.println("**Documents**");
            for (Document d : e.getDocuments()) {
                System.out.println(d.getId() + "->" + d.getName());
            }

        }*/


    public void startTestingNGram(String filenameDocs, String ngram) throws IOException {
        NGram ng = new NGram();
        ng.setFilenameDocuments(filenameDocs);
        ng.setN(Integer.parseInt(ngram));
        Map<String, Map<String, Double>> nGramsFeatures = ng.computeFeatures("wall myocardial infarction", "Dice");

        // Map<String, List<String>> nGramsFeature = ng.ngrams(filenameQueries, filenameDocs);
        for (String st : nGramsFeatures.keySet()) {

            System.out.println("Key=" + "\t" + st);
            System.out.println("***********ngrams with average scores***********");
            double sum = 0;
            double average = 0;

            for (String s : nGramsFeatures.get(st).keySet()) {


                //System.out.println("key=" + "\t" + s);
                //System.out.println("jaccard score=" + "\t" + nGramsFeatures.get(st).get(s));
                sum += nGramsFeatures.get(st).get(s);
            }

            average = sum / (double) nGramsFeatures.get(st).keySet().size();
            System.out.println("average:" + "\t" + average);

        }


    }
}
