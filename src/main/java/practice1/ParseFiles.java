package practice1;


import com.opencsv.CSVReader;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by romdhane on 19/07/17.
 */
public class ParseFiles {

    private FileReader fr = null;
    private FileReader frq = null;
    private FileReader frd = null;
    private List<EvaluationEntity> ee = new ArrayList<>();
    int vocab_size =0;



    public List<EvaluationEntity> parseArgs(String filenameQueries, String filenameDocs) {

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
                query.setName(lineq[1]);

                e.setQuery(query);

                frd = new FileReader(filenameDocs);
                CSVReader brd = new CSVReader(frd);
                String[] lined = null;
                while ((lined = brd.readNext()) != null) {

                    Document doc = new Document();

                    //String[] lined = sCurrentLined.split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                    int documentId = Integer.parseInt(lined[0]);

                    doc.setId(documentId);
                    String[] queryReleventIds = lineq[2].split(" ");

                    // if contained, the current document is relevant
                    if(Arrays.asList(queryReleventIds).contains(lined[0])) {
                        Document reldoc = new Document();
                        reldoc.setId(documentId);
                        reldoc.setName(lined[1]);
                        relevantDocuments.add(reldoc);
                    }

                }
                e.setRelevant_documents(relevantDocuments);
                ee.add(e);
                 //vocab_size += e.getBagOfWords().size();
            }

            //ng.setVocab_size(vocab_size);

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

    public List<Document> retrieveDocuments(String filenameDocs) throws FileNotFoundException, IOException{

        List<Document> docs = new ArrayList<>();
        frd = new FileReader(filenameDocs);
        CSVReader brd = new CSVReader(frd);
        String[] lined = null;
        while ((lined = brd.readNext()) != null) {

            Document doc = new Document();
            int documentId = Integer.parseInt(lined[0]);
            doc.setId(documentId);

            doc.setName(lined[1]);
            docs.add(doc);
        }

        return docs;
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

            query.setName(lineq[1]);
            queries.add(query);
        }

        return queries;
    }

}
