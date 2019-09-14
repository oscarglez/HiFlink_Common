package com.hiflink.common.storage;

import com.hiflink.common.storage.sorting.IPaginationSet;
import com.hiflink.common.storage.sorting.SortingEntityCollection;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import scala.Tuple2;

public interface IClientStorage {
    public static IClientStorage newClientForStorage(String implementationPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);
        return (IClientStorage) cls.newInstance();

    }

    public static IClientStorage newClientForStorage(String implementationPath,String configFilePath,String mode, String cloudPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName(implementationPath, true, classLoader);

        Class[] cArg = new Class[3];
        cArg[0] = String.class;
        cArg[1] = String.class;
        cArg[2] = String.class;

        return (IClientStorage) cls.getConstructor(cArg).newInstance(configFilePath,mode,cloudPath);

    }

    public void connect();
    public void appendVirtualStore (String key,String value);
    public void save(String mapName, String key, GenericRecord genericRecord);
    public void save(String mapName, String key, GenericRecord indexRecord, Optional reindexData);
    public Object searchByKey(String indexMap,String key);
    public Collection<GenericRecord> search(String indexMap, Map<String,Object> searchParameters, Optional<SortingEntityCollection> sortingEntityCollection);
    public Tuple2<IPaginationSet, Collection<GenericRecord>> searchWithToken(String indexMap, Map<String,Object> searchParameters, String paginationMap , Schema paginationSchema ,Optional<IPaginationSet> paginationSet, Optional<SortingEntityCollection> sortingEntityCollection);
    public Collection<GenericRecord> getFullCollection(String principal, Optional reindexData);
    public void lockMaps(Object currentInstance, ArrayList<String> lockableList);
    public void  clean(String currentIndex, Optional currentInstance);
    public void unlockMaps(Object currentInstance, ArrayList<String> lockableList);
}
