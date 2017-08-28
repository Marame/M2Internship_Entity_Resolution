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

    // versions of VSM,
    List<String> vsm_versions = Arrays.asList(VSM_BM25);

    //Versions of smoothing in Language Model
    List<String> smoothing_versions = Arrays.asList(JELINEK_SMOOTHING, DIRICHLET_SMOOTHING);

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.startTesting(args[0], args[1], args[2], args[3]);
    }


    public void startTesting(String queryFile, String documentFile, String stopWordsFile,String Ngram) throws IOException {


      /*practice1.Aggregating aggregate = new practice1.Aggregating();

      aggregate.aggregate(queryFile, documentFile, newFile);
        System.out.println("finishing");*/

        ParseCorpus pf = new ParseCorpus();
        Lemmatizer lem = new Lemmatizer();
        lem.initializeCoreNLP();
        PorterStemmer porterStemmer = new PorterStemmer();
        final Tokenizer tokeniser = new Tokenizer(lem, porterStemmer);

        Index index = new Index(tokeniser);
        index.setStopWordsFile(stopWordsFile);
        List<Document> documents = pf.parseDocuments(documentFile);

        index.setDocuments(documents);
        index.indexAll();

        Evaluation evaluator = new Evaluation();

        List<EvaluationEntity> ee = pf.parseQueries(queryFile);
        evaluator.setEe(ee);

        // VSM - BM25
      VectorSpaceModel vsm = new VectorSpaceModel(VectorSpaceModel.VSM_BM25, NO_NLP_METHOD, index);
        for (EvaluationEntity e : ee) {
            List<Document> results = vsm.getRankingScoresVSM(e);
            sortResults(results);
            e.setResults(results);
        }
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
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("Language Model");
        evaluator.printEvaluation();

        //NGRAM
        int ngram = Integer.parseInt(Ngram);

        removeScores(ee);       // a bit crap, but the most efficient way
        NGramModel nGramModel = new NGramModel(NO_NLP_METHOD, lem, index, ngram);
        nGramModel.setN(ngram);
        for (EvaluationEntity e : ee) {
            List<Document> results = nGramModel.getRankingScoresNgram(e);
            sortResults(results);
            e.setResults(results);
        }
        System.out.println("NGRAM");
        evaluator.printEvaluation();

    }
    private void removeScores(List<EvaluationEntity> ee) {
        for(EvaluationEntity e : ee) {
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




