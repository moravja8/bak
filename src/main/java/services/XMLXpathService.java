package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;

/**
 * Created by cloudera on 3/29/16.
 */
public class XMLXpathService {

    private Logger log = LoggerFactory.getLogger(XMLXpathService.class);

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private XPathFactory xPathfactory;

    private Transformer transformer;

    protected XMLXpathService() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            log.info("Could not set features to DocumentBuilderFactory. Resulting in document builder might not work well.", e);
        }
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("Could not parse configuration.", e);
        }
        xPathfactory = XPathFactory.newInstance();
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            log.error("Could not create transformer.", e);
        }
    }

    public Document buildDocument(File xmlFile){
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException e) {
            log.error("Unable to build xml dom.", e);
        } catch (IOException e) {
            log.error("Unable to build document.", e);
        }
        return doc;
    }

    public Node compileExecuteXpath(Document doc, String xpathString){
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        try {
            expr = xpath.compile(xpathString);
        } catch (XPathExpressionException e) {
            log.error("Could not compile given xpath.", e);
        }

        if (expr != null){
            try {
                return (Node) expr.evaluate(doc, XPathConstants.NODE);
            } catch (XPathExpressionException e) {
                log.error("Given xpath does not work for this document.", e);
            }
        }
        return null;
    }

    public void saveDocument(File nodeSettings, Document xmlSettings) {
        String newNodeSettingsPath = backupSettings(nodeSettings);
        Result result = new StreamResult(newNodeSettingsPath);
        Source source = new DOMSource(xmlSettings);

        try {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            log.info("Changed DOM document saved to file.");
        } catch (TransformerException e) {
            log.error("Could not transform DOM document to XML file.", e);
            restoreSettings(nodeSettings, newNodeSettingsPath);
        }
    }

    private void restoreSettings(File nodeSettings, String newNodeSettingsPath) {
        File originalSettings = new File(newNodeSettingsPath);
        File backedUpSettings = ServiceFactory.getFileSystemService().getBackupFile(nodeSettings);

        try {
            ServiceFactory.getFileSystemService().moveFileSilently(backedUpSettings, originalSettings);
            log.info("File " + originalSettings.getName() + " was restored.");
        } catch (IOException e) {
            log.error("File " + originalSettings.getName() + " could not be restored.", e);
        }
    }

    private String backupSettings(File nodeSettings) {
        //create backup folder if not exists
        String backupFolderName = ServiceFactory.getFileSystemService().getBackupFolderName(nodeSettings);
        File backupRoot = ServiceFactory.getFileSystemService().createFolder(backupFolderName);

        //backup settings
        File backupFile = ServiceFactory.getFileSystemService().getBackupFile(nodeSettings);
        //if there is an older backup file, delete it
        if(backupFile.exists()){
            backupFile.delete();
        }
        //do the backup, if fails return null
        String newNodeSettings = nodeSettings.getAbsolutePath();
        try {
            ServiceFactory.getFileSystemService().moveFileSilently(nodeSettings, backupFile);
            log.info("File " + nodeSettings.getName() + " was backuped.");
        } catch (IOException e) {
            log.error("File " + nodeSettings.getName() + " could not be backuped.", e);
            return null;
        }
        return newNodeSettings;
    }
}
