package knimeEntities;

import knimeEntities.knimeNodes.KnimeHiveConnectorNode;
import knimeEntities.knimeNodes.KnimeSqlExecutorNode;
import knimeEntities.knimeNodes.KnimeNode;
import knimeEntities.knimeNodes.KnimeWriterNode;
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
public class KnimeWorkflow extends KnimeNode{

    private static Logger log = LoggerFactory.getLogger(KnimeWorkflow.class);

    private ArrayList<KnimeNode> nodes = null;

    private KnimeSqlExecutorNode sqlExecutor = null;

    private KnimeHiveConnectorNode hiveConnector = null;

    private ArrayList<KnimeWriterNode> writers = null;

    public KnimeWorkflow(File root) throws IOException {
        super(root, "workflow.knime");
    }

    public String runWorkflow(String db, String table){
        String[] commands = new String[7];
        commands[0] = KnimeWorkflowManager.getInstance().getKnimeHome() + File.separator + "knime";
        commands[1] = "-consoleLog";
        commands[2] = "-nosplash";
        commands[3] = "-application";
        commands[4] = "org.knime.product.KNIME_BATCH_APPLICATION";
        commands[5] = "-workflowDir="+ super.getNodeRoot().getPath();
        commands[6] = "-reset";

        StringBuilder comSb = new StringBuilder();
        for (String command: commands) {
            comSb.append(command + " ");
        }
        ArrayList<String> inputLog = ServiceFactory.getShellCommandsExecutorService().executeCommand(comSb.toString());

        KnimeLogCostsMapper knimeLogCostsMapper = new KnimeLogCostsMapper(inputLog, db, table, getWorkflowName());
        return knimeLogCostsMapper.mapCostsFromLog();
    }

    private File[] findNodes(){
        if(super.getNodeRoot() != null && super.getNodeRoot().isDirectory()){
            return super.getNodeRoot().listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden() && !pathname.getName().equals("backup");
                }
            });
        }
        return new File[0];
    }

    private ArrayList<KnimeNode> loadNodes(){
        nodes = new ArrayList<KnimeNode>();
        writers = new ArrayList<KnimeWriterNode>();

        File[] roots = findNodes();
        for (File root: roots) {
            try {
                KnimeNode node = new KnimeNode(root);

                if ("hive_connector".equals(node.getLabel())) {
                    KnimeHiveConnectorNode hiveConnector = new KnimeHiveConnectorNode(root);
                    node = hiveConnector;
                    this.hiveConnector = hiveConnector;
                }else if("sql_executor".equals(node.getLabel())) {
                    KnimeSqlExecutorNode sqlExecutor = new KnimeSqlExecutorNode(root);
                    node = sqlExecutor;
                    this.sqlExecutor = sqlExecutor;
                }else if(node.getLabel() != null && node.getLabel().contains("export_")) {
                    KnimeWriterNode writerNode = new KnimeWriterNode(root);
                    node = writerNode;
                    this.writers.add(writerNode);
                }
                nodes.add(node);
            } catch (IOException e) {
                log.error("Node " + root.getName() + " could not be loaded - settings not found.", e);
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

    public KnimeSqlExecutorNode getSqlExecutor() throws NullPointerException{
        if (sqlExecutor == null) {
            loadNodes();
        }
        return sqlExecutor;
    }

    public String getWorkflowName(){
        return super.getNodeRoot().getName();
    }

    /**
     * Uloží všechny uzly workflow, jejichž nastavení se změnilo od posledního uložení.
     * Uloží i své vlastní nastavení, pokud se změnilo.
     */
    public void saveWorkflow(){
        //uložení uzlů
        for (KnimeNode node: nodes) {
            if(node.isChanged()){
                node.saveXmlSettings();
            }
        }
        //uložení sebe sama
        if(super.isChanged()){
            this.saveXmlSettings();
        }
    }

    /**
     * @return složka, kde bude uložena záloha nodu
     */
    @Override
    public String getBackupFolderName() {
        String workflowBackupFolder = this.getNodeRoot().getAbsolutePath() + File.separator + "backup";
        return workflowBackupFolder + File.separator + this.getNodeRoot().getName();
    }

    @Override
    public String toString() {
        return getWorkflowName();
    }

    public KnimeHiveConnectorNode getHiveConnector() {
        return hiveConnector;
    }
}
