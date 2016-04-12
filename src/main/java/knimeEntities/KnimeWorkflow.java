package knimeEntities;

import knimeEntities.Nodes.DatabaseQueryExecutors.DatabaseQueryExecutorKnimeNode;
import knimeEntities.Nodes.DatabaseQueryExecutors.DatabaseReaderKnimeNode;
import knimeEntities.Nodes.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import performance.KnimeLogCostsMapper;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cloudera on 3/29/16.
 */
public class KnimeWorkflow {

    private Logger log = LoggerFactory.getLogger(KnimeWorkflow.class);

    private File workflowRoot = null;

    private ArrayList<KnimeNode> nodes = null;

    private DatabaseQueryExecutorKnimeNode databaseExecutor = null;

    public KnimeWorkflow(File root) {
        workflowRoot = root;
    }

    public String runWorkflow(String db, String table){
        String[] commands = new String[7];
        commands[0] = KnimeWorkflowManager.getInstance().getKnimeHome() + File.separator + "knime";
        commands[1] = "-consoleLog";
        commands[2] = "-nosplash";
        commands[3] = "-application";
        commands[4] = "org.knime.product.KNIME_BATCH_APPLICATION";
        commands[5] = "-workflowDir="+ this.workflowRoot.getPath();
        commands[6] = "-reset";
        //ServiceFactory.getShellCommandsExecutorService().callProcess(commands);
        StringBuilder comSb = new StringBuilder();
        for (String command: commands) {
            comSb.append(command + " ");
        }
        ArrayList<String> inputLog = ServiceFactory.getShellCommandsExecutorService().executeCommand(comSb.toString());

        KnimeLogCostsMapper knimeLogCostsMapper = new KnimeLogCostsMapper(inputLog, db, table, getWorkflowName());
        return knimeLogCostsMapper.mapCostsFromLog();
    }

    private File[] findNodes(){
        if(workflowRoot != null && workflowRoot.isDirectory()){
            return workflowRoot.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            });
        }

        return null;
    }

    private ArrayList<KnimeNode> loadNodes(){
        nodes = new ArrayList<KnimeNode>();
        File[] roots = findNodes();
        for (File root: roots) {
            try {
                KnimeNode node = null;
                if(root.getName().contains("Database Reader ")){
                    DatabaseQueryExecutorKnimeNode databaseExecutor = new DatabaseReaderKnimeNode(root);
                    node = databaseExecutor;
                    this.databaseExecutor = databaseExecutor;
                }else{
                    node = new KnimeNode(root);
                }
                nodes.add(node);
            } catch (IOException e) {
                log.error("Node " + root.getName() + "could not be loaded - settings not found.", e);
            }
        }

        return nodes;
    }

    public ArrayList<KnimeNode> getNodes() {
        if(nodes != null){
            return nodes;
        }
        return loadNodes();
    }

    public DatabaseQueryExecutorKnimeNode getDatabaseExecutor() throws NullPointerException{
        if (databaseExecutor == null) {
            loadNodes();
        }
        return databaseExecutor;
    }

    public String getWorkflowName(){
        return workflowRoot.getName();
    }

    @Override
    public String toString() {
        return getWorkflowName();
    }


}
