import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by romdhane on 23/05/17.
 **/
public class Main {

    Document query1 = new Document("query1", "news about presidential campaign", 0.0d);
    Document doc11 = new Document("doc11", "news about", 0.0d);
    Document doc21 = new Document("doc21", "news about organic food campaign", 0.0d);
    Document doc31 = new Document("doc31", "news of presidential campaign", 0.0d);
    Document doc41 = new Document("doc41", "news of presidential campaign presidential candidate", 0.0d);
    Document doc51 = new Document("doc51", "news of organic food campaign campaign campaign campaign", 0.0d);
    Document doc61 = new Document("doc61", "news of", 0.0d);
    List<String> bow1 = Arrays.asList("news", "about", "presidential", "campaign", "food");

    Document query2 = new Document("query2", "Test Vector space model", 0.0d);
    Document doc12 = new Document("doc12", "Test Vector", 0.0d);
    Document doc22 = new Document("doc22", "Test Vector space", 0.0d);
    Document doc32 = new Document("doc32", "Vector space model", 0.0d);
    Document doc42 = new Document("doc42", "Test Test Test Vector Model", 0.0d);
    Document doc52 = new Document("doc52", "test Vector space model", 0.0d);
    Document doc62 = new Document("doc62", "Test space", 0.0d);
    List<String> bow2 = Arrays.asList("Test", "Vector", "space", "model");

    Document[] docs1 = {doc11, doc21, doc31, doc41, doc51, doc61};
    Document[] relevant_docs1 = {doc31, doc41, doc21};

    Document[] docs2 = {doc12, doc22, doc32, doc42, doc52, doc62};
    Document[] relevant_docs2 = {doc42, doc32, doc22};

    EvaluationEntity ee1 = new EvaluationEntity(query1, docs1, relevant_docs1, bow1);
    EvaluationEntity ee2 = new EvaluationEntity(query2, docs2, relevant_docs2, bow2);
    EvaluationEntity[] ee = {ee1, ee2};
    Double[][] precision_matrix = new Double[10][10];
    Double[][] recall_matrix = new Double[10][10];
    Double[][] F1_matrix = new Double[10][10];

    int[] N = {1, 2, 3, 4, 5};


    // versions of VSM
    String[] versions = {new String("Binary"), new String("TF"), new String("TF/IDF"), new String("BM25")};


    public static void main(String... args) throws IOException {

        Main main = new Main();
        main.startTestingVSM();
    }

    public void startTestingVSM() throws IOException {

        //Our initial Evaluation entities
        for (EvaluationEntity e : ee) {
            System.out.println("query: " + e.getQuery().getName());
            int i = 1;
            for (Document doc : e.getDocuments()) {
                System.out.println("d" + i + ": " + doc.getName());
                i++;
            }
        }

        for (String version : versions) {

                System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");


               // System.out.println("++++++++++++++++++" + " Evaluation for " + "\t" + version + "+++++++++++++++++++++");
            int idx_ent = 0;
            for (EvaluationEntity e : ee) {
                VSMTFxIDFVersion vsm = new VSMTFxIDFVersion(version);
                List<Document> results = vsm.getRankingScores(e);


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
                System.out.println("ranked results for query n" +"\t"+(idx_ent+1));
                for (Document d : results) {
                    System.out.println(d.getName() + "->" + d.getScore());
                }
                for (Integer n : N) {
                    int idx_N = n - 1;
                    Evaluation eval = new Evaluation();
                    List<Double> resultsAtn = eval.evaluateVSM(n, e.getRelevant_documents(), results);
                    precision_matrix[idx_ent][idx_N] = resultsAtn.get(0);
                    recall_matrix[idx_ent][idx_N] = resultsAtn.get(1);
                    F1_matrix[idx_ent][idx_N] = resultsAtn.get(2);

                }
                idx_ent++;
            }

            System.out.println("+++++++++Evaluation+++++++++");
            for (int i = 0; i < N.length; i++) {
                double sum_precision = 0;
                double sum_recall = 0;
                double sum_F1 = 0;
                for (int j = 0; j < ee.length; j++) {
                    sum_precision += precision_matrix[j][i];
                    sum_recall += recall_matrix[j][i];
                    sum_F1 += F1_matrix[j][i];

                }
                double average_precision = sum_precision / ee.length;
                double average_recall = sum_recall / ee.length;
                double average_F1 = sum_F1 / ee.length;

                System.out.println("Average precision for" + "\t" + (i + 1) + "\t" + ":" + "\t" + average_precision);
                System.out.println("Average recall for" + "\t" + (i + 1) + "\t" + ":" + "\t" + average_recall);
                System.out.println("Average F1 for" + "\t" + (i + 1) + "\t" + ":" + "\t" + average_F1);
            }

        }


    }
}




