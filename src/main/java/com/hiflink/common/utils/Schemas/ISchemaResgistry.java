package com.hiflink.common.utils.Schemas;

public interface ISchemaResgistry {
    public static ISchemaResgistry newClientForStorage(String implementationPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);
        return (ISchemaResgistry) cls.newInstance();
    }

    public String getLatest_Schema (String schemaRegistryUrl,ISchemaSolver iSchemaSolver);
}
