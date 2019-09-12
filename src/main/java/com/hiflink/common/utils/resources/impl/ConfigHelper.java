package com.hiflink.common.utils.resources.impl;

import com.typesafe.config.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ConfigHelper implements Serializable {
    public Config loadConfig(Class<?> currentClass, String element, String configMode, String elementPrefix) throws IOException {
        Config internalConfig;
        Config resolvedConfig = ConfigFactory.empty();
        Properties accumulatedProperties;

        //This located base config
        internalConfig = ConfigFactory.parseReader(ResourceManager.getReaderFromResources(currentClass, element, configMode, elementPrefix));


        if (internalConfig.hasPath("multi.config.order")) {
            String[] configurationArray = internalConfig.getString("multi.config.order").split(";");
            List<String> accumulableArray = Arrays.asList(internalConfig.getString("multi.config.accumulables").split(";"));
            accumulatedProperties = new Properties();
            for (String configurationData : configurationArray) {
                internalConfig = ConfigFactory.parseReader(ResourceManager.getReaderFromResources(currentClass, configurationData, configMode, elementPrefix));

                Iterator<Map.Entry<String, ConfigValue>> configIterator = internalConfig.entrySet().iterator();
                while (configIterator.hasNext()) {
                    Map.Entry<String, ConfigValue> cfgToUse = configIterator.next();

                    if (cfgToUse.getValue().valueType() == ConfigValueType.STRING) {

                        String toSetData = internalConfig.getString(cfgToUse.getKey());
                        if (resolvedConfig.hasPath(cfgToUse.getKey())) {
                            if (!accumulableArray.contains(cfgToUse.getKey())) {
                                toSetData = internalConfig.getString(cfgToUse.getKey());
                            } else {
                                String postpend = resolvedConfig.getString(cfgToUse.getKey());
                                toSetData = toSetData + (!toSetData.isEmpty() && !postpend.isEmpty() ? "," : "") + postpend;
                            }
                        }
                        resolvedConfig = resolvedConfig.withValue(cfgToUse.getKey(), ConfigValueFactory.fromAnyRef(toSetData));
                    } else {
                        resolvedConfig = resolvedConfig.withValue(cfgToUse.getKey(), cfgToUse.getValue());
                    }
                }
            }
        } else {
            resolvedConfig = internalConfig;
        }

        return resolvedConfig;
    }


    public Config addConfig(Config config, Config updateParameters) {
        Config returnConfig = config != null ? config : ConfigFactory.empty();
        if (updateParameters != null) {

            Iterator<Map.Entry<String, ConfigValue>> configIterator = updateParameters.entrySet().iterator();

            while (configIterator.hasNext()) {
                Map.Entry<String, ConfigValue> cfgToUse = configIterator.next();
                returnConfig = returnConfig.withValue(cfgToUse.getKey(), cfgToUse.getValue());
            }
        }
        return returnConfig;

    }

    public Config addConfig(Config config, Properties updateParameters) {
        Config returnConfig = config != null ? config : ConfigFactory.empty();
        if (updateParameters != null) {

            Iterator<Map.Entry<Object, Object>> configIterator = updateParameters.entrySet().iterator();

            while (configIterator.hasNext()) {
                Map.Entry<Object, Object> cfgToUse = configIterator.next();
                returnConfig = returnConfig.withValue(cfgToUse.getKey().toString(), ConfigValueFactory.fromAnyRef(cfgToUse.getValue()));
            }
        }
        return returnConfig;

    }

//    public String resolveConfigValue(Config config, String minPath, List<String> namePath) {
//
//        String strategyPrefix = "";
//        int locatedPosition = 0;
//        List<String> sortedPrefix = namePath;
//        if (namePath.size() <= minPath.split("\\.").length) {
//            if (config.hasPath(minPath)) {
//                return config.getString(minPath);
//            }
//            return null;
//        }
//
//        String fullName = "";
//        for (String s : namePath) {
//            fullName = fullName + s + ".";
//        }
//
//        fullName = fullName.endsWith(".") ? fullName.substring(0, fullName.length() - 1) : fullName;
//        fullName = fullName.startsWith(".") ? fullName.substring(1, fullName.length()) : fullName;
//
//        if (config.hasPath(fullName)) {
//            return config.getString(fullName);
//        } else {
//            return resolveConfigValue(config, minPath, namePath.subList(1, namePath.size()));
//        }
//    }


    public String resolveConfigValue(Config config, String minPath, List<String> namePath) {

       return resolveConfigValue(config, minPath, namePath,null);
    }

    public String resolveConfigValue(Config config, String minPath, List<String> namePath, String defaultValue) {

        String strategyPrefix = "";
        int locatedPosition = 0;
        List<String> sortedPrefix = namePath;
        if (namePath.size() <= minPath.split("\\.").length) {
            if (config.hasPath(minPath)) {
                return config.getString(minPath);
            } else if (defaultValue != null) {
                return defaultValue;
            }
            return null;
        }

        String fullName = "";
        for (String s : namePath) {
            fullName = fullName + s + ".";
        }
        fullName = fullName.endsWith(".") ? fullName.substring(0, fullName.length() - 1) : fullName;
        fullName = fullName.startsWith(".") ? fullName.substring(1, fullName.length()) : fullName;

        if (config.hasPath(fullName)) {
            return config.getString(fullName);
        } else {
            return resolveConfigValue(config, minPath, namePath.subList(1, namePath.size()),defaultValue);
        }
    }
}
