package com.hiflink.common.storage;

public interface IClientStorage {
    public static IClientStorage newClientForStorage(String implementationPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);
        return (IClientStorage) cls.newInstance();

    }

    public void connect();
}
