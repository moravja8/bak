package services;

import model.KnimeWorkflowNode;
import java.util.ArrayList;

/**
 * Singleton
 * Not threadsafe
 * Created by cloudera on 3/28/16.
 */
public interface KnimeWorkflowService {

    ArrayList<KnimeWorkflowNode> getWorkflows();

    String runWorkflow(KnimeWorkflowNode workflowNode, String db, String table);

    void loadNodes(KnimeWorkflowNode knimeWorkflowNode);
}
