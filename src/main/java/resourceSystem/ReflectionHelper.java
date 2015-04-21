package resourceSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class ReflectionHelper {

    private static final Logger logger = LogManager.getLogger();

    public static Object createInstance(String className) {
        try {
            Object instance = Class.forName(className).newInstance();
            logger.info("Created instance of '{}'", className);
            return instance;
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error(e);
        }
        return null;
    }

    public static void setFieldValue(Object object,
                                     String fieldName,
                                     String value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().equals(String.class)) {
                field.set(object, value);
            } else if (field.getType().equals(int.class)) {
                field.set(object, Integer.decode(value));
            }
            logger.info("Set field '{}' equal to '{}'", field.getName(), field.get(object));

            field.setAccessible(false);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            logger.error(e);
        }
    }
}
