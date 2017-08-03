package practice1;


import com.opencsv.CSVReader;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

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
    private FileReader frd = null;
    List<EvaluationEntity> ee = new ArrayList<>();
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

                List<Document> docs = new ArrayList<>();
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
                    if(Arrays.asList(queryReleventIds).contains(documentId)) {
                        Document reldoc = new Document();
                        reldoc.setId(documentId);
                        reldoc.setName(lined[1]);
                        relevantDocuments.add(reldoc);
                    }
                    
                    doc.setName(lined[1]);
                    docs.add(doc);
                }

                e.setDocuments(docs);
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
}
