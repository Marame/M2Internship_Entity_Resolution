package practice1;

import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.models.LanguageModel;
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

    private List<Document> documents;

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


    Double[][] precision_matrix = new Double[100][100];
    Double[][] recall_matrix = new Double[100][100];
    Double[][] F1_matrix = new Double[100][100];
    Double[][] ret_rel_matrix = new Double[100][100];
    Double[][] notret_notrel_matrix = new Double[100][100];
    Double[][] notret_rel_matrix = new Double[100][100];

    //Double[][] notret_rel_matrix = new Double[10][10];

    int[] N = {1, 2, 3, 4, 5, 6};

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

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Double> evaluateVSM(int n, List<Document> relevant_docs, List<Document> results) {

        List<Double> listresults = new ArrayList<>();

        List<Document> firstN = results.subList(0, n);

        int ret_relevant = 0;
        int ret_nonrelevant = 0;
        int notret_relevant = 0;
        int notret_nonrelevant = 0;

        for (Document m : firstN) {
            String key = m.getContent();
            for (Document d : relevant_docs) {
                if (d.getContent().equals(key)) ret_relevant++;
            }
        }

        List<Document> rest = results.subList(n, results.size());
        for (Document l : rest) {
            String key = l.getContent();
            for (Document d : relevant_docs) {
                if (d.getContent().equals(key)) notret_relevant++;
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
        listresults.add((double) notret_relevant);

        return listresults;
    }


    public void final_evaluation(List<EvaluationEntity> ee, String smoothing_version, String vsm_version, String nlp_method, Index index) throws IOException {

        List<Document> results = new ArrayList<>();
        int idx_ent = 0;
        for (EvaluationEntity e : ee) {
            if (smoothing_version.equals("")) {
                VectorSpaceModel vsm = new VectorSpaceModel(vsm_version, nlp_method, index);
                results = vsm.getRankingScoresVSM(e);
            } else if (!smoothing_version.equals("")) {
                LanguageModel lm = new LanguageModel(smoothing_version, nlp_method, index);
                results = lm.getRankingScoresLM(e, smoothing_version);
            }


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
       /*if(smoothing_version.equals("dirichlet-prior")){
            System.out.println("ranked results for query n" + "\t" + (idx_ent + 1));
            for (Document d : results) {
                System.out.println(d.getContent() + "->" + d.getScore());
            }}*/
            int idx_N = 0;
            for (Integer n : N) {
                List<Double> resultsAtn = evaluateVSM(n, e.getRelevant_documents(), results);
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
                sum_F1 += F1_matrix[j][i];
                sum_retrel += ret_rel_matrix[j][i];
                sum_notretnotrel += notret_notrel_matrix[j][i];
                sum_notretrel += notret_rel_matrix[j][i];
            }

            double macro_average_precision = sum_precision / ee.size();
            this.macro_average_precision = macro_average_precision;
            double macro_average_recall = sum_recall / ee.size();
            this.macro_average_recall = macro_average_recall;
            double macro_average_F1 = sum_F1 / (double) ee.size();
            this.macro_average_F1 = macro_average_F1;
            double micro_average_precision = sum_retrel / (double) (sum_retrel + sum_notretrel);
            this.micro_average_precision = micro_average_precision;
            double micro_average_recall = sum_retrel / (double) (sum_retrel + sum_notretnotrel);
            this.micro_average_recall = micro_average_recall;
            double micro_average_F1 = 2 * micro_average_precision * micro_average_recall / (micro_average_precision + micro_average_recall);
            this.micro_average_F1 = micro_average_F1;

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
                if (j == 0)
                    sum_precision_N += precision_matrix[i][j];
                else {
                    sum_precision_N += precision_matrix[i][j] * (recall_matrix[i][j - 1]);
                }
            }

            total += (1 / (double) (i + 1)) * sum_precision_N;
        }
        double MAP = (1 / (double) ee.size()) * total;
        this.MAP = MAP;

        //System.out.println("Mean average precision :" + "\t" + MAP);
    }

}






