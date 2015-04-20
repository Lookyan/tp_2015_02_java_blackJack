package main;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Context {

    private static final Logger logger = LogManager.getLogger();

    private Map<Class<?>, Object> context = new HashMap<>();

    public void add(Class<?> clazz, Object object) {
        if(context.containsKey(clazz)) {
            logger.fatal("Trying to add existing class to context!");
        } else {
            context.put(clazz, object);
            logger.info("Putting object of '{}' class", clazz.getName());
        }
    }

    public Object get(Class<?> clazz) {
        return context.get(clazz);
    }
}
