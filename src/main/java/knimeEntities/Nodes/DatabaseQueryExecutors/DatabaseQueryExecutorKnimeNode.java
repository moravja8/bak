package knimeEntities.Nodes.DatabaseQueryExecutors;

import knimeEntities.Nodes.KnimeNode;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 4/1/16.
 */
public abstract class DatabaseQueryExecutorKnimeNode extends KnimeNode{

    public DatabaseQueryExecutorKnimeNode(File root) throws IOException {
        super(root);
    }

    public abstract String getSQLCode();

    public abstract void setSQLCode(String code);
}
