package services;

import knimeEntities.KnimeWorkflowManager;
import knimeEntities.knimeNodes.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

/**
 * Created by moravja8 on 4/11/16.
 *
 * Settings of Knime log4j logger needs to be updated for purposes of this application. Costs evaluator gets information
 * about time of execution of each node of Knime workflow from Knime's console log, which however, in default settings,
 * does not log all information to stdout in case of batch application.
 * This problem can be fixed by adding a 'batchexec' node reference to 'root' element of the settings.
 * Path to log4j settings file is defined statically, but there is a reason for that. There is a premise, that if the name
 * of the file changes, it would be because of a new version of log4j and in that case the structure of xml file needs to be
 * checked also, whether it has changed.
 */
public class KnimeLogSettingsService {
    private File knimeLogSettingsFile = null;
    private static Logger log = LoggerFactory.getLogger(KnimeLogSettingsService.class);
    private Document parsedSettings = null;

    /**
     * Constructor locates and loads the log4j setting file. If the file is not found, it fires NullPointerException.
     * @throws NullPointerException
     */
    protected KnimeLogSettingsService() throws NullPointerException{
        StringBuilder logFilePath = new StringBuilder();
        logFilePath.append(KnimeWorkflowManager.getInstance().getKnimeWorkingFolder().getAbsolutePath());
        logFilePath.append(File.separator);
        logFilePath.append(".metadata");
        logFilePath.append(File.separator);
        logFilePath.append("knime");
        logFilePath.append(File.separator);
        logFilePath.append("log4j3.xml");

        knimeLogSettingsFile = new File(logFilePath.toString());

        if(!knimeLogSettingsFile.isFile()){
            log.error("Log4j settings file for Knime was not found. Cost estimation will not work.");
            throw new NullPointerException();
        }
    }

    /**
     * Adds node appender-ref[@ref='batchexec'] to root node, backups old settings and saves new settings.
     */
    public void addBatchExecutorLoggerToRoot() throws IOException {
        parsedSettings = ServiceFactory.getKnimeNodeService().buildDocumentFromXML(knimeLogSettingsFile);

        Node root = ServiceFactory.getKnimeNodeService().compileExecuteXpath(parsedSettings, "//root");

        Node batchExecutorLogger = ServiceFactory.getKnimeNodeService().compileExecuteXpath(parsedSettings, "//root/appender-ref[@ref='batchexec']");

        if(batchExecutorLogger == null) { // batchExecutorLogger is not set yet to root
            batchExecutorLogger = parsedSettings.createElement("appender-ref");
            Attr attr = parsedSettings.createAttribute("ref");
            attr.setValue("batchexec");
            batchExecutorLogger.getAttributes().setNamedItem(attr);
            root.appendChild(batchExecutorLogger);

            ServiceFactory.getKnimeNodeService().saveNode(new KnimeNode(knimeLogSettingsFile.getParentFile(), "log4j3.xml", parsedSettings));
        }
    }

}
