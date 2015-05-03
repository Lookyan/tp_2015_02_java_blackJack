package resourceSystem;

import base.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResourceFactory {

    private static final Logger logger = LogManager.getLogger();

    private static ResourceFactory instance;

    private Map<String, Resource> resourceMap = new HashMap<>();

    private ResourceFactory() {}

    public static ResourceFactory getInstance() {
        if (instance == null) {
            instance = new ResourceFactory();
        }
        return instance;
    }

    public void init(String directory) {
        Iterator<String> iterator = VFS.getInstance().getIterator(directory);
        while (iterator.hasNext()) {
            String path = iterator.next();
            resourceMap.put(path, readXML(path));
        }
    }

    public Resource get(String path) {
        Resource resource = resourceMap.get(path);
        if (resource == null) {
            logger.error("No resource on path '{}'", path);
        }
        return resource;
    }

    private Resource readXML(String xmlFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            SaxHandler handler = new SaxHandler();
            saxParser.parse(xmlFile, handler);

            return (Resource) handler.getObject();

        } catch (Exception e) {
            logger.error(e);
        }
        return null;

    }

}
