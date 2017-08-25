package practice1;


import com.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.omg.CORBA.SystemException;
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

    public List<Document> parseDocuments(String filenameDocs) throws IOException, SystemException {

        List<Document> docs = new ArrayList<>();
        frd = new FileReader(filenameDocs);

        Iterable<CSVRecord> records = null;
        try {
            CSVFormat csvFormat = CSVFormat.EXCEL.withHeader().withDelimiter(',');
            records = csvFormat.parse(frd);
        } catch (IOException e) {
            System.out.println("cannot read csv file");
        }

        for (CSVRecord record : records) {
            Document doc = new Document();
            doc.setId(Integer.parseInt(record.get(1)));
            System.out.println(record.get(1));
            doc.setContent(record.get(0));
            docs.add(doc);
        }
      this.documents = docs;
        return docs;
    }

    public List<EvaluationEntity> parseQueries(String filename)throws IOException {

        try {

            fr = new FileReader(filename);
            Iterable<CSVRecord> records = null;
            try {
                CSVFormat csvFormat = CSVFormat.EXCEL.withHeader().withDelimiter(',');
                records = csvFormat.parse(fr);
            } catch (IOException e) {
                System.out.println("cannot read csv file");
            }

            for (CSVRecord record : records) {
                EvaluationEntity e = new EvaluationEntity();
                List<Document> relevantDocuments = new ArrayList<>();
                int val = Integer.parseInt(record.get(0));

                Document query = new Document();
                query.setId(val);
                query.setContent(record.get(1));
                e.setQuery(query);

                String[] queryReleventIds = record.get(2).split(" ");

                // if contained, the current document is relevant
                for (String relId : queryReleventIds) {
                    int valId = Integer.parseInt(relId);

                    for (Document Doc : documents) {

                        if (Doc.getId() == valId) {
                            Document reldoc = new Document();
                            reldoc.setId(Doc.getId());
                            reldoc.setContent(Doc.getContent());
                            relevantDocuments.add(reldoc);

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
