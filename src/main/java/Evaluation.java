import Entities.Document;
import Entities.EvaluationEntity;
import Models.LanguageModel;
import Models.VectorSpaceModel;
import Utilities.Lemmatizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by romdhane on 14/06/17.
 */

public class Evaluation {

    private double precision;
    private double recall;
    private double F1;
    private double MAP;
    private double micro_av_precision;
    Double[][] precision_matrix = new Double[10][10];
    Double[][] recall_matrix = new Double[10][10];
    Double[][] F1_matrix = new Double[10][10];
    Double[][] ret_rel_matrix = new Double[10][10];
    Double[][] notret_notrel_matrix = new Double[10][10];

    //Double[][] notret_rel_matrix = new Double[10][10];


    int[] N = {1, 2, 3, 4, 5, 6};


    public List<Double> evaluateVSM(int n, List<Document> relevant_docs, List<Document> results) {

        List<Double> listresults = new ArrayList<>();

        List<Document> firstN = results.subList(0, n);

        int ret_relevant = 0;
        int ret_nonrelevant = 0;
        int notret_relevant = 0;
        int notret_nonrelevant = 0;

        for (Document m : firstN) {
            String key = m.getName();
            for (Document d : relevant_docs) {
                if (d.equals(key)) ret_relevant++;
            }
        }

        List<Document> rest = results.subList(n, results.size());
        for (Document l : rest) {
            String key = l.getName();
            for (Document d : relevant_docs) {
                if (d.equals(key)) notret_relevant++;
            }
        }

        ret_nonrelevant = firstN.size() - ret_relevant;
        notret_nonrelevant = rest.size() - notret_relevant;

        precision = ret_relevant / ((double) (ret_relevant + ret_nonrelevant));
        recall = ret_relevant / (double) (ret_relevant + notret_relevant);
        F1 = 2 * precision * recall / (precision + recall);

        listresults.add(precision);
        listresults.add(recall);
        listresults.add(F1);
        listresults.add((double) ret_relevant);
        listresults.add((double) notret_nonrelevant);

        return listresults;
    }

    public double getMAP() {
        return MAP;
    }

    public void final_evaluation(List<EvaluationEntity> ee, String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l) throws IOException {
        List<Document> results = new ArrayList<>();
        int idx_ent = 0;
        for (EvaluationEntity e : ee) {
            VectorSpaceModel vsm = new  VectorSpaceModel(vsm_version, nlp_method, l);
            LanguageModel lm = new LanguageModel(smoothing_version, nlp_method, l);
            if (smoothing_version.equals(""))
                results = vsm.getRankingScoresVSM(e);
            else if (!smoothing_version.equals("")) results = lm.getRankingScoresLM(e, smoothing_version);


            Collections.sort(results, new Comparator<Document>() {
                @Override
                public int compare(Document o1, Document o2) {
                    final double document1 = o1.getScore();
                    final double document2 = o2.getScore();
                    return document1 < document2 ? 1
                            : document1 > document2 ? -1 : 0;
                }
            });

            // ranked results
            /*System.out.println("ranked results for query n" + "\t" + (idx_ent + 1));
            for (Document d : results) {
                System.out.println(d.getName() + "->" + d.getScore());
            }*/
            for (Integer n : N) {
                int idx_N = n - 1;
                List<Double> resultsAtn = evaluateVSM(n, e.getRelevant_documents(), results);
                precision_matrix[idx_ent][idx_N] = resultsAtn.get(0);
                recall_matrix[idx_ent][idx_N] = resultsAtn.get(1);
                F1_matrix[idx_ent][idx_N] = resultsAtn.get(2);
                ret_rel_matrix[idx_ent][idx_N] = resultsAtn.get(3);
                notret_notrel_matrix[idx_ent][idx_N] = resultsAtn.get(4);
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
                sum_F1 += F1_matrix[j][i];
                sum_retrel += ret_rel_matrix[j][i];
                sum_notretnotrel += notret_notrel_matrix[j][i];
            }

            double macro_average_precision = sum_precision / ee.size();
            double macro_average_recall = sum_recall / ee.size();
            double macro_average_F1 = sum_F1 / ee.size();
            double micro_average_precision = sum_retrel / ((double) sum_retrel + sum_notretrel);
            double micro_average_recall = sum_retrel / ((double) sum_retrel + sum_notretnotrel);
            double micro_average_F1 = 2 * micro_average_precision * micro_average_recall / (micro_average_precision + micro_average_recall);

          /*  if (i == 2) {
               System.out.println("Macro average precision for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_precision);
                System.out.println("Macro average recall for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_recall);
                System.out.println("Macro average F1 for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_F1);

                System.out.println("Micro average precision for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_precision);
                System.out.println("Micro average recall for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_recall);
                System.out.println("Micro average F1 for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_F1);

            }*/
        }
        //compute the mean average precision
        double total = 0;

        for (int i = 0; i < ee.size(); i++) {
            double sum_precision_N = 0;

            for (int j = 0; j < N.length; j++) {
                sum_precision_N += precision_matrix[i][j];
            }

            total += (1 / (double) (i + 1)) * sum_precision_N;
        }
        double MAP = (1 / (double) ee.size()) * total;
        this.MAP = MAP;

        //System.out.println("Mean average precision :" + "\t" + MAP);
    }

}






