package org.json.object;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//todo make singleton
public class ObjectInstantiator {

    //todo open this up with a register method
    private static final Map<Type, Constructor<?>> CONSTRUCTOR_CACHE = new HashMap<>();
    static {
        try {
            CONSTRUCTOR_CACHE.put(List.class, ArrayList.class.getConstructor());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object newInstance(Type type) {
        try{
            if(CONSTRUCTOR_CACHE.containsKey(type)){
                return CONSTRUCTOR_CACHE.get(type).newInstance();
            }else{
                //todo object must be marked public in order to instantiate is there a
                // way to get the default constructor and not have a public class?
                Constructor<?> constructor = ((Class<?>)type).getConstructor();
                constructor.setAccessible(true);
                CONSTRUCTOR_CACHE.put(type, constructor);
                return constructor.newInstance();
            }
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object newArray(Type type, int length) {
        return Array.newInstance((Class<?>) type,length);
    }

}
