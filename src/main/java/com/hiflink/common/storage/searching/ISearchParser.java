package com.hiflink.common.storage.searching;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface ISearchParser {
    public static ISearchParser newSearchParser(String implementationPath, String indexKey) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArg = new Class[1];
        cArg[0] = String.class;

        return (ISearchParser) cls.getConstructor(cArg).newInstance(indexKey);
    }

    enum ConcatExpresion {
        AND, OR
    }


    public Map<String, Object> parse(Map<String, Object> mapSearch, Map<String, String> indexTypes);


}