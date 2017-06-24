import java.io.IOException;
import java.util.Arrays;
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


    // versions of VSM
    String[] vsm_versions = {new String("Binary"), new String("TF"), new String("TF/IDF"), new String("BM25")};
    //Versions of smoothing in Language Model
    String[] smoothing_versions = {new String("jelinek-mercer"), new String("dirichlet-prior")};
    double lambda = 0.4;

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

        System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");

        for (String version : vsm_versions) {

            System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

            Evaluation eval = new Evaluation();
            eval.final_evaluation(ee, "", version);
        }

        System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

        for (String version : smoothing_versions) {

            System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

            Evaluation eval = new Evaluation();
            eval.final_evaluation(ee, version, "");
        }
    }
}





