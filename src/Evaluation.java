import java.util.List;

/**
 * Created by romdhane on 14/06/17.
 */
public class Evaluation {

    private double precision;
    private double recall;
    private double F1;
    //int notret_nonrelevant = 4;

   // List<Document> relevant_docs = Arrays.asList(doc3, doc4, doc2);
    int[] N = {1, 2, 3, 4, 5, 6};
    public void evaluateVSM(Document[] docs, Document[] relevant_docs, List<Document> results){


    //Varying the number of documents for which we calculate precision and recall
            for (int n: N) {
                List<Document> firstN = results.subList(0, n);
                int ret_relevant = 0;
                int ret_nonrelevant = 0;
                int notret_relevant = 0;

                for (Document m : firstN) {

                    String key = m.getName();
                    for (Document d : relevant_docs) {
                        if (d.getName() == key) ret_relevant++;
                    }
                }
                if (n != docs.length) {
                    List<Document> rest = results.subList(n, docs.length);
                    for (Document l : rest) {
                        String key = l.getName();
                        for (Document d : relevant_docs) {
                            if (d.getName() == key) notret_relevant++;
                        }
                    }
                }

                ret_nonrelevant = n - ret_relevant;

                System.out.println("Evaluation for" + "\t" + n + "\t" + "documents");
                /*System.out.println("ret_relevant :"+ "\t"+ret_relevant);
                System.out.println("ret_nonrelevant"+ "\t"+ret_nonrelevant);
                System.out.println("notret_relevant:"+ "\t"+notret_relevant);*/
                precision = ret_relevant / ((double) (ret_relevant + ret_nonrelevant));
                recall = ret_relevant / (double) (ret_relevant + notret_relevant);
                F1 = 2 * precision * recall / (precision + recall);
                System.out.println("Precision :" + "\t" + precision);
                System.out.println("Recall :" + "\t" + recall);
                System.out.println("F1:" + "\t" + F1);
            }
    }
}




