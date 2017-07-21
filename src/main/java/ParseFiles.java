import Entities.Document;
import Entities.EvaluationEntity;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by romdhane on 19/07/17.
 */
public class ParseFiles {

    private FileReader fr = null;
    private FileReader frd = null;
    List<EvaluationEntity> ee = new ArrayList<>();
    private static final Pattern SPACE = Pattern.compile(" ");


    public List<EvaluationEntity> parseArgs(String filenameQueries, String filenameDocs) {

        try {

            fr = new FileReader(filenameQueries);
            CSVReader br = new CSVReader(fr);
            String[] lineq = null;
            while ((lineq = br.readNext()) != null) {

                EvaluationEntity e = new EvaluationEntity();
                Document query = new Document();
                List<Document> reldocs = new ArrayList<>();
                int val = Integer.parseInt(lineq[0]);
                query.setId(val);
                query.setName(lineq[1]);
                e.setQuery(query);
                e.setQuery(query);
                List<Document> docs = new ArrayList<>();
                frd = new FileReader(filenameDocs);
                CSVReader brd = new CSVReader(frd);
                String[] lined = null;
                while ((lined = brd.readNext()) != null) {

                    Document doc = new Document();

                    //String[] lined = sCurrentLined.split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                    int vald = Integer.parseInt(lined[0]);

                    doc.setId(vald);
                   // String[] relevant_ids = lineq[2].toString().split("\\t");
                    String[] relevant_ids = SPACE.split(lineq[2]);

                    int rid = 0;
                    for (int i = 0; i < relevant_ids.length; i++) {

                        rid = Integer.parseInt(relevant_ids[i].replace("\"", ""));

                        if (rid == vald) {
                            Document reldoc = new Document();
                            reldoc.setId(rid);
                            reldoc.setName(lined[1]);
                            reldocs.add(reldoc);

                        }
                    }
                    doc.setName(lined[1]);
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

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return ee;
    }
}
