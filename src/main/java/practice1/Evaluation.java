package practice1;

import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.models.LanguageModel;
import practice1.models.NGramModel;
import practice1.models.VectorSpaceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by romdhane on 14/06/17.
 */
public class Evaluation {
    private Index index;
    public final static String VSM_TF = "TF";
    public final static String VSM_TFIDF = "TF/IDF";
    public final static String VSM_BM25 = "BM25";
    public final static String JELINEK_SMOOTHING = "jelinek-mercer";
    public final static String DIRICHLET_SMOOTHING = "dirichlet-prior";


    private double precision;
    private double recall;
    private double F1;
    private double MAP;
    private double micro_average_precision;
    private double macro_average_precision;
    private double micro_average_recall;
    private double macro_average_recall;
    private double micro_average_F1;
    private double macro_average_F1;


    Double[][] precision_matrix = new Double[1000][100];
    Double[][] recall_matrix = new Double[1000][100];
    Double[][] F1_matrix = new Double[1000][100];
    Double[][] ret_rel_matrix = new Double[1000][100];
    Double[][] notret_notrel_matrix = new Double[1000][100];
    Double[][] notret_rel_matrix = new Double[1000][100];

    //Double[][] notret_rel_matrix = new Double[10][10];

    //int[] N = {500, 800, 900, 1000, 1400};
    int[] N = {5, 10, 15, 20, 25, 27};

    public double getMAP() {
        return MAP;
    }

    public double getMicro_average_precision() {
        return micro_average_precision;
    }

    public double getMacro_average_precision() {
        return macro_average_precision;
    }

    public double getMicro_average_recall() {
        return micro_average_recall;
    }

    public double getMacro_average_recall() {
        return macro_average_recall;
    }

    public double getMicro_average_F1() {
        return micro_average_F1;
    }

    public double getMacro_average_F1() {
        return macro_average_F1;
    }

    public int reward(List<Document> results, List<Document> relevant_docs){
        int reward = 0;
        for (Document hit: results) {
            if (relevant_docs.get(0).getId() == hit.getId()) reward += 2;
            //if (relevant_docs.get(1).getId() == hit.getId()) reward += 1;
        }
       return reward;

    }

    public List<Double> evaluateVSM(List<Document> results, List<Document> relevant_docs, int n) {

        List<Double> listresults = new ArrayList<>();

        List<Document> firstN = results.subList(0, n);

        int ret_relevant = 0;
        int ret_nonrelevant = 0;
        int notret_relevant = 0;
        int notret_nonrelevant = 0;


        //counting relevant document that have been retrieved
        for (Document m : firstN) {

            Integer key = m.getId();
            for (Document d : relevant_docs) {
                Integer keyd = d.getId();
                if (keyd.equals(key)) ;
                ret_relevant++;

            }
        }

        //counting the relevant documents that have not been retrieved
        List<Document> rest = results.subList(n, results.size());
        for (Document l : rest) {
            int key = l.getId();
            for (Document d : relevant_docs) {
                if (d.getId() == key) notret_relevant++;
            }
        }

        ret_nonrelevant = firstN.size() - ret_relevant;

        notret_nonrelevant = rest.size() - notret_relevant;

        precision = ret_relevant  / ((double) (ret_relevant + ret_nonrelevant));
        recall = ret_relevant  / ((double) (ret_relevant  + notret_relevant));
        F1 = (2 * precision * recall) / (precision + recall);

        listresults.add(precision);
        listresults.add(recall);
        listresults.add(F1);
        listresults.add((double) ret_relevant );
        listresults.add((double) notret_nonrelevant);
        listresults.add((double) notret_relevant );

        return listresults;
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

    public void final_evaluation(List<EvaluationEntity> ee, String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l, Index index, int ngram) throws IOException {

        int idx_ent = 0;
        for (EvaluationEntity e : ee) {
            List<Document> results = new ArrayList<>();

            if (ngram > -1) {
                NGramModel Ngram = new NGramModel(nlp_method, l, index, ngram);
                results = Ngram.getRankingScoresNgram(e, nlp_method);


            } else {
                if ("".equals(smoothing_version)) {
                    VectorSpaceModel vsm = new VectorSpaceModel(vsm_version, nlp_method, index);
                    results = vsm.getRankingScoresVSM(e);

                } else {
                    LanguageModel lm = new LanguageModel(smoothing_version, nlp_method, l, index);
                    results = lm.getRankingScoresLM(e, smoothing_version);

                    /*if (DIRICHLET_SMOOTHING.equals(smoothing_version)) {
                        System.out.println("Query: " + e.getQuery().getContent());
                        for (Document d : results) {
                            System.out.println(d.getId() + "->" + d.getContent() + "->" + d.getScore());
                        }
                    }*/

                }
            }
          sortResults(results);


            int idx_N = 0;
            for (Integer n : N) {
                List<Double> resultsAtn = evaluateVSM(results, e.getRelevant_documents(), n);

                precision_matrix[idx_ent][idx_N] = resultsAtn.get(0);
                recall_matrix[idx_ent][idx_N] = resultsAtn.get(1);
                F1_matrix[idx_ent][idx_N] = resultsAtn.get(2);
                ret_rel_matrix[idx_ent][idx_N] = resultsAtn.get(3);
                notret_notrel_matrix[idx_ent][idx_N] = resultsAtn.get(4);
                notret_rel_matrix[idx_ent][idx_N] = resultsAtn.get(5);
                idx_N++;
            }
            idx_ent++;

        }

        //System.out.println("+++++++++Evaluation+++++++++");
        for (int i = 0; i < N.length; i++) {
            double sum_precision = 0;
            double sum_recall = 0;
            double sum_F1 = 0;
            double sum_retrel = 0;
            double sum_notretrel = 0;
            double sum_notretnotrel = 0;
            for (int j = 0; j < ee.size(); j++) {
                sum_precision += precision_matrix[j][i];
                sum_recall += recall_matrix[j][i];
                //if(!Double.isNaN(recall_matrix[j][i]))
                sum_F1 += F1_matrix[j][i];
                sum_retrel += ret_rel_matrix[j][i];
                sum_notretnotrel += notret_notrel_matrix[j][i];
                sum_notretrel += notret_rel_matrix[j][i];
            }


            double macro_average_precision = sum_precision / ee.size();
            this.macro_average_precision = macro_average_precision;
            double macro_average_recall = sum_recall / ee.size();
            this.macro_average_recall = macro_average_recall;
            double macro_average_F1 = sum_F1 / ee.size();
            this.macro_average_F1 = macro_average_F1;
            double micro_average_precision = sum_retrel / (sum_retrel + sum_notretrel);
            this.micro_average_precision = micro_average_precision;
            double micro_average_recall = sum_retrel / (sum_retrel + sum_notretnotrel);
            this.micro_average_recall = micro_average_recall;
            double micro_average_F1 = (2 * micro_average_precision * micro_average_recall) / (micro_average_precision + micro_average_recall);
            this.micro_average_F1 = micro_average_F1;

        }

        //compute the mean average precision
        double total = 0;

        for (int i = 0; i < ee.size(); i++) {
            double sum_precision_N = 0;

            for (int j = 0; j < N.length; j++) {
                if (j == 0)
                    sum_precision_N += precision_matrix[i][j] * recall_matrix[i][0];
                else {
                    sum_precision_N += precision_matrix[i][j] * (recall_matrix[i][j - 1]);
                }
            }

            total += sum_precision_N;
        }
        double MAP = total/ (double) ee.size();
        this.MAP = MAP;

    }

}






