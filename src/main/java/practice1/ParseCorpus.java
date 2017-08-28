package practice1;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import practice1.entities.Document;
import practice1.entities.EvaluationEntity;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romdhane on 19/07/17.
 */
public class ParseCorpus {
    private List<EvaluationEntity> ee = new ArrayList<>();

    public List<Document> parseDocuments(String filenameDocs) {
        List<Document> docs = new ArrayList<>();
        Iterable<CSVRecord> records = null;
        FileReader frd = null;
        try {

            CSVFormat csvFormat = CSVFormat.EXCEL.withDelimiter(',');
            frd = new FileReader(filenameDocs);


            records = csvFormat.parse(frd);

            for (CSVRecord record : records) {
                Document doc = new Document();
                doc.setId(Integer.parseInt(record.get(1)));
                doc.setContent(record.get(0));
                docs.add(doc);
            }

        } catch (IOException e) {
            System.out.println("cannot read csv file");
        } finally {
            try {
                if (frd != null)
                    frd.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        return docs;
    }

    public List<EvaluationEntity> parseQueries(String filename, List<Document> documents) throws IOException {

        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            Iterable<CSVRecord> records = null;

            CSVFormat csvFormat = CSVFormat.EXCEL.withHeader().withDelimiter(',');
            records = csvFormat.parse(fr);


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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return ee;
    }
}