package com.hiflink.common.utils.resources.impl;


import com.hiflink.common.utils.exceptions.NotImplementedYetException;
import com.hiflink.common.utils.resources.Resource;
import com.hiflink.common.utils.resources.ResourceParams;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class CloudResource implements Resource, Serializable {


    public CloudResource() {
    }

    @Deprecated
    public static Collection<String> getResources( final Pattern pattern ) throws Exception {
        throw new NotImplementedYetException();
    }

    @Deprecated
    public static String getContentFromResources( Class cls, final String element ) throws IOException {
        String retval = null;

        RestTemplate plantilla = new RestTemplate();
        retval = plantilla.getForObject(element, String.class);
        return retval;
    }
    @Deprecated
    public static Properties getPropertiesFromResources( Class cls, final String element ) throws IOException {
        Properties retval = null;

        RestTemplate plantilla = new RestTemplate();
        String configToString = plantilla.getForObject(element, String.class);
        if (configToString != null && !configToString.isEmpty()) {
            Reader r = null;
            try {
                retval = new Properties();
                r = new StringReader(configToString);
                retval.load(r);
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        } else {
            throw new IOException("File not found");
        }

        return retval;
    }
    @Deprecated
    public static Reader getReaderFromResources( Class cls, final String element ) throws IOException {
        Reader retval = null;

        RestTemplate plantilla = new RestTemplate();
        String configToString = plantilla.getForObject(element, String.class);
        if (configToString != null && !configToString.isEmpty()) {
            retval = new StringReader(configToString);
        } else {
            throw new IOException("File not found");
        }

        return retval;


    }


    @Override
    public Collection<String> getResources(Map<ResourceParams, Object> configMap) throws MalformedURLException, Exception {
        return getResources((Pattern) configMap.get(ResourceParams.PATTERN));
    }

    @Override
    public String getContentFromResources(Map<ResourceParams, Object> configMap) throws IOException {
        return getContentFromResources((Class) configMap.get(ResourceParams.CLASS_FROM_READING), (String) configMap.get(ResourceParams.FILE_NAME));
    }

    @Override
    public Properties getPropertiesFromResources(Map<ResourceParams, Object> configMap) throws IOException {
        return getPropertiesFromResources((Class) configMap.get(ResourceParams.CLASS_FROM_READING), (String) configMap.get(ResourceParams.FILE_NAME));
    }

    @Override
    public Reader getReaderFromResources(Map<ResourceParams, Object> configMap) throws IOException {
        return getReaderFromResources((Class) configMap.get(ResourceParams.CLASS_FROM_READING), (String) configMap.get(ResourceParams.FILE_NAME));
    }

    @Override
    public void putContentFromResources(String file, Map<ResourceParams, Object> paramsMap) throws Exception {
        throw new NotImplementedYetException();
    }

    @Override
    public void putContentFromResources(File input, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException, NotImplementedYetException {
        throw new NotImplementedYetException();
    }

    @Override
    public Collection<String> getResourcesFromDirectory(Map<ResourceParams, Object> paramsMap) throws IOException, NotImplementedYetException {
        throw new NotImplementedYetException();
    }

}
