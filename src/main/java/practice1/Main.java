package practice1;


import org.tartarus.snowball.ext.PorterStemmer;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.models.NGramModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static practice1.Index.NO_NLP_METHOD;
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

        List<EvaluationEntity> ee = pf.parseQueries(queryFile, documents);

        // VSM - BM25

       /* removeScores(ee);
=======
/*        removeScores(ee);
>>>>>>> 6ec9fcc4541979468428f957dcaeb3b850c9f3c8
        VectorSpaceModel vsm25 = new VectorSpaceModel(VectorSpaceModel.VSM_BM25, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm25.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }





        System.out.println("VSM-BM25");
        printRanking(ee);
        evaluator.printEvaluation(ee);*/


       /* removeScores(ee);
=======
>>>>>>> 6ec9fcc4541979468428f957dcaeb3b850c9f3c8
        // VSM - Binary
        /*removeScores(ee);
        VectorSpaceModel vsmbin = new VectorSpaceModel(VectorSpaceModel.VSM_BINARY, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsmbin.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("Binary");
        printRanking(ee);
        evaluator.printEvaluation(ee);*/

        // VSM - TF
/*        removeScores(ee);
        VectorSpaceModel vsmtf = new VectorSpaceModel(VectorSpaceModel.VSM_TF, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsmtf.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }

        System.out.println("VSM-TF");
        evaluator.printEvaluation(ee);*/

        // VSM - TF-IDF
/*        removeScores(ee);
        VectorSpaceModel vsmtfidf = new VectorSpaceModel(VectorSpaceModel.VSM_TFIDF, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsmtfidf.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }

        System.out.println("VSM-BM25");
        evaluator.printEvaluation(ee);*/


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
       /* removeScores(ee);

/*        removeScores(ee);

        LanguageModel lm = new LanguageModel(JELINEK_SMOOTHING, NO_NLP_METHOD, lem, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = lm.getRankingScoresLM(e);
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("Language Model: jelinek");

        evaluator.printEvaluation(ee);*/


        //Language Model (Dirichlet smoothing)
/*        removeScores(ee);       // a bit crap, but the most efficient way
        LanguageModel languageModel2 = new LanguageModel(DIRICHLET_SMOOTHING, NO_NLP_METHOD, lem, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = languageModel2.getRankingScoresLM(e);
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("Language model: dirichlet");
        evaluator.printEvaluation(ee);*/


        //NGRAM
        int ngram = Integer.parseInt(Ngram);

        removeScores(ee);       // a bit crap, but the most efficient way
        NGramModel nGramModel = new NGramModel(NO_NLP_METHOD, lem, index, ngram);

        for (EvaluationEntity e : ee) {
            List<Document> results = nGramModel.getRankingScoresNgram(e);
            sortResults(results);
            e.setResults(results);
        }

        printRanking(ee);
        System.out.println("NGRAM");
        evaluator.printEvaluation(ee);


    }

    private void printRanking(List<EvaluationEntity> ee) {

        for (EvaluationEntity e : ee) {
            System.out.println("Query: " + e.getQuery());
            List<Document> sublist = e.getResults().subList(0, 15);
            for (Document doc : sublist) {
                System.out.println(doc);
            }
        }
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

                return document1 < document2 ? 1 : document1 > document2 ? -1 : 0;
            }
        });
    }

}




