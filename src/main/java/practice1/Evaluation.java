package practice1;

import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by romdhane on 14/06/17.
 */
public class Evaluation {

    //int[] N = {10, 20,50};
    int[] N = {10, 20,50,100};
   // private List<EvaluationEntity> ee = new ArrayList<>();


    public double reward(Document result, List<Document> relevant_docs) {
        Double reward = 0.0;
        if (relevant_docs.get(0).getId() == result.getId()) reward = 4.0;
        if (relevant_docs.size() > 2) {
            if (relevant_docs.get(1).getId() == result.getId()) {
                reward = 3.0;
            } else if (relevant_docs.get(2).getId() == result.getId()) reward = 2.0;
            else
                reward = 1.0;
        }
        return reward;

    }

    //compute CDG at N
    public double CDGAtN(EvaluationEntity e, List<Document> results, int n) {
        double cumul = 0;
        for (Document doc : results.subList(0, n)) {
            double score = reward(doc, e.getRelevant_documents());

            cumul += (Math.pow(2, score) - 1) / Math.log(n);
        }
        return cumul;

    }

    public double IdealCDGAtN(List<Document> results, int n) {
        double cumul = 0;
        for (Document doc : results.subList(0, n)) {
            cumul += (Math.pow(2, 4) - 1) / Math.log(n);
        }
        return cumul;

    }


    public double numRel(List<Document> results, List<Document> relevant_docs, int n) {
        Double reward = 0.0;
        for (Document m : results.subList(0, n)) {

            Integer key = m.getId();
            for (Document d : relevant_docs) {
                Integer keyd = d.getId();
                if (keyd.equals(key)) ;
                reward++;
            }
        }
        return reward;

    }


    public List<Double> evaluate(List<Document> results, List<Document> relevant_docs, int n) {

        List<Double> listresults = new ArrayList<>();
        double precision = 0;
        double recall = 0;
        double F1 = 0;



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
                if (keyd.equals(key)) {
                    ret_relevant++;
                }

            }
        }
        //counting the relevant documents that have not been retrieved
        List<Document> rest = results.subList(n, results.size());
        for (Document l : rest) {
            Integer key = l.getId();
            for (Document d : relevant_docs) {
                Integer keyd = d.getId();
                if (keyd.equals(key)) {
                    notret_relevant++;
                }
            }
        }

        ret_nonrelevant = firstN.size() - ret_relevant;

        notret_nonrelevant = rest.size() - notret_relevant;

        precision = ret_relevant / (double) (ret_relevant + ret_nonrelevant);
        recall = ret_relevant / (double) (ret_relevant + notret_relevant);
        F1 = (2 * precision * recall) / (precision + recall);

        listresults.add(precision);

        listresults.add(recall);
        listresults.add(F1);
        listresults.add((double) ret_relevant);
        listresults.add((double) notret_nonrelevant);
        listresults.add((double) notret_relevant);

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


    public List<Double[][]> retrieve_evaluation_measures(List<EvaluationEntity> ee) throws IOException {

        List<Double[][]> list_eval = new ArrayList<>();

        Double[][] precision_matrix = new Double[ee.size()][N.length];
        Double[][] recall_matrix = new Double[ee.size()][N.length];
        Double[][] F1_matrix = new Double[ee.size()][N.length];
        Double[][] ret_rel_matrix = new Double[ee.size()][N.length];
        Double[][] notret_notrel_matrix = new Double[ee.size()][N.length];
        Double[][] notret_rel_matrix = new Double[ee.size()][N.length];

        int idx_ent = 0;
        for (EvaluationEntity e: ee) {

            List<Document> results = e.getResults();

            int idx_N = 0;
            for (Integer n : N) {
                List<Double> resultsAtn = evaluate(results, e.getRelevant_documents(), n);

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
       //System.out.println(Arrays.deepToString(precision_matrix));
        list_eval.add(precision_matrix);
        list_eval.add(recall_matrix);
        list_eval.add(F1_matrix);
        list_eval.add(ret_rel_matrix);
        list_eval.add(notret_notrel_matrix);
        list_eval.add(notret_rel_matrix);
        return list_eval;
    }

    public List<Double> getEvaluationMeasures(List<EvaluationEntity> ee) throws IOException{
        List<Double> evaluationMeasures = new ArrayList<>();
        List<Double[][]> list_eval = retrieve_evaluation_measures(ee);
        Double[][] precision_matrix =  list_eval.get(0);
        Double[][] recall_matrix =  list_eval.get(1);
        Double[][] F1_matrix =  list_eval.get(2);
        Double[][] ret_rel_matrix =  list_eval.get(3);
        Double[][] notret_notrel_matrix =  list_eval.get(4);
        Double[][] notret_rel_matrix =  list_eval.get(5);


        for (int j = 0; j < N.length; j++) {
            double sum_precision_k = 0;
            double sum_recall_k = 0;
            double sum_F1_k = 0;
            double sum_retrel_k = 0;
            double sum_notretrel_k = 0;
            double sum_notretnotrel_k = 0;

            //Macro measures
            for (int k = 0; k < ee.size(); k++) {
                //if(!Double.isNaN(F1_matrix[k][j]))
                sum_precision_k += precision_matrix[k][j];
                if (k == ee.size()-1)
                 System.out.println(" Macro Average Precision at" +"\t"+ N[j] +"\t"+ "=" +"\t"+sum_precision_k);

                sum_recall_k += recall_matrix[k][j];
                if (k == ee.size()-1)
                    System.out.println(" Macro Average Recall at" +"\t"+ N[j] +"\t"+ "=" +"\t"+ sum_recall_k);

                if(!Double.isNaN(F1_matrix[k][j]))
                {
                    sum_F1_k+= F1_matrix[k][j];
                    if (k == ee.size()-1)
                        System.out.println(" Macro Average F1 at" +"\t"+ N[j] +"\t"+ "=" +"\t"+ sum_F1_k);
                }

               //Micro measures

                sum_retrel_k+= ret_rel_matrix[k][j];
                sum_notretrel_k+= notret_rel_matrix[k][j];
                sum_notretnotrel_k+= notret_notrel_matrix[k][j];
                 double micro_average_precision =0;
                double micro_average_recall =0;
                double micro_average_F1 =0;


                if (k == ee.size()-1)
                    micro_average_precision = sum_retrel_k / (sum_retrel_k + sum_notretrel_k);
                    micro_average_recall = sum_retrel_k / (sum_retrel_k + sum_notretnotrel_k);
                    micro_average_F1 = (2*micro_average_precision*micro_average_recall)/(micro_average_precision + micro_average_recall);

                System.out.println(" Micro Average Precision at" +"\t"+N[j] +"\t"+"="+"\t"+ micro_average_precision);
                System.out.println(" Micro Average Recall at" +"\t"+ N[j] +"\t"+ "=" +"\t"+ micro_average_recall);
                System.out.println(" Micro Average F1 at" +"\t"+ N[j] +"\t"+ "=" +"\t"+micro_average_F1);

            }
        }




        //compute the mean average precision
        double total = 0;
        int i=0;
        for (EvaluationEntity e: ee) {
            double sum_precision_N = 0;

            for (int j = 0; j < N.length; j++) {
                double numrel = numRel(e.getResults(), e.getRelevant_documents(), N[N.length - 1]);

                if (j == 0)
                    sum_precision_N += (1 / (numrel + 1)) * (precision_matrix[i][j] * recall_matrix[i][0]);
                else {
                    sum_precision_N += (1 / (numrel + 1)) * (precision_matrix[i][j] * (recall_matrix[i][j - 1]));
                }
            }
            total += sum_precision_N;
            i++;
        }
        double MAP = total / (double) ee.size();
        evaluationMeasures.add(MAP);

        //compute nCDG
        double sum = 0;
        for (EvaluationEntity e: ee) {
            for (int n : N) {
                sum += CDGAtN(e, e.getResults(), n) / IdealCDGAtN(e.getResults(), N[N.length - 1]);
            }
        }

        double nCDG = sum / (double) ee.size();
        evaluationMeasures.add(nCDG);

       /* evaluationMeasures.add(micro_average_precision);
        evaluationMeasures.add(micro_average_recall);
        evaluationMeasures.add(micro_average_F1);
        evaluationMeasures.add(macro_average_precision);
        evaluationMeasures.add(macro_average_recall);
        evaluationMeasures.add(macro_average_F1);*/
        return evaluationMeasures;

    }

    public void printEvaluation(List<EvaluationEntity> ee) throws IOException {
        List<Double> measures = getEvaluationMeasures(ee);
        StringBuilder sb = new StringBuilder();
        sb.append("MAP"+"\t"+measures.get(0)+"\n");
        sb.append("nCDG"+"\t"+measures.get(1)+"\n");
        /*sb.append("Micro Average Precision"+"\t"+measures.get(2)+"\n");
        sb.append("Micro Average Recall"+"\t"+measures.get(3)+"\n");
        sb.append("Micro Average F1"+"\t"+measures.get(4)+"\n");
        sb.append("Macro Average Precision"+"\t"+measures.get(5)+"\n");
        sb.append("Macro Average Recall"+"\t"+measures.get(6)+"\n");
        sb.append("Macro Average F1"+"\t"+measures.get(7)+"\n");*/
        System.out.println(sb.toString());

    }
}












