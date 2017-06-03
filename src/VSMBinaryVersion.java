import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by romdhane on 25/05/17.
 */
public class VectorSpaceModelBinaryVersion  {
    HashMap<String, Integer> dotProduct = new HashMap<String, Integer>();
    ArrayList<Integer> indexVect = new ArrayList<>();
    String doc1 = "Test space model example";
    String doc2 = "Test model";
    String[] docs = {doc1, doc2};


    // computing ranking scores between the query and each one of the documents
     HashMap<String, Integer> getRankingScores(String query, String[] docs) throws IOException {
         VectorSpaceModel vect = new VectorSpaceModel();

        ArrayList<Integer> vectquery = vect.indexVector(query);
         for (Integer i: vectquery) {
             System.out.println(i);

         }

        for (String doc : docs) {
            ArrayList<Integer> vectdoc = vect.indexVector(doc);

            int sum = 0;
            for (int i = 0; i < vectquery.size(); i++) {
                int value =  vectquery.get(i)* vectdoc.get(i);
                sum += value;
            }

            dotProduct.put(doc + "\n", sum);
        }
        return dotProduct;
    }

}
