package DAO;

import model.KnimeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;

/**
 * Created by cloudera on 5/2/16.
 */
public interface KnimeNodeDao {
    Document createDOM(File xmlFile);

    Node readParameter(Document doc, String xpathString);

    void update(KnimeNode node);

    HashMap<String, String> readParameters(Document doc);
}
