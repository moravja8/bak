package performance;

import dbConnectors.H2Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cloudera on 4/11/16.
 */
public class KnimeLogCostsMapper implements CostsMapper{
    private Logger log = LoggerFactory.getLogger(KnimeLogCostsMapper.class);
    private ArrayList<String> inputLog = null;
    private String db;
    private String table;
    private String wf;

    public KnimeLogCostsMapper(ArrayList<String> inputLog, String db, String table, String wf) {
        this.inputLog = inputLog;
        this.db = db;
        this.table = table;
        this.wf = wf;
    }

    public String mapCostsFromLog(){
        ArrayList<String> filteredLog = filterIputLog(inputLog);
        ArrayList<String[]> costMap = createCostMap(filteredLog, db, table, wf);

        StringBuilder costsSb = new StringBuilder();
        for (String[] line: costMap) {
            String sql = "insert into costs values('" + line[0]+"','"+line[1]+"','"+line[2]+"','"+ line[3]+"','"+line[4]+"',"+line[5]+","+line[6]+")";
            H2Connector.getInstance().execute(sql);
            costsSb.append("Duration of execution of node " + line[4] + " was " + line[6] + " secs \n");
        }
        String costs = costsSb.toString();
        log.info("The costs for last workflow execution are: \n" + costs);

        return costs;
    }

    private ArrayList<String[]> createCostMap(ArrayList<String> filteredLog, String db, String table, String wf) {
        ArrayList<String[]> costmap = new ArrayList<String[]>();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);

        for (String line: filteredLog) {

            // remove log header
            line = line.replaceAll("INFO \t KNIME-Worker-\\d LocalNodeExecutionJob\t ", "");
            line = line.replace("End execute", "");

            int paren = line.lastIndexOf('(');
            String duration = line.substring(paren).replaceAll("\\D", "");
            String rest = line.substring(0, paren-1).trim();

            int lastSpace = rest.lastIndexOf(' ');
            String nodeName = rest.substring(0, lastSpace).trim();
            String nodeNbr = String.valueOf(Integer.valueOf(rest.substring(lastSpace).trim().replace(":", "")).intValue());

            costmap.add(new String[]{strDate, db, table, wf, nodeName, nodeNbr, duration});
        }

        return costmap;
    }

    private ArrayList<String> filterIputLog(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<String>();
        for (String line: input) {
            log.trace("Readed line from input log: " + line);
            if(line.contains("INFO")){ // INFO rows
                if(line.contains("KNIME-Worker")){ // rows from BatchExecutor log
                    log.info("Filter accepted line:" + line);
                    output.add(line);
                }
            }
        }
        return output;
    }

}
