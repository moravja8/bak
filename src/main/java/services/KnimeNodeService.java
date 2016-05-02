package services;

import model.KnimeNode;
import org.w3c.dom.Document;


/**
 * Created by cloudera on 3/29/16.
 */
public interface KnimeNodeService {
    String getParameterValue(Document doc, String key);

    void setParameterValue(Document doc, String key, String value);

    void restore(KnimeNode node);

    void backup(KnimeNode node);
}
