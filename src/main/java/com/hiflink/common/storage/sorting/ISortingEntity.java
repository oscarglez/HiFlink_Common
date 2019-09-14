package com.hiflink.common.storage.sorting;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public interface ISortingEntity extends Serializable {
    public static ISortingEntity newPaginationSet(String implementationPath, String entityName, EntitySortingDirection entitySortingDirection, Class entityType) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArg = new Class[3];
        cArg[0] = String.class;
        cArg[1] = EntitySortingDirection.class;
        cArg[2] = Class.class;

        return (ISortingEntity) cls.getConstructor(cArg).newInstance(entityName, entitySortingDirection, entityType);
    }

    public static ISortingEntity newPaginationSet(String implementationPath, String entityName) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArg = new Class[1];
        cArg[0] = String.class;

        return (ISortingEntity) cls.getConstructor(cArg).newInstance(entityName);
    }

    public String getEntityName();

    public Class<?> getEntityType();

    public EntitySortingDirection getEntitySortingDirection();
}
