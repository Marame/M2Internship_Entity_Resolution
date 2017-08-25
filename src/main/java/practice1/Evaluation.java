package practice1;

import practice1.entities.Document;
import practice1.entities.EvaluationEntity;
import practice1.models.LSIModel;
import practice1.models.LanguageModel;
import practice1.models.NGramModel;
import practice1.models.VectorSpaceModel;

import java.io.IOException;
import java.util.*;

/**
 * Created by romdhane on 14/06/17.
 */
public class Evaluation {
    private Index index;
    int[] N = {10,20,30,40,50};
    //int[] N = {10, 20, 30, 40, 50, 100, 150, 200, 250, 300, 350, 400, 420};
    private List<EvaluationEntity> ee = new ArrayList<>();
    public final static String VSM_BINARY = "Binary";
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
    private double nCDG;


    public Evaluation(List<EvaluationEntity> ee) {
        this.ee = ee;
    }

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

    public double getnCDG() {
        return nCDG;
    }

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

    public Map<EvaluationEntity, List<Document>> allocateResults(String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l, Index index, int ngram, boolean lsiUsed) throws IOException {
        Map<EvaluationEntity, List<Document>> map = new HashMap<EvaluationEntity, List<Document>>();

        for (EvaluationEntity e : ee) {

            if (ngram > -1) {
                List<Document> results = new ArrayList<>();
                NGramModel Ngram = new NGramModel(nlp_method, l, index, ngram);
                results = Ngram.getRankingScoresNgram(e, nlp_method);
                sortResults(results);
                map.put(e, results);

            } else {
                if ("".equals(smoothing_version)) {
                    if (lsiUsed == false) {
                        List<Document> results = new ArrayList<>();
                        VectorSpaceModel vsm = new VectorSpaceModel(vsm_version, nlp_method, index);
                        results = vsm.getRankingScoresVSM(e);
                        sortResults(results);
                        map.put(e, results);
                    } else {
                        List<Document> results = new ArrayList<>();
                        LSIModel lsi = new LSIModel(vsm_version, index, nlp_method);
                        results = lsi.getRankingScoresLSI(e);
                        sortResults(results);
                        map.put(e, results);
                            /*System.out.println("Query: " + e.getQuery().getContent());
                            for (Document d : results) {
                                System.out.println(d.getId() + "->" + d.getContent() + "->" + d.getScore());
                            }*/
                    }
                } else {
                    List<Document> results = new ArrayList<>();
                    LanguageModel lm = new LanguageModel(smoothing_version, nlp_method, l, index);
                    results = lm.getRankingScoresLM(e, smoothing_version);
                    sortResults(results);
                    map.put(e, results);
                }
            }
        }
        return map;
    }


    public List<Object> retrieve_evaluation_measures(String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l, Index index, int ngram, boolean lsiUsed) throws IOException {

        List<Object> list_eval = new ArrayList<>();

        Map<EvaluationEntity, List<Document>> mapResults = allocateResults(smoothing_version, vsm_version, nlp_method, l, index, ngram, lsiUsed);
        Double[][] precision_matrix = new Double[ee.size()][N.length];
        Double[][] recall_matrix = new Double[ee.size()][N.length];
        Double[][] F1_matrix = new Double[ee.size()][N.length];
        Double[][] ret_rel_matrix = new Double[ee.size()][N.length];
        Double[][] notret_notrel_matrix = new Double[ee.size()][N.length];
        Double[][] notret_rel_matrix = new Double[ee.size()][N.length];

        int idx_ent = 0;
        for (Map.Entry<EvaluationEntity, List<Document>> entry : mapResults.entrySet()) {
            EvaluationEntity e = entry.getKey();
            List<Document> results = entry.getValue();

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
        //System.out.println(Arrays.deepToString(precision_matrix));
        list_eval.add(precision_matrix);
        list_eval.add(recall_matrix);
        list_eval.add(F1_matrix);
        list_eval.add(ret_rel_matrix);
        list_eval.add(notret_notrel_matrix);
        list_eval.add(notret_rel_matrix);
        return list_eval;
    }

    public void final_evaluation(String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l, Index index, int ngram, boolean lsiUsed) throws IOException {
        List<Object> list_eval = retrieve_evaluation_measures(smoothing_version, vsm_version, nlp_method, l, index, ngram, lsiUsed);
        Double[][] precision_matrix = (Double[][]) list_eval.get(0);
        Double[][] recall_matrix = (Double[][]) list_eval.get(1);
        Double[][] F1_matrix = (Double[][]) list_eval.get(2);
        Double[][] ret_rel_matrix = (Double[][]) list_eval.get(3);
        Double[][] notret_notrel_matrix = (Double[][]) list_eval.get(4);
        Double[][] notret_rel_matrix = (Double[][]) list_eval.get(5);
        double sum_precision = 0;
        double sum_recall = 0;
        double sum_F1 = 0;
        double sum_retrel = 0;
        double sum_notretrel = 0;
        double sum_notretnotrel = 0;

        for (int i = 0; i < ee.size(); i++) {
            for (int j = 0; j < N.length; j++) {

                sum_precision += precision_matrix[i][j];
                sum_recall += recall_matrix[i][j];
                sum_F1 += F1_matrix[i][j];
                sum_retrel += ret_rel_matrix[i][j];
                sum_notretnotrel += notret_notrel_matrix[i][j];
                sum_notretrel += notret_rel_matrix[i][j];
                this.macro_average_precision = macro_average_precision;
            }
        }
        //compute precisions@K
        double sum_precision_k = 0;
        for (int j = 0; j < (N.length - 1); j++) {
            for (int k = 0; k < ee.size(); k++) {

                sum_precision_k += F1_matrix[k][j];
                //if (k == ee.size() - 1)
                //  System.out.println("F1 at" + N[j] + "=" + sum_precision_k);
            }
        }


        double macro_average_precision = sum_precision / N.length;
        this.macro_average_precision = macro_average_precision;
        double macro_average_recall = sum_recall / N.length;
        this.macro_average_recall = macro_average_recall;
        double macro_average_F1 = sum_F1 / N.length;
        this.macro_average_F1 = macro_average_F1;
        double micro_average_precision = sum_retrel / (sum_retrel + sum_notretrel);
        this.micro_average_precision = micro_average_precision;
        double micro_average_recall = sum_retrel / (sum_retrel + sum_notretnotrel);
        this.micro_average_recall = micro_average_recall;
        double micro_average_F1 = (2 * micro_average_precision * micro_average_recall) / (micro_average_precision + micro_average_recall);
        this.micro_average_F1 = micro_average_F1;

    }

    public List<Double> getEvaluationMeasures(String smoothing_version, String vsm_version, String nlp_method, Lemmatizer l, Index index, int ngram, boolean lsiUsed) throws IOException {

        List<Double> evaluationMeasures = new ArrayList<>();
        //compute the mean average precision
        double sum_precision_N = 0;
        double total = 0;
        int i = 0;
        Map<EvaluationEntity, List<Document>> mapresults = allocateResults(smoothing_version, vsm_version, nlp_method, l, index, ngram, lsiUsed);
        List<Object> list_eval = retrieve_evaluation_measures(smoothing_version, vsm_version, nlp_method, l, index, ngram, lsiUsed);
        Double[][] precision_matrix = (Double[][]) list_eval.get(0);
        Double[][] recall_matrix = (Double[][]) list_eval.get(1);
        Double[][] F1_matrix = (Double[][]) list_eval.get(2);
        Double[][] ret_rel_matrix = (Double[][]) list_eval.get(3);
        Double[][] notret_notrel_matrix = (Double[][]) list_eval.get(4);
        Double[][] notret_rel_matrix = (Double[][]) list_eval.get(5);

        for (Map.Entry<EvaluationEntity, List<Document>> entry : mapresults.entrySet()) {
            EvaluationEntity e = entry.getKey();
            List<Document> results = entry.getValue();
            sortResults(results);
            for (int j = 0; j < N.length; j++) {
                double numrel = numRel(results, e.getRelevant_documents(), N[N.length - 1]);

                if (j == 0)
                    sum_precision_N += (1 / (numrel + 1)) * (precision_matrix[i][j] * recall_matrix[i][0]);
                else {
                    sum_precision_N += (1 / (numrel + 1)) * (precision_matrix[i][j] * (recall_matrix[i][j - 1]));
                }
            }
            total += sum_precision_N;
            i++;
        }
        double MAP = total / (double) mapresults.entrySet().size();
        evaluationMeasures.add(MAP);

        //compute nCDG
        double sum = 0;
        for (Map.Entry<EvaluationEntity, List<Document>> entry : mapresults.entrySet()) {
            EvaluationEntity e = entry.getKey();
            List<Document> results = entry.getValue();
            sortResults(results);
            for (int n : N) {
                sum += CDGAtN(e, results,  n) / IdealCDGAtN(results, N[N.length -1]);
            }
        }

        double nCDG = sum / (double) mapresults.entrySet().size();
       evaluationMeasures.add(nCDG);
       return evaluationMeasures;

    }

}












