package knimeEntities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton
 * Not threadsafe
 * Created by cloudera on 3/28/16.
 */
public class KnimeWorkflowManager {
    private static Logger log = LoggerFactory.getLogger(KnimeWorkflowManager.class);

    private static KnimeWorkflowManager instance = null;
    private File knimeWorkingFolder = null;
    private File knimeHome = null;
    private ArrayList<KnimeWorkflow> workflows = null;

    protected KnimeWorkflowManager(){
        knimeWorkingFolder = new File(ServiceFactory.getPropertiesLoaderService().getProperty("knimeWorkingFolder"));
        knimeHome = new File(ServiceFactory.getPropertiesLoaderService().getProperty("knimeHome"));
    }

    public static KnimeWorkflowManager getInstance(){
        if(instance == null){
            instance = new KnimeWorkflowManager();
        }
        return instance;
    }

    public File getKnimeHome() {
        return knimeHome;
    }

    public File[] findWorkflows(){
        if(knimeWorkingFolder != null && knimeWorkingFolder.isDirectory()){
            return knimeWorkingFolder.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            });
        }

        return null;
    }

    private ArrayList<KnimeWorkflow> loadWorkflows(){
        workflows = new ArrayList<KnimeWorkflow>();
        File[] roots = findWorkflows();
        for (File root: roots) {
            try {
                workflows.add(new KnimeWorkflow(root));
            } catch (IOException e) {
                log.error("Workflow "+ root.getName() + " could not be loaded.", e);
            }
        }

        return workflows;
    }

    public ArrayList<KnimeWorkflow> getWorkflows() {
        if(workflows != null){
            return workflows;
        }
        return loadWorkflows();
    }

    public File getKnimeWorkingFolder() {
        return knimeWorkingFolder;
    }
}
