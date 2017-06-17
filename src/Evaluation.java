import java.util.ArrayList;
import java.util.List;

/**
 * Created by romdhane on 14/06/17.
 */
public class Evaluation {

    private double precision;
    private double recall;
    private double F1;


    public List<Double> evaluateVSM(int n, Document[] relevant_docs, List<Document> results) {

        List<Double> listresults = new ArrayList<>();

        List<Document> firstN = results.subList(0, n);


        int ret_relevant = 0;
        int ret_nonrelevant = 0;
        int notret_relevant = 0;

        for (Document m : firstN) {

            String key = m.getName();
            for (Document d : relevant_docs) {
                if (d.getName().equals(key)) ret_relevant++;
            }
        }
        if (n != results.size()) {
            List<Document> rest = results.subList(n, results.size() - 1);

            for (Document l : rest) {
                String key = l.getName();
                for (Document d : relevant_docs) {
                    if (d.getName().equals(key)) notret_relevant++;
                }
            }


            ret_nonrelevant = n - ret_relevant;

            precision = ret_relevant / ((double) (ret_relevant + ret_nonrelevant));
            recall = ret_relevant / (double) (ret_relevant + notret_relevant);
            F1 = 2 * precision * recall / (precision + recall);
           /* System.out.println("Precision :" + "\t" + precision);
            System.out.println("Recall :" + "\t" + recall);
            System.out.println("F1:" + "\t" + F1);*/
            listresults.add(precision);
            listresults.add(recall);
            listresults.add(F1);
        }
        return listresults;
    }

}




