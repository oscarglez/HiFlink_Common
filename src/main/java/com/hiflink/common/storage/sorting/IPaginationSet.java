package com.hiflink.common.storage.sorting;

import java.lang.reflect.InvocationTargetException;

public interface IPaginationSet {
    public static IPaginationSet newPaginationSet(String implementationPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        return (IPaginationSet) cls.newInstance();
    }

    public static IPaginationSet newPaginationSet(String implementationPath, Integer paginSize) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArg = new Class[1];
        cArg[0] = String.class;

        return (IPaginationSet) cls.getConstructor(cArg).newInstance(paginSize);
    }


    public void nextPage();

    public void previousPage();

    public void firstPage();

    public void lastPage();

    public String getToken();

    public Integer getCurrentPage();

    public Integer getRecordsCount();

    public Integer getPaginSize();

    public Integer getPaginCount();

}
