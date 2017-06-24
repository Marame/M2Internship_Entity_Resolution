

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
    Double[][] precision_matrix = new Double[10][10];
    Double[][] recall_matrix = new Double[10][10];
    Double[][] F1_matrix = new Double[10][10];
    Double[][] ret_rel_matrix = new Double[10][10];
    Double[][] notret_notrel_matrix = new Double[10][10];

    //Double[][] notret_rel_matrix = new Double[10][10];


    int[] N = {1, 2, 3, 4, 5};


    public List<Double> evaluateVSM(int n, Document[] relevant_docs, List<Document> results) {

        List<Double> listresults = new ArrayList<>();

        List<Document> firstN = results.subList(0, n);


        int ret_relevant = 0;
        int ret_nonrelevant = 0;
        int notret_relevant = 0;
        int notret_nonrelevant = 0;

        for (Document m : firstN) {

            String key = m.getName();
            for (Document d : relevant_docs) {
                if (d.getName().equals(key)) ret_relevant++;
            }
        }
        if (n != results.size()) {
            List<Document> rest = results.subList(n, results.size() - 1);

            for (Document l : rest) {
                String key = l.getName();
                for (Document d : relevant_docs) {
                    if (d.getName().equals(key)) notret_relevant++;
                }
            }


            ret_nonrelevant = firstN.size() - ret_relevant;
            notret_nonrelevant = rest.size() - notret_relevant;

            precision = ret_relevant / ((double) (ret_relevant + ret_nonrelevant));
            recall = ret_relevant / (double) (ret_relevant + notret_relevant);
            F1 = 2 * precision * recall / (precision + recall);
           /* System.out.println("Precision :" + "\t" + precision);
            System.out.println("Recall :" + "\t" + recall);
            System.out.println("F1:" + "\t" + F1);*/
            listresults.add(precision);
            listresults.add(recall);
            listresults.add(F1);
            listresults.add((double) ret_relevant);
            listresults.add((double) notret_nonrelevant);

        }
        return listresults;
    }

    public void final_evaluation(EvaluationEntity[] ee, String smoothing_version, String vsm_version) throws IOException {
        List<Document> results = new ArrayList<>();
        int idx_ent = 0;
        for (EvaluationEntity e : ee) {
            VSMTFxIDFVersion vsm = new VSMTFxIDFVersion(vsm_version);
            LanguageModel lm = new LanguageModel(smoothing_version);
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
            System.out.println("ranked results for query n" + "\t" + (idx_ent + 1));
            for (Document d : results) {
                System.out.println(d.getName() + "->" + d.getScore());
            }
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

        System.out.println("+++++++++Evaluation+++++++++");
        for (int i = 0; i < N.length; i++) {
            double sum_precision = 0;
            double sum_recall = 0;
            double sum_F1 = 0;
            double sum_retrel = 0;
            double sum_notretrel = 0;
            double sum_notretnotrel = 0;
            for (int j = 0; j < ee.length; j++) {
                sum_precision += precision_matrix[j][i];
                sum_recall += recall_matrix[j][i];
                sum_F1 += F1_matrix[j][i];
                sum_retrel += ret_rel_matrix[j][i];
                sum_notretnotrel += notret_notrel_matrix[j][i];


            }

            double macro_average_precision = sum_precision / ee.length;
            double macro_average_recall = sum_recall / ee.length;
            double macro_average_F1 = sum_F1 / ee.length;
            double micro_average_precision = sum_precision / ((double) sum_retrel + sum_notretrel);
            double micro_average_recall = sum_recall / ((double) sum_retrel + sum_notretrel);
            double micro_average_F1 = 2 * micro_average_precision * micro_average_recall / ((double) micro_average_precision + micro_average_recall);


            System.out.println("Macro average precision for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_precision);
            System.out.println("Macro average recall for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_recall);
            System.out.println("Macro average F1 for" + "\t" + (i + 1) + "\t" + ":" + "\t" + macro_average_F1);

            System.out.println("Micro average precision for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_precision);
            System.out.println("Micro average recall for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_recall);
            System.out.println("Micro average F1 for" + "\t" + (i + 1) + "\t" + ":" + "\t" + micro_average_F1);
        }
        //compute the mean average precision
        double total = 0;

        for (int i = 0; i < ee.length; i++) {
            double sum_precision_N = 0;

            for (int j = 0; j < N.length; j++) {
                sum_precision_N += precision_matrix[i][j];
                //System.out.println(ret_rel_matrix[j][i]);
            }

            total += (1 / (double) (i + 1)) * sum_precision_N;
        }
        double MAP = (1 / (double) ee.length) * total;
        System.out.println("Mean average precision :" + "\t" + MAP);
    }

}





