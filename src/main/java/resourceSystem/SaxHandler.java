package resourceSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {

    private static final Logger logger = LogManager.getLogger();

	private static String CLASSNAME = "class"; 	
	private String element = null; 
	private Object object = null;
	
	public void startDocument() throws SAXException {
        logger.info("Start document");
	}
 
	public void endDocument() throws SAXException {
        logger.info("End document ");
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (!qName.equals(CLASSNAME)) {
			element = qName;
		} else {
			String className = attributes.getValue(0);
            logger.info("Class name: " + className);
			object = ReflectionHelper.createInstance(className);
		}	
	}
 
	public void endElement(String uri, String localName, String qName) throws SAXException {
		element = null;
	}
 
	public void characters(char ch[], int start, int length) throws SAXException {
		if (element != null) {
			String value = new String(ch, start, length);
            logger.info(element + " = " + value);
			ReflectionHelper.setFieldValue(object, element, value);
		}
	}
	
	public Object getObject(){
		return object;
	}
}
