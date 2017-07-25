import Models.NGram;

import java.io.IOException;
import java.util.Map;


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
        main.startTesting(args[0], args[1]);


    }


    public void startTesting(String filenameQueries, String filenameDocs) throws IOException

    {
        //public void startTesting(String filenameQueries, String filenameDocs) throws FileNotFoundException, IOException {

        //Our initial Evaluation entities
           /* for (EvaluationEntity e : ee) {
                System.out.println("query: " + e.getQuery().getName());
                int i = 1;
                for (Document doc : e.getDocuments()) {
                    System.out.println("d" + i + ": " + doc.getName());
                    i++;
                }
            }*/

     /*   Lemmatizer lem = new Lemmatizer();
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
            eval.final_evaluation(ee, "", version, "nothing", lem);
            long endTime = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
           // System.out.print("Execution time for nothing  is " + formatter.format((endTime - startTime) / 1000d) + " seconds"+"\n");

            Evaluation eval1 = new Evaluation();
            long startTime1 = System.currentTimeMillis();
            eval1.final_evaluation(ee, "", version, "stemming", lem);
            long endTime1 = System.currentTimeMillis();
            NumberFormat formatter1 = new DecimalFormat("#0.00000");
            //System.out.print("Execution time for stemming  is " + formatter1.format((endTime1 - startTime1)/ 1000d) + " seconds"+"\n");
            Evaluation eval2 = new Evaluation();
            long startTime2 = System.currentTimeMillis();
            eval2.final_evaluation(ee, "", version, "lemmatizing", lem);
            long endTime2 = System.currentTimeMillis();
            NumberFormat formatter2 = new DecimalFormat("#0.00000");
            //System.out.print("Execution time for lemmatizing  is " + formatter2.format((endTime2 - startTime2)/ 1000d) + " seconds"+"\n");
            sb.append(String.format("%-10s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
        }
        System.out.println(sb.toString());


        System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

        StringBuilder sb1 = new StringBuilder();
        sb1.append(String.format("%-10s%-30s%-30s%-30s\n", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb1.append(String.format("==========================================================\n"));

        for (String version : smoothing_versions) {

            Evaluation eval = new Evaluation();
            eval.final_evaluation(ee, version, "", "nothing", lem);

            Evaluation eval1 = new Evaluation();
            eval1.final_evaluation(ee, version, "", "stemming", lem);
            Evaluation eval2 = new Evaluation();
            eval2.final_evaluation(ee, version, "", "lemmatizing", lem);
            sb1.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMAP() + "||", eval1.getMAP() + "||", eval2.getMAP() + "\n"));
        }
        System.out.println(sb1.toString());
    }*/




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
        NGram ng = new NGram();
        Map<String, Map<String, Double>> nGramsFeature = ng.computeFeature(" news about",filenameQueries, filenameDocs);
        // Map<String, List<String>> nGramsFeature = ng.ngrams(filenameQueries, filenameDocs);
        for (String st : nGramsFeature.keySet()) {

            System.out.println("Key=" + "\t" + st);
            System.out.println("***********ngrams with frequencies***********");
            for (String s : nGramsFeature.get(st).keySet()) {

                System.out.println("Token key=" + "\t" + s);
                //System.out.println("Token count=" + "\t" + nGramsFeature.get(st).get(s));
            }
        }

    }
}
