package knimeEntities.Nodes;

import org.w3c.dom.Document;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by cloudera on 3/29/16.
 */
public class KnimeNode {

    private File nodeRoot;
    private File nodeSettings = null;
    private Document xmlSettings = null;

    public KnimeNode(File root) throws IOException {
        nodeRoot = root;

        File[] settingFiles = root.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(pathname.getName().equals("settings.xml")){
                    return true;
                }
                return false;
            }
        });

        if(settingFiles.length != 1){
            throw new IOException("Settings file not found.");// fail fast
        }else{
            nodeSettings = settingFiles[0];
        }
    }

    public Document getXmlSettings() {
        if(xmlSettings == null){
            xmlSettings = ServiceFactory.getXmlXpathService().buildDocument(nodeSettings);
        }
        return xmlSettings;
    }

    public void saveXmlSettings(){
        if(xmlSettings != null){
            ServiceFactory.getXmlXpathService().saveDocument(nodeSettings, xmlSettings);
        }
    }

    @Override
    public String toString() {
        return nodeRoot.getName();
    }
}
