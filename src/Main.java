import java.io.IOException;
import java.util.HashMap;

/**
 * Created by romdhane on 23/05/17.
 */
public class Main {
    static String query = "Test  vector space model Test";
    static String doc1 = "Test space model example";
    static String doc2 = "Test model";
    static String[] doc = {doc1, doc2};
    static HashMap<String, Float> rankingScores;

    public static void main(String... args) throws IOException {

        VectorSpaceModel vect = new VectorSpaceModel();

       rankingScores = vect.getRankingScores(query, doc);

        for (String name: rankingScores.keySet()){

            String key =name.toString();
            String value = rankingScores.get(name).toString();
            System.out.println(key + " " + value);


        }

    }

}