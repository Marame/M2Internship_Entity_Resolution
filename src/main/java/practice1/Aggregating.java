package practice1;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by romdhane on 30/07/17.
 */
public class Aggregating {
    private FileReader frq = null;
    private FileReader frd = null;

    public void stripDuplicatesFromFile(String filename) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Set<String> lines = new HashSet<String>(10000);
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String unique : lines) {
            writer.write(unique);
            writer.newLine();
        }
        writer.close();
    }
    public void aggregate(String filenameQueries, String filenameDocs ,String newfilenameQueries) throws  IOException {

        try {
            stripDuplicatesFromFile(filenameQueries);
            frq = new FileReader(filenameQueries);
            CSVReader brq = new CSVReader(frq);
            String[] lineq = null;
            System.out.println("training");
            int iq =0;

            PrintWriter pw = new PrintWriter(new File(newfilenameQueries));
            while ((lineq = brq.readNext()) != null) {

                frd = new FileReader(filenameDocs);
                CSVReader brd = new CSVReader(frd);

                String[] lined = null;
                String relids = "";
                int id =0;
                while ((lined = brd.readNext()) != null ) {

                    if (lined[3].indexOf(lineq[2]) != -1) {

                        relids += lined[1] + " ";
                        System.out.println(relids);
                    }
                    id ++;
                }

                  if (relids !=""){

                    StringBuilder sb = new StringBuilder();

                    sb.append("\""+lineq[0]+"\"");
                    sb.append(',');
                    sb.append("\""+lineq[2].replaceAll("[-+.^:,_&/?]","")+"\"");
                    sb.append(',');
                    sb.append("\""+relids+"\"");
                    sb.append('\n');
                    pw.write(sb.toString());}

                iq++;
            }

              pw.close();
        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (frq != null)
                    frq.close();
                if(frd != null)
                    frd.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }
}