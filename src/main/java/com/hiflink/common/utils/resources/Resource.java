package com.hiflink.common.utils.resources;


import com.hiflink.common.utils.exceptions.NotImplementedYetException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public interface Resource extends Serializable {


    public Collection<String> getResources(Map<ResourceParams, Object> configMap) throws MalformedURLException, Exception;

    public String getContentFromResources(Map<ResourceParams, Object> configMap) throws IOException, Exception;

    public Properties getPropertiesFromResources(Map<ResourceParams, Object> configMap) throws IOException, Exception;

    public Reader getReaderFromResources(Map<ResourceParams, Object> configMap) throws IOException, Exception;

    void putContentFromResources(String file, Map<ResourceParams, Object> paramsMap) throws Exception;

    void putContentFromResources(File input, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException, NotImplementedYetException;

    Collection<String> getResourcesFromDirectory(Map<ResourceParams, Object> paramsMap) throws Exception;








}
