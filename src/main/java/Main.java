import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by romdhane on 07/07/17.
 */

public class Main {
    // versions of VSM
    String[] vsm_versions = {new String("Binary"), new String("TF"), new String("TF/IDF"), new String("BM25")};
    //Versions of smoothing in Language Model
    String[] smoothing_versions = {new String("jelinek-mercer"), new String("dirichlet-prior")};
    String[] nlp_methods = {new String("stemming"), new String("lemmatizing")};
    double lambda = 0.4;
    static String FILE_NAME_QUERIES = "/home/romdhane/Documents/stage_inria/queries";
    static String FILE_NAME_DOCS = "/home/romdhane/Documents/stage_inria/documents";
    String document = "news about presidential candidate who participated in the last presidential elections";

    public static void main(String[] args) throws IOException {

        Main main = new Main();
        main.startTesting(FILE_NAME_QUERIES, FILE_NAME_DOCS);
    }


    public void startTesting(String filenameQueries, String filenameDocs) throws FileNotFoundException, IOException {

       BufferedReader br = null;
        FileReader fr = null;
        BufferedReader brd = null;
        FileReader frd = null;
        List<EvaluationEntity> ee = new ArrayList<>();


        try {

            fr = new FileReader(filenameQueries);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(filenameQueries));


            while ((sCurrentLine = br.readLine()) != null && sCurrentLine.length() != 0) {
                EvaluationEntity e = new EvaluationEntity();
                Document query = new Document();
                List<Document> reldocs = new ArrayList<>();
                //String[] splited = sCurrentLine.split("--");
                Pattern pq = Pattern.compile("\"([^\"]*)\"");
                Matcher mq = pq.matcher(sCurrentLine);
                List<String> matches_q = new ArrayList<String>();
                while (mq.find()) {
                    matches_q.add(mq.group(1));
                }
                int val = Integer.parseInt(matches_q.get(0).replace("\\s", ""));
          ;
                query.setId(val);
                query.setName(matches_q.get(1));
                e.setQuery(query);
                e.setQuery(query);
                List<Document> docs = new ArrayList<>();
                frd = new FileReader(filenameDocs);
                brd = new BufferedReader(frd);
                String sCurrentLined;
                brd = new BufferedReader(new FileReader(filenameDocs));
                while ((sCurrentLined = brd.readLine()) != null && sCurrentLined.length() != 0) {
                    if (sCurrentLine.isEmpty()) break;
                    //System.out.println(sCurrentLined);
                    Document doc = new Document();

                    Pattern pd = Pattern.compile("\"([^\"]*)\"");
                    Matcher md = pd.matcher(sCurrentLined);
                    List<String> matches_d = new ArrayList<String>();
                    while (md.find()) {
                        matches_d.add(md.group(1));
                    }

                    int vald = Integer.parseInt(matches_d.get(0).replace("\\s", ""));
                    doc.setId(vald);

                    String[] relevant_ids = matches_q.get(2).split(",");

                    int rid = 0;
                    for (int i = 0; i < relevant_ids.length; i++) {
                        rid = Integer.parseInt(relevant_ids[i].replaceAll("\\s", ""));
                        if (rid == vald) {
                            Document reldoc = new Document();
                            reldoc.setId(rid);
                            reldoc.setName(matches_d.get(1).toString());
                            reldocs.add(reldoc);
                            e.setRelevant_documents(reldocs);

                        }
                    }

                    e.setRelevant_documents(reldocs);
                    doc.setName(matches_d.get(1));
                    docs.add(doc);
                }

                e.setDocuments(docs);
                e.setRelevant_documents(reldocs);
                ee.add(e);
            }


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
            //Our initial Evaluation entities
           /* for (EvaluationEntity e : ee) {
                System.out.println("query: " + e.getQuery().getName());
                int i = 1;
                for (Document doc : e.getDocuments()) {
                    System.out.println("d" + i + ": " + doc.getName());
                    i++;
                }
            }*/
            for (String nlp_method : nlp_methods) {
                System.out.println("$$$$$$$$$$$$ Results for Vector Space Model $$$$$$$$$$$$$");
                System.out.println("Using"+ "\t" +nlp_method + "\t" +"method");

                for (String version : vsm_versions) {

                    System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");
                    Evaluation eval = new Evaluation();
                    eval.final_evaluation(ee, "", version, nlp_method);
                }

                System.out.println("$$$$$$$$$$$$ Results for Language Model $$$$$$$$$$$$$");

                for (String version : smoothing_versions) {

                    System.out.println("***********Results for" + "\t" + version + "\t" + "version*********");

                    Evaluation eval = new Evaluation();
                    eval.final_evaluation(ee, version, "", nlp_method);
                }
            }
        }

            /*for (EvaluationEntity e : ee) {
                System.out.println("**Query**");
                System.out.println(e.getQuery().getName());
                System.out.println("**Relevant documents**");
                for (Document d : e.getRelevant_documents()) {
                    System.out.println(d.getId() + "->" + d.getName());
                }
                System.out.println("**Documents**");
                for (Document d : e.getDocuments()) {
                    System.out.println(d.getId() + "->" + d.getName());
                }
                System.out.println("**Bag of words**");
                for (String s : e.getBagOfWords()) {
                    System.out.println(s);
                }
              }*/
    }

}
