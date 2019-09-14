package com.hiflink.common.utils.Schemas;


import com.typesafe.config.Config;

import java.lang.reflect.InvocationTargetException;

public interface ISchemaSolver {
    public static ISchemaSolver newSchemaSolver(String implementationPath, Config schemaConfig,String schemaName) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArgs = new Class[2];
        cArgs[0] = Config.class;
        cArgs[1] = String.class;
        return (ISchemaSolver) cls.getConstructor(cArgs).newInstance(schemaConfig,schemaName);

    }

    public String getSubjectName();

}
