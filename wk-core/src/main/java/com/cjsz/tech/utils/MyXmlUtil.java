package com.cjsz.tech.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiaihua on 16/9/26.
 */
public class MyXmlUtil {

    public static List findSubElements(Element parent,String tag) throws IllegalArgumentException {
        NodeList l = parent.getChildNodes();
        List/**/ elements = new ArrayList(l.getLength());
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equalsIgnoreCase(tag)) {
                elements.add((Element)n);
            } else{
                continue;
            }
        }
        return elements;
    }

    public static List findSrcSubElements(Element parent) throws IllegalArgumentException {
        NodeList l = parent.getChildNodes();
        List/**/ elements = new ArrayList(l.getLength());
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE ) {
                elements.add((Element)n);
            } else if (n.getNodeType() == Node.TEXT_NODE) {
                String text = ((Text)n).getNodeValue();
                if (text.trim().length() > 0) {
                    throw new IllegalArgumentException("non-ws text encountered in " + parent + ": " + text); // NOI18N
                }
            } else if (n.getNodeType() == Node.COMMENT_NODE) {
                // OK, ignore
            } else {
                throw new IllegalArgumentException("unexpected non-element child of " + parent + ": " + n); // NOI18N
            }
        }
        return elements;
    }
}
