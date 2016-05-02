package knimeEntities.knimeNodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 5/1/16.
 */
public class KnimeWriterNode extends KnimeNode {
    private static Logger log = LoggerFactory.getLogger(KnimeNode.class);

    private String outputFile = null;

    public KnimeWriterNode(File root) throws IOException {
        super(root);
    }

    @Nullable
    private String getOutputFile() {
        if(outputFile == null){
            String[] labelParts = super.getLabel().split("_");
            if(labelParts.length == 2){
                return ServiceFactory.getPropertiesLoaderService().getProperty("OutputFolder") +
                        File.separator + labelParts[1];
            }else{
                log.error("Label of export node is not in correct format. " +
                        "Correct format is 'export_nodeName.extension'.", new IllegalArgumentException());
                return null;
            }
        }
        return  outputFile;
    }

    public void refreshSettigs(){
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "OutPutFile", this.getOutputFile());
        super.setChanged(true);
    }
}
