package model;

import services.ServiceFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 4/1/16.
 */
public class KnimeSqlExecutorNode extends KnimeNode{

    public KnimeSqlExecutorNode(File root) throws IOException {
        super(root);
    }

    public String getSQLCode() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "statement");
    }

    public void setSQLCode(String code) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "statement", code);
        setChanged(true);
    }
}
