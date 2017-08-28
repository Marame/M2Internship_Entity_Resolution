package practice1;


import org.tartarus.snowball.ext.PorterStemmer;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.models.LanguageModel;
import practice1.models.NGramModel;
import practice1.models.VectorSpaceModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static practice1.Index.NO_NLP_METHOD;
import static practice1.models.LanguageModel.DIRICHLET_SMOOTHING;
import static practice1.models.LanguageModel.JELINEK_SMOOTHING;
import static practice1.models.VectorSpaceModel.VSM_BM25;

/**
 * Created by romdhane on 07/07/17.
 */

public class Main {

    // versions of VSM,
    List<String> vsm_versions = Arrays.asList(VSM_BM25);

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.startTesting(args[0], args[1], args[2]);
    }


    public void startTesting(String queryFile, String documentFile, String Ngram) throws IOException {


      /*practice1.Aggregating aggregate = new practice1.Aggregating();

      aggregate.aggregate(queryFile, documentFile, newFile);
        System.out.println("finishing");*/

        ParseCorpus pf = new ParseCorpus();
        Lemmatizer lem = new Lemmatizer();
        lem.initializeCoreNLP();
        PorterStemmer porterStemmer = new PorterStemmer();
        final Tokenizer tokeniser = new Tokenizer(lem, porterStemmer);

        Index index = new Index(tokeniser);
        List<Document> documents = pf.parseDocuments(documentFile);

        index.setDocuments(documents);
        index.indexAll();

        Evaluation evaluator = new Evaluation();

<<<<<<< HEAD
        List<EvaluationEntity> ee = pf.parseQueries(queryFile);
        evaluator.setEe(ee);

        // VSM - BM25
      VectorSpaceModel vsm = new VectorSpaceModel(VectorSpaceModel.VSM_BM25, NO_NLP_METHOD, index);
=======
        List<EvaluationEntity> ee = pf.parseQueries(queryFile, documents);


        // VSM - Binary
        VectorSpaceModel vsm = new VectorSpaceModel(VectorSpaceModel.VSM_BINARY, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }
        evaluator.printEvaluation(ee);

        // VSM - TF
        removeScores(ee);
        vsm = new VectorSpaceModel(VectorSpaceModel.VSM_TF, NO_NLP_METHOD, index);
>>>>>>> 508e40c4bbe3b7607d8e6a0e1958038cdd36f8b7
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }
<<<<<<< HEAD
        System.out.println("VSM-BM25");
        //evaluator.getEvaluationMeasures();
       evaluator.printEvaluation();

        //  LSI
       /* LSIModel lsi = new LSIModel(VectorSpaceModel.VSM_BM25, NO_NLP_METHOD, index);
     lsi.computeSVD();
        System.out.println("SVD done");
        for (EvaluationEntity e : ee) {
            List<Document> results = lsi.getRankingScoresLSI(e);
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("LSI");
        //evaluator.getEvaluationMeasures();
        evaluator.printEvaluation();*/
       
        // Language Model
        LanguageModel lm = new LanguageModel(JELINEK_SMOOTHING, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = lm.getRankingScoresLM(e, JELINEK_SMOOTHING);
=======
        evaluator.printEvaluation(ee);

        // VSM - TF-IDF
        removeScores(ee);
        vsm = new VectorSpaceModel(VectorSpaceModel.VSM_TFIDF, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm.getRankingScoresVSM(e);
>>>>>>> 508e40c4bbe3b7607d8e6a0e1958038cdd36f8b7
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("Language Model");
        evaluator.printEvaluation();

        // VSM - BM25
        removeScores(ee);
        vsm = new VectorSpaceModel(VSM_BM25, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }
        evaluator.printEvaluation(ee);


        //NGRAM
        int ngram = Integer.parseInt(Ngram);

        removeScores(ee);       // a bit crap, but the most efficient way
        NGramModel nGramModel = new NGramModel(NO_NLP_METHOD, lem, index, ngram);
<<<<<<< HEAD
        nGramModel.setN(ngram);
=======
>>>>>>> 508e40c4bbe3b7607d8e6a0e1958038cdd36f8b7
        for (EvaluationEntity e : ee) {
            List<Document> results = nGramModel.getRankingScoresNgram(e);
            sortResults(results);
            e.setResults(results);
        }
<<<<<<< HEAD
        System.out.println("NGRAM");
        evaluator.printEvaluation();
=======
        evaluator.printEvaluation(ee);


        //Language Model (Jelinek smoothing)
        removeScores(ee);       // a bit crap, but the most efficient way
        LanguageModel languageModel1 = new LanguageModel(JELINEK_SMOOTHING, NO_NLP_METHOD, lem, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = languageModel1.getRankingScoresLM(e);
            sortResults(results);
            e.setResults(results);
        }
        evaluator.printEvaluation(ee);


        //Language Model (Dirichlet smoothing)
        removeScores(ee);       // a bit crap, but the most efficient way
        LanguageModel languageModel2 = new LanguageModel(DIRICHLET_SMOOTHING, NO_NLP_METHOD, lem, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = languageModel2.getRankingScoresLM(e);
            sortResults(results);
            e.setResults(results);
        }
        evaluator.printEvaluation(ee);

        /*

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb.append(String.format("============================================================\n"));

        for (String version : vsm_versions) {

            LSIModel lsi = new LSIModel(version, index, STEMMING_NLP_METHOD);
            // lsi.computeSVD();

            *//*Evaluation eval = new Evaluation();

            eval.final_evaluation(ee, "", version, NO_NLP_METHOD, lem, index, -1, true);*//*

           *//* Evaluation eval1 = new Evaluation();

            eval1.final_evaluation(ee, "", version, STEMMING_NLP_METHOD, lem, index, -1, true);*//*


            Evaluation eval2 = new Evaluation(ee);

            //List<Double> evalMeasures =  eval2.getEvaluationMeasures("", version, STEMMING_NLP_METHOD, lem, index, -1, true);


            sb.append("************** MAP values ***************" + "\n");
            // sb.append(String.format("%-20s%-20s", version, evalMeasures.get(0) + "\n"));
            //sb.append(String.format("%-20s%-20s", version, evalMeasures.get(1) + "\n"));

            *//*sb.append("************** Micro average precision values ***************" + "\n");
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
            sb.append("***********nCDG**************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getnCDG() + "||", eval1.getnCDG() + "||", eval2.getnCDG() + "\n"));*//*

        }
        // System.out.println(sb.toString());

        System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");

        StringBuilder sb1 = new StringBuilder();
        sb.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb.append(String.format("============================================================\n"));

        for (String version : vsm_versions) {
            Evaluation eval2 = new Evaluation(ee);
            List<Double> evalMeasures = eval2.getEvaluationMeasures("", version, NO_NLP_METHOD, lem, index, -1, false);

            sb1.append("************** MAP values ***************" + "\n");
            sb1.append(String.format("%-20s%-20s", version, evalMeasures.get(0) + "\n"));
            sb1.append("***********nCDG**************" + "\n");
            sb1.append(String.format("%-20s%-20s", version, evalMeasures.get(1) + "\n"));

            sb.append("************** Micro average precision values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, evalMeasures.get(2) + "\n"));
            sb.append("************** Micro average recall values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, evalMeasures.get(3) + "\n"));
            sb.append("************** Macro average precision values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, evalMeasures.get(4) + "\n"));
            sb.append("************** Macro average recall values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, evalMeasures.get(5) + "\n"));
            sb.append("************** Micro average F1 values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, evalMeasures.get(6) + "\n"));
            sb.append("************** Macro average F1 values ***************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getMacro_average_F1() + "||", eval1.getMacro_average_F1() + "||", eval2.getMacro_average_F1() + "\n"));
            sb.append("***********nCDG**************" + "\n");
            sb.append(String.format("%-20s%-20s%-20s%-20s", version, eval.getnCDG() + "||", eval1.getnCDG() + "||", eval2.getnCDG() + "\n"));

        }
        System.out.println(sb1.toString());


        System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.format("%-10s%-30s%-30s%-30s\n", "version", NO_NLP_METHOD, STEMMING_NLP_METHOD, LEMMATIZING_NLP_METHOD + "\n"));
        sb2.append(String.format("==========================================================\n"));

        for (String version : smoothing_versions) {

            //Evaluation eval = new Evaluation(ee);
            //eval.final_evaluation(ee, version, "", NO_NLP_METHOD, lem, index, -1, false);

            Evaluation eval2 = new Evaluation(ee);
            List<Double> evalMeasures = eval2.getEvaluationMeasures(version, "", STEMMING_NLP_METHOD, lem, index, -1, false);

            sb2.append("************** MAP ***************" + "\n");
            sb2.append(String.format("%-20s%-20s", version, evalMeasures.get(0) + "\n"));
            sb2.append("***********nCDG***************" + "\n");
            sb2.append(String.format("%-20s%-20s", version, evalMeasures.get(1) + "\n"));


        }
        System.out.println(sb2.toString());
*/

        /*System.out.println("$$$$$$$$$$$$ Results for NGram Model $$$$$$$$$$$$$");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.format("%-10s%-20s%-20s%-20s", "version", "MAP:nothing", "MAP:Stemming", "MAP:Lemmatizing" + "\n"));
        sb2.append(String.format("============================================================\n"));


        Evaluation eval = new Evaluation();

        eval.final_evaluation(ee, "", "", NO_NLP_METHOD, lem, index, ngram,  false);


        Evaluation eval1 = new Evaluation();


        eval1.final_evaluation(ee, "", "", STEMMING_NLP_METHOD, lem, index, ngram,  false);


        Evaluation eval2 = new Evaluation();

        eval2.final_evaluation(ee, "", "", LEMMATIZING_NLP_METHOD, lem, index, ngram,  false);


        /*sb2.append("************** MAP values ***************" + "\n");
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
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getMacro_average_F1() + "||", eval1.getMacro_average_F1() + "||", eval2.getMacro_average_F1() + "\n"));*/
        /*sb2.append("************** nCDG ***************" + "\n");
        sb2.append(String.format("%-20s%-20s%-20s%-20s", "", eval.getnCDG() + "||", eval1.getnCDG() + "||", eval2.getnCDG() + "\n"));
        System.out.println(sb2.toString());*/
>>>>>>> 508e40c4bbe3b7607d8e6a0e1958038cdd36f8b7

    }
    private void removeScores(List<EvaluationEntity> ee) {
        for (EvaluationEntity e : ee) {
            Collections.emptyList();
            e.setResults(null);
        }
    }

    public void sortResults(List<Document> results) {

        Collections.sort(results, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                final double document1 = o1.getScore();
                final double document2 = o2.getScore();
                return document1 < document2 ? 1
                        : document1 > document2 ? -1 : 0;
            }
        });
    }

}




