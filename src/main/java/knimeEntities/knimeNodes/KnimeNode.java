package knimeEntities.knimeNodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by cloudera on 3/29/16.
 */
public class KnimeNode {
    private static Logger log = LoggerFactory.getLogger(KnimeNode.class);

    private File nodeRoot;
    private File nodeSettings = null;
    private Document xmlSettings = null;
    private String label = null;

    /** Příznak udává, jestli se nastavení nodu změnilo od posledního uložení */
    private boolean changed = false;

    public KnimeNode(File root) throws IOException {
        nodeRoot = root;
        loadSettings("settings.xml");

    }

    public KnimeNode(File root, String settings) throws IOException{
        nodeRoot = root;
        loadSettings(settings);
    }

    public KnimeNode(File root, String settings, Document parsedSettings) throws IOException {
        this(root, settings);
        this.xmlSettings = parsedSettings;
    }

    private void loadSettings(final String settings) throws IOException {
        File[] settingFiles = nodeRoot.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(pathname.getName().equals(settings)){
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
            xmlSettings = ServiceFactory.getKnimeNodeService().buildDocumentFromXML(nodeSettings);
        }
        return xmlSettings;
    }

    public void saveXmlSettings(){
        if(xmlSettings != null && changed){
            ServiceFactory.getKnimeNodeService().saveNode(this);
        }
    }

    public String getLabel(){
        if(label == null){
            Node labelNode = ServiceFactory.getKnimeNodeService().compileExecuteXpath(this.getXmlSettings(), "//entry[@key='text']");
            try {
                label = labelNode.getAttributes().getNamedItem("value").getNodeValue();
            } catch (NullPointerException e) {
                log.info("Node "+ nodeRoot.getName() + " does not have a label.");
            }
        }
        return label;
    }

    /**
     * @return složka, kde bude uložena záloha nodu
     */
    public String getBackupFolderName(){
        String workflowBackupFolder = this.getNodeRoot().getParentFile().getAbsolutePath() + File.separator + "backup";
        return workflowBackupFolder + File.separator + this.getNodeRoot().getName();
    }

    /**
     * @return soubor se zálohou nodu
     */
    public File getBackupFile() {
        return new File (getBackupFolderName() + File.separator + this.getNodeSettings().getName());
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public File getNodeRoot() {
        return nodeRoot;
    }

    public File getNodeSettings() {
        return nodeSettings;
    }

    @Override
    public String toString() {
        return nodeRoot.getName();
    }
}
