import java.io.IOException;
import java.util.*;

/**
 * Created by romdhane on 23/05/17.
**/
public class Main {

    Document query = new Document("query", "news about presidential campaign", 0.0d);
    Document doc1 = new Document("doc1", "news about", 0.0d);
    Document doc2 = new Document("doc2", "news about organic food campaign", 0.0d);
    Document doc3 = new Document("doc2", "news of presidential campaign", 0.0d);
    Document doc4 = new Document("doc2", "news of presidential campaign presidential candidate", 0.0d);
    Document doc5 = new Document("doc2", "news of organic food campaign campaign campaign campaign", 0.0d);
    Document doc6 = new Document("doc2", "news of", 0.0d);
    Document[] docs = {doc1, doc2, doc3, doc4, doc5, doc6};
    Document[] relevant_docs = {doc3, doc4, doc2};

    // versions of VSM
    String[] versions = {new String("Binary"), new String("TF"), new String("TF/IDF"), new String("BM25")};


    public static void main(String... args) throws IOException {

        Main main = new Main();
        main.startTestingVSM();
    }

    public void startTestingVSM() throws IOException {

        System.out.println("query: " + query.getName());
        int i = 1;
        for (Document doc : docs) {
            System.out.println("d" + i + ": " + doc.getName());
            i++;
        }

        for (String version : versions) {
            VSMTFxIDFVersion vsm = new VSMTFxIDFVersion(version);
            List<Document> results = vsm.getRankingScores(query, docs);


            System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

            Collections.sort(results, new Comparator<Document>() {
                @Override
                public int compare(Document o1, Document o2) {
                    final double document1 = o1.getScore();
                    final double document2 = o2.getScore();
                    return document1 < document2 ? 1
                            : document1 > document2 ? -1 : 0;
                }
            });

            for (Document d : results) {
                System.out.println(d.getName());
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++");

            Evaluation eval = new Evaluation();
            eval.evaluateVSM(docs, relevant_docs, results);
        }

    }


}


