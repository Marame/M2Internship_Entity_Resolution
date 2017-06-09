import java.io.IOException;
import java.util.Map;

/**
 * Created by romdhane on 23/05/17.
 */
public class Main {

    String query = "news about presidential campaign";
    String doc1 = "news about";
    String doc2 = "news about organic food campaign";
    String doc3 = "news of presidential campaign";
    String doc4 = "news of presidential campaign presidential candidate";
    String doc5 = "news of organic food campaign campaign campaign campaign";
    String[] docs = {doc1, doc2, doc3, doc4, doc5};

    // versions of VSM
    String[] versions = {new String("Binary"), "TF", "TF/IDF", "BM25"};

    public static void main(String... args) throws IOException {

        Main main = new Main();
        main.startTestingVSM();
    }

    public void startTestingVSM() throws IOException {

        System.out.println("query: " + query);
        int i = 1;
        for (String doc : docs) {
            System.out.println("d" + i + ": " + doc);
            i++;
        }

        for (String version : versions) {
            VSMTFxIDFVersion vsm = new VSMTFxIDFVersion(version);
            Map<String, Double> scores = vsm.getRankingScores(query, docs);
            System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

            for (String Key : scores.keySet()) {
                String key = Key.toString();
                String value = scores.get(Key).toString();
                System.out.println(key + " " + value);

            }
        }

    }
}







