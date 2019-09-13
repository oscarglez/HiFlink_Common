package com.hiflink.common.utils.Schemas;

import com.hiflink.common.storage.IClientStorage;

public interface ISchemaSolver {
    public static ISchemaSolver newSchemaSolver(String implementationPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);
        return (ISchemaSolver) cls.newInstance();

    }

    public String getSubjectName();

}
