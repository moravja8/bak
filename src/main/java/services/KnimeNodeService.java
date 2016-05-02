package services;

import com.sun.org.apache.xpath.internal.NodeSet;
import knimeEntities.knimeNodes.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.HashMap;

/**
 * Created by cloudera on 3/29/16.
 */
public class KnimeNodeService {

    private static Logger log = LoggerFactory.getLogger(KnimeNodeService.class);

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private XPathFactory xPathfactory;

    private Transformer transformer;

    protected KnimeNodeService() {
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

    public Document buildDocumentFromXML(File xmlFile){
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

    public void saveNode(KnimeNode node){
        String newNodeSettingsPath =  node.getNodeSettings().getAbsolutePath();

        backupNode(node);

        Result result = new StreamResult(newNodeSettingsPath);
        Source source = new DOMSource(node.getXmlSettings());

        try {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            log.info("Changed DOM document saved to file.");
        } catch (TransformerException e) {
            log.error("Could not transform DOM document to XML file.", e);
            restoreNode(node);
        }
    }

    public void restoreNode(KnimeNode node) {
        if (node.getBackupFile().exists()){
            try {
                ServiceFactory.getFileSystemService().moveFileSilently(node.getBackupFile(), node.getNodeSettings());
                log.info("Node " + node.getNodeRoot().getName() + " was restored.");
            } catch (IOException e) {
                log.error("Node " + node.getNodeRoot().getName() + " could not be restored.", e);
            }
        }else{
            log.debug("There is no backup for node " + node.toString());
        }
    }

    private void backupNode(KnimeNode node){ //File nodeSettings) {
        //create backup folder if not exists
        ServiceFactory.getFileSystemService().createFolder(node.getBackupFolderName());

        //if there is an older backup file, delete it
        if(node.getBackupFile().exists()){
            node.getBackupFile().delete();
        }

        //do the backup, if fails return null
        try {
            ServiceFactory.getFileSystemService().moveFileSilently(node.getNodeSettings(), node.getBackupFile());
            log.info("Node " + node.getNodeRoot().getName() + " was backuped.");
        } catch (IOException e) {
            log.error("Node " + node.getNodeRoot().getName() + " could not be backuped.", e);
        }
    }

    public String getParameterValue(Document doc, String key){
        Node entryNode = this.compileExecuteXpath(doc, "//entry[@key='"+key+"']");
        return entryNode.getAttributes().getNamedItem("value").getNodeValue();
    }

    public void setParameterValue(Document doc, String key, String value){
        Node output = this.compileExecuteXpath(doc, "//entry[@key='"+key+"']");
        output.getAttributes().getNamedItem("value").setNodeValue(value);
    }

    public HashMap<String, String> getAllParameters(Document doc){
        NodeList nodeSet = new NodeSet();
        HashMap<String, String> parameters = new HashMap<String, String>();

        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        try {
            expr = xpath.compile("//entry");
        } catch (XPathExpressionException e) {
            log.error("Could not compile given xpath.", e);
        }

        if (expr != null){
            try {
                nodeSet = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                log.error("Given xpath does not work for this document.", e);
                return parameters;
            }
        }

        for (int i = 0; i < nodeSet.getLength(); i++) {
            Node node = nodeSet.item(i);
            String key = node.getAttributes().getNamedItem("key").getNodeValue();
            String value = node.getAttributes().getNamedItem("value").getNodeValue();
            parameters.put(key, value);
        }
        return parameters;
    }
}
