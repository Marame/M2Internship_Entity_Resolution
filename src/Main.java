import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romdhane on 23/05/17.
 */
public class Main {

    String query = "news about presidential campaign";
    String doc1 = "news about organic food campaign";
    String doc2 = "news of presidential campaign ";
    String doc3 = "news of presidential campaign presidential candidate";
    String[] docs = {doc1, doc2, doc3};

    // versions of VSM
    String[] versions = {new String("VSM Binary"), "VSM with TF", "Improved VSM with IDF", "Solving problem Presidential vs About", "VSM with TFxIDF"};

    public static void main(String... args) throws IOException {

        Main main = new Main();
        main.startTestingVSM();
    }

    public void startTestingVSM() throws IOException {

        for (String version : versions) {
            VSMTFxIDFVersion vsm = new VSMTFxIDFVersion(version);
            Map<String, Float> scores = vsm.getRankingScores(query, docs);
            System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

            for (String Key : scores.keySet()) {
                String key = Key.toString();
                String value = scores.get(Key).toString();
                System.out.println(key + " " + value);

            }
        }

    }
}







