package knimeEntities.Nodes.DatabaseQueryExecutors;

import org.w3c.dom.Node;
import services.ServiceFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 4/1/16.
 */
public class DatabaseReaderKnimeNode extends DatabaseQueryExecutorKnimeNode {


    public DatabaseReaderKnimeNode(File root) throws IOException {
        super(root);
    }


    public String getSQLCode() {
        Node output = ServiceFactory.getXmlXpathService().compileExecuteXpath(super.getXmlSettings(), "//entry[@key='statement']");
        return output.getAttributes().getNamedItem("value").getNodeValue();
    }

    public void setSQLCode(String code) {
        Node output = ServiceFactory.getXmlXpathService().compileExecuteXpath(super.getXmlSettings(), "//entry[@key='statement']");
        output.getAttributes().getNamedItem("value").setNodeValue(code);
        super.saveXmlSettings();
    }
}
