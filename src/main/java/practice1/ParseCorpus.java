package practice1;


import com.opencsv.CSVReader;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romdhane on 19/07/17.
 */
public class ParseCorpus {

    private FileReader fr = null;
    private FileReader frq = null;
    private FileReader frd = null;
    String nlpUsed ;
    private List<EvaluationEntity> ee = new ArrayList<>();
    List<Document> documents;
    int vocab_size =0;

    public void setNlpUsed(String nlpUsed) {
        this.nlpUsed = nlpUsed;
    }

    public List<Document> retrieveDocuments(String filenameDocs, String nlpUsed) throws FileNotFoundException, IOException{

        List<Document> docs = new ArrayList<>();
        frd = new FileReader(filenameDocs);
        CSVReader brd = new CSVReader(frd);
        String[] lined = null;
        int line_id =0;
        while ((lined = brd.readNext()) != null&&(line_id<200)) {
            line_id ++;

            Document doc = new Document();
            if(nlpUsed.equals("false")){
                doc.setId(Integer.parseInt(lined[1]));
                doc.setContent(lined[3].replaceAll("[-+.^:,_&/?]","")+"\"");}
            else {
                doc.setId(Integer.parseInt(lined[0]));
                doc.setContent(lined[1]);
            }
            docs.add(doc);
        }
      this.documents = docs;
        return docs;
    }

    public List<EvaluationEntity> parseArgs(String filenameQueries, String filenameDocs, String nlpUsed)throws IOException {

        try {

            fr = new FileReader(filenameQueries);
            CSVReader br = new CSVReader(fr);
            String[] lineq = null;

            while ((lineq = br.readNext()) != null) {
                EvaluationEntity e = new EvaluationEntity();

                List<Document> relevantDocuments = new ArrayList<>();
                int val = Integer.parseInt(lineq[0]);

                Document query = new Document();
                query.setId(val);
                query.setContent(lineq[1]);

                e.setQuery(query);


                    //String[] lined = sCurrentLined.split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                    if(!nlpUsed.equals("true")) {

                        String[] queryReleventIds = lineq[2].split(" ");

                        // if contained, the current document is relevant
                        for (String relId : queryReleventIds) {
                            int valId = Integer.parseInt(relId.replaceAll("\"", ""));

                            for (Document Doc : documents) {

                                if (Doc.getId() == valId) {
                                    Document reldoc = new Document();
                                    reldoc.setId(valId);
                                    reldoc.setContent(Doc.getContent());
                                    relevantDocuments.add(reldoc);

                                }
                            }
                        }
                    }
                    else {

                        String[] queryReleventIds = lineq[2].split(" ");


                        // if contained, the current document is relevant
                        for (String relId : queryReleventIds) {
                            int valId = Integer.parseInt(relId.replaceAll("\"", ""));

                            for (Document Doc : documents) {

                                if (Doc.getId() == valId) {
                                    Document reldoc = new Document();
                                    reldoc.setId(valId);
                                    reldoc.setContent(Doc.getContent());
                                    relevantDocuments.add(reldoc);

                                }
                            }
                        }

                    }

                e.setRelevant_documents(relevantDocuments);
                ee.add(e);

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            try {
                if (fr != null)
                    fr.close();

                if(frd != null)
                    frd.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return ee;
    }




    public List<Document> retrieveQueries(String filenameQueries) throws FileNotFoundException, IOException{

        List<Document> queries = new ArrayList<>();
        frq = new FileReader(filenameQueries);
        CSVReader brd = new CSVReader(frq);
        String[] lineq = null;
        while ((lineq = brd.readNext()) != null) {

            Document query = new Document();
            int queryId = Integer.parseInt(lineq[0]);
            query.setId(queryId);

            query.setContent(lineq[1]);
            queries.add(query);
        }

        return queries;
    }

}
