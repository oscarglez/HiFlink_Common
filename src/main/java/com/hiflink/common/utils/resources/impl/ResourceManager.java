package com.hiflink.common.utils.resources.impl;

import com.hiflink.common.utils.exceptions.NotImplementedYetException;
import com.hiflink.common.utils.resources.Resource;
import com.hiflink.common.utils.resources.ResourceParams;
import com.hiflink.common.utils.resources.TypeReader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class ResourceManager implements Serializable {
//    @Deprecated
//    public static String resourcesMode = "file";
//    @Deprecated
//    public static String resourcesURI = null;

    private transient Resource resourceInstance;

    public ResourceManager() {
    }


    public ResourceManager(String nameClass) throws Exception {

        //TODO: instanciar el la clase rearudce con el nombre

        throw new NotImplementedYetException();
    }

    public ResourceManager(TypeReader typeReader) {
        this(typeReader, null);
    }

    public ResourceManager(TypeReader typeReader, Map<ResourceParams, Object> paramsMap) {
        if (typeReader == null)
            throw new IllegalArgumentException("The field [TypeReader] can't be null");

        switch (typeReader) {
            case CLOUD:
                resourceInstance = new CloudResource();
                break;
            case S3:
                resourceInstance = new S3Resources(paramsMap);
                break;
            case FILE:
                resourceInstance = new LocalResources();
                break;
        }
    }


    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    @Deprecated
    public static Collection<String> getResources(final Pattern pattern,String resourcesMode) throws MalformedURLException, Exception {
        Collection<String> retval = null;
        if (resourcesMode.equals("file")) {
            retval = LocalResources.getResources(pattern);
        } else {
            CloudResource.getResources(pattern);
        }
        return retval;
    }
    @Deprecated
    public static String getContentFromResources(Class cls, final String element,String resourcesMode,String resourcesURI) throws IOException {
        String retval = null;
        if (resourcesMode.equals("file")) {
            retval = LocalResources.getContentFromResources(cls, element);
        } else {
            String urlToLoad = element;
            URL uri = null;
            try {
                String temporalURI = resourcesURI + element;
                uri = new URL(temporalURI);
                String uriFile = uri.getFile();
                if (uriFile == null || (uriFile != null && uriFile.isEmpty())) {
                    uri = new URL(resourcesURI );
                }
                urlToLoad = uri.toString();
            } catch (IOException ex) {
                uri.getFile();
            }


            retval = CloudResource.getContentFromResources(cls, urlToLoad);
        }
        return retval;


    }
    @Deprecated
    public static Properties getPropertiesFromResources(Class cls, final String element,String resourcesMode,String resourcesURI) throws IOException {
        Properties retval = null;
        if (resourcesMode.equals("file")) {
            retval = LocalResources.getPropertiesFromResources(cls, element);
        } else {
            String urlToLoad = element;
            URL uri = null;
            try {
                String temporalURI = resourcesURI + element;
                uri = new URL(temporalURI);
                String uriFile = uri.getFile();
                if (uriFile == null || (uriFile != null && uriFile.isEmpty())) {
                    uri = new URL(resourcesURI );
                }
                urlToLoad = uri.toString();
            } catch (IOException ex) {
                uri.getFile();
            }


            retval = CloudResource.getPropertiesFromResources(cls, urlToLoad);
        }

        return retval;
    }
    @Deprecated
    public static Reader getReaderFromResources(Class cls, final String element,String resourcesMode,String resourcesURI) throws IOException {
        Reader retval = null;
        if (resourcesMode.equals("file")) {
            retval = LocalResources.getReaderFromResources(cls, element);
        } else {
            String urlToLoad = element;
            URL uri = null;
            try {
                String temporalURI = resourcesURI + element;
                uri = new URL(temporalURI);
                String uriFile = uri.getFile();
                if (uriFile == null || (uriFile != null && uriFile.isEmpty())) {
                    uri = new URL(resourcesURI );
                }
                urlToLoad = uri.toString();
            } catch (IOException ex) {
                uri.getFile();
            }


            retval = CloudResource.getReaderFromResources(cls, urlToLoad);
        }

        return retval;


    }




    public Collection<String> getResourcesFromPattern(Map<ResourceParams, Object> paramsMap) throws MalformedURLException, Exception {
        return resourceInstance.getResources(paramsMap);
    }



    public Collection<String> getResourcesFromDirectory(Map<ResourceParams, Object> paramsMap) throws MalformedURLException, Exception {
        return resourceInstance.getResourcesFromDirectory(paramsMap);
    }


    public Reader getReaderFromResources(Map<ResourceParams, Object> paramsMap) throws Exception {
        return resourceInstance.getReaderFromResources(paramsMap);
    }

    public String getContentFromResources(Map<ResourceParams, Object> paramsMap) throws Exception {
        return resourceInstance.getContentFromResources(paramsMap);
    }

    public Properties getPropertiesFromResources(Map<ResourceParams, Object> configMap) throws Exception {
        return resourceInstance.getPropertiesFromResources(configMap);

    }


    public void putContentFromResources(String file, Map<ResourceParams, Object> paramsMap) throws Exception {
        resourceInstance.putContentFromResources(file, paramsMap);
    }

    public void putContentFromResources(File file, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException, NotImplementedYetException {
        resourceInstance.putContentFromResources(file, paramsMap);
    }


}

