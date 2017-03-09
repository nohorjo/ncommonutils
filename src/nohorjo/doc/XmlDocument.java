package nohorjo.doc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class handles actions on {@link Document}s
 * 
 * @author muhammed
 *
 */
public class XmlDocument {
	private Document doc;

	public XmlDocument(Document doc) {
		this.doc = doc;
	}

	/**
	 * Gets the value of an element's attribute
	 * 
	 * @param xpathToElement
	 *            the xpath String to the element
	 * @param attribute
	 *            the attribute to get
	 * @return the attribute's value
	 * @throws XPathExpressionException
	 *             if there was a problem evaluating the xpath or the attribute
	 */
	public String getAttribute(String xpathToElement, String attribute) throws XPathExpressionException {
		return getNode(xpathToElement).getAttributes().getNamedItem(attribute).getNodeValue();
	}

	/**
	 * Gets the child {@link Element}s of a {@link Node}
	 * 
	 * @param xpathToElement
	 *            the xpath String to the parent {@link Node}
	 * @return the children of the parent {@link Node} that are {@link Element}s
	 * @throws XPathExpressionException
	 *             if there was a problem with expression
	 */
	public List<Node> getChildElements(String xpathToElement) throws XPathExpressionException {
		List<Node> children = new ArrayList<>();
		NodeList nodeList = getNode(xpathToElement).getChildNodes();
		Node child = nodeList.item(0);
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				children.add(child);
			}
			child = child.getNextSibling();
		}
		return children;
	}

	/**
	 * Gets the {@link Node} defined by the xpath
	 * 
	 * @param xpath
	 *            the xpath expression
	 * @return {@link Node}
	 * @throws XPathExpressionException
	 *             if there was a problem with expression
	 */
	private Node getNode(String xpath) throws XPathExpressionException {
		return (Node) getXmlEntity(xpath, XPathConstants.NODE);
	}

	/**
	 * Reads the config xml
	 * 
	 * @param xpath
	 *            xpath expression String
	 * @param returnType
	 *            {@link XPathConstants}
	 * @return the evaluation of the xpath
	 * @throws XPathExpressionException
	 *             if there was a problem with expression or the the returnType
	 *             is not of {@link XPathConstants}
	 */
	private Object getXmlEntity(String xpath, QName returnType) throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().compile(xpath).evaluate(doc, returnType);
	}
}
