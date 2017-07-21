import Entities.EvaluationEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * Created by romdhane on 07/07/17.
 */

public class Main {
    // versions of VSM
    String[] vsm_versions = {new String("Binary"), new String("TF"), new String("TF/IDF"), new String("BM25")};
    //Versions of smoothing in Language Model
    String[] nlp_methods = {new String("stemming"), new String("lemmatizing"), new String("nothing")};
    String[] smoothing_versions = {new String("jelinek-mercer"), new String("dirichlet-prior")};

    public static void main(String[] args) throws IOException {

        Main main = new Main();
        //main.startTesting(args[0], args[1], args[2], args[3], args[4]);
        main.startTesting(args[0], args[1]);
    }


   // public void startTesting(String filenameQueries, String filenameDocs, String filePredict, String ngram, String K) throws FileNotFoundException, IOException

    //{
    public void startTesting(String filenameQueries, String filenameDocs) throws FileNotFoundException, IOException {

        //Our initial Evaluation entities
           /* for (EvaluationEntity e : ee) {
                System.out.println("query: " + e.getQuery().getName());
                int i = 1;
                for (Document doc : e.getDocuments()) {
                    System.out.println("d" + i + ": " + doc.getName());
                    i++;
                }
            }*/

        ParseFiles pf = new ParseFiles();

        List<EvaluationEntity> ee = pf.parseArgs(filenameQueries, filenameDocs);
        System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb.append(String.format("============================================================\n"));
        for (String version : vsm_versions) {

            Evaluation eval = new Evaluation();
            long startTime = System.currentTimeMillis();
            eval.final_evaluation(ee, "", version, "nothing");
            long endTime = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            System.out.print("Execution time for nothing  is " + formatter.format((endTime - startTime) / 1000d) + " seconds"+"\n");

            Evaluation eval1 = new Evaluation();
            long startTime1 = System.currentTimeMillis();
            eval1.final_evaluation(ee, "", version, "stemming");
            long endTime1 = System.currentTimeMillis();
            NumberFormat formatter1 = new DecimalFormat("#0.00000");
            System.out.print("Execution time for stemming  is " + formatter1.format((endTime1 - startTime1)/ 1000d) + " seconds"+"\n");
            Evaluation eval2 = new Evaluation();
            long startTime2 = System.currentTimeMillis();
            eval2.final_evaluation(ee, "", version, "lemmatizing");
            long endTime2 = System.currentTimeMillis();
            NumberFormat formatter2 = new DecimalFormat("#0.00000");
            System.out.print("Execution time for lemmatizing  is " + formatter2.format((endTime2 - startTime2)/ 1000d) + " seconds"+"\n");
            sb.append(String.format("%-10s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
        }
        //System.out.println(sb.toString());


        System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(String.format("%-10s%-30s%-30s%-30s\n", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb1.append(String.format("==========================================================\n"));

        for (String version : smoothing_versions) {

            Evaluation eval = new Evaluation();
            eval.final_evaluation(ee, version, "", "nothing");

            Evaluation eval1 = new Evaluation();
            eval1.final_evaluation(ee, version, "", "stemming");
            Evaluation eval2 = new Evaluation();
            eval2.final_evaluation(ee, version, "", "lemmatizing");
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
        }
       // System.out.println(sb1.toString());
    }
}

        /*String trainFile = filenameDocs;
        String predictFile = filePredict;
        int nGram = Integer.parseInt(ngram);
        double k = Double.parseDouble(K);
        double[] linearInterpoParams;
           /* if (args.length > 5) {
            linearInterpoParams = new double[ngram];
            for (int i = 0; i < ngram; i++) {
                linearInterpoParams[i] = Double.parseDouble(args[i+4]);
            }
        } else
          linearInterpoParams = null;*/

       /* NGram lm = new NGram(nGram);
		/*if (args.length > 4 + ngram) {
        int lowFreThre = Integer.parseInt(args[ngram+4]), unkThre = Integer.parseInt(args[ngram+5]);
        lm = new NGram(ngram, K, linearInterpoParams, lowFreThre, unkThre);
    } else
    lm = new NGram(ngram, K, linearInterpoParams, 5, 6);*/

		/*lm.train(trainFile);
    Double[] pred = lm.predict(predictFile);
    String savePred = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
		lm.savePredictions(pred, "/pred-" + savePred + ".txt");
		lm.evaluate(pred);*/




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








