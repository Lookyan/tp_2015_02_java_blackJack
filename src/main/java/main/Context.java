package main;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 02.04.15.
 */
public class Context {
    private static Context instance;

    public static Context getInstance() {
        if(instance == null) {
            instance = new Context();
        }
        return instance;
    }

    private Map<Class<?>, Object> context = new HashMap<>();

    public void add(Class<?> clazz, Object object) {
        //TODO: error
        if(context.containsKey(clazz)) {
            //error
        }
        context.put(clazz, object);
    }

    public Object get(Class<?> clazz) {
        return context.get(clazz);
    }
}
