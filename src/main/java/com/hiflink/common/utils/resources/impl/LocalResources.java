package com.hiflink.common.utils.resources.impl;


import com.hiflink.common.utils.exceptions.NotImplementedYetException;
import com.hiflink.common.utils.resources.Resource;
import com.hiflink.common.utils.resources.ResourceParams;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class LocalResources implements Resource, Serializable {

    public static String resourcesMode = "file";

    public LocalResources() {
    }

    /**
     * Permite obtener un objeto 'Properties' a partir del archivo 'fileName' indicado.
     *
     * @param fileName archivo que contiene las properties deseadas. Puede contener una ruta si el archivo se encuentra en un directorio.
     * @return contenido del archivo 'fileName' como objeto Properties.
     * @throws IOException              en caso de error de lectura/escritura
     * @throws IllegalArgumentException si 'fileName' es nulo, vacío o el fichero no existe.
     */
    @Deprecated
    public static Properties file2Properties(String fileName) throws IOException {
        if (fileName == null) throw new IllegalArgumentException("El valor de 'fileName' no puede ser nulo.");
        if (fileName.trim().length() == 0)
            throw new IllegalArgumentException("El valor de 'fileName' no puede ser vacío.");
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new IllegalArgumentException("El fichero '" + new File(fileName).getAbsolutePath() + "' no existe.");
        Properties props = new Properties();
        props.load(inputStream);
        return props;
    }

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    @Deprecated
    public static Collection<String> getResources(final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    @Deprecated
    private static Collection<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    @Deprecated
    private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
                retval.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }

    @Deprecated
    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        retval.add(fileName);
                    }
                } catch (final IOException e) {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

    @Deprecated
    public static String getContentFromResources(Class cls, final String element) throws IOException {
        if (element == null || (element != null && element.isEmpty())) {
            throw new IllegalArgumentException("El valor de 'fileName' no puede ser nulo.");
        }

        InputStream in = null;
        BufferedReader br = null;
        String txt = "";
        try {

            in = cls.getResourceAsStream(new File(element).getName());
            if (in == null) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(new File(element).getName());
                if (in == null) {
                    throw new IllegalArgumentException("El fichero '" + new File(element).getAbsolutePath() + "' no existe.");
                }
            }
            br = new BufferedReader(new InputStreamReader(in));
            String resource;
            while ((resource = br.readLine()) != null) {
                txt = txt + resource;
            }


            /*try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String resource;
                while ((resource = br.readLine()) != null) {
                    txt = txt + resource;
                }
            }*/
        } finally {
            if (br != null) {
                br.close();
            }
            if (in != null) {
                in.close();
            }

        }
        return txt;


    }

    @Deprecated
    public static Properties getPropertiesFromResources(Class cls, final String element) throws IOException {
        if (element == null || (element != null && element.isEmpty())) {
            throw new IllegalArgumentException("El valor de 'fileName' no puede ser nulo.");
        }

        Properties props = new Properties();
        InputStream in = null;
        try {

            in = cls.getResourceAsStream(new File(element).getName());
            if (in == null) {

                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(new File(element).getName());
                if (in == null) {
                    throw new IllegalArgumentException("El fichero '" + new File(element).getAbsolutePath() + "' no existe.");
                }
            }
            props.load(in);
        } finally {

            if (in != null) {
                in.close();
            }

        }

        return props;
    }
    @Deprecated
    public static Reader getReaderFromResources(Class cls, final String element) throws IOException {
        if (element == null || (element != null && element.isEmpty())) {
            throw new IllegalArgumentException("El valor de 'fileName' no puede ser nulo.");
        }

        InputStream in = null;
        BufferedReader br = null;


        in = cls.getResourceAsStream(new File(element).getName());
        if (in == null) {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(new File(element).getName());
            if (in == null) {
                throw new IllegalArgumentException("El fichero '" + new File(element).getAbsolutePath() + "' no existe.");
            }
        }
        br = new BufferedReader(new InputStreamReader(in));

        return br;


    }


    @Override
    public Collection<String> getResources(Map<ResourceParams, Object> configMap) throws MalformedURLException, Exception {
        return getResources((Pattern) configMap.get(ResourceParams.PATTERN));
    }

    @Override
    public String getContentFromResources(Map<ResourceParams, Object> paramsMap) throws IOException {
        Class cls = (Class) paramsMap.get(ResourceParams.CLASS_FROM_READING);
        String fileName = (String) paramsMap.get(ResourceParams.FILE_NAME);

        if (cls == null)
            throw new IllegalArgumentException("The parameter [" + ResourceParams.CLASS_FROM_READING + "] can't be null");
        if (fileName == null)
            throw new IllegalArgumentException("The field [" + ResourceParams.FILE_NAME + "] can't be null");


        return getContentFromResources(cls, fileName);
    }

    @Override
    public Properties getPropertiesFromResources(Map<ResourceParams, Object> paramsMap) throws IOException {
        Class cls = (Class) paramsMap.get(ResourceParams.CLASS_FROM_READING);
        String fileName = (String) paramsMap.get(ResourceParams.FILE_NAME);

        if (cls == null)
            throw new IllegalArgumentException("The parameter [" + ResourceParams.CLASS_FROM_READING + "] can't be null");
        if (fileName == null)
            throw new IllegalArgumentException("The field [" + ResourceParams.FILE_NAME + "] can't be null");


        return getPropertiesFromResources(cls, fileName);
    }

    @Override
    public Reader getReaderFromResources(Map<ResourceParams, Object> paramsMap) throws IOException {
        String fileName = (String) paramsMap.get(ResourceParams.FILE_NAME);
        String encoding = (String) paramsMap.getOrDefault(ResourceParams.TEXT_ENCODING,"UTF-8" );

        if (fileName == null)
            throw new IllegalArgumentException("The field [" + ResourceParams.FILE_NAME + "] can't be null");



        if (fileName == null || (fileName != null && fileName.isEmpty())) {
            throw new IllegalArgumentException("El valor de 'fileName' no puede ser nulo.");
        }

        InputStream in  = Files.newInputStream(Paths.get(fileName));
        return new BufferedReader(new InputStreamReader(in,encoding));
    }

    @Override
    public void putContentFromResources(String file, Map<ResourceParams, Object> paramsMap) throws Exception {
        throw new NotImplementedYetException();
    }

    @Override
    public void putContentFromResources(File File, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException, NotImplementedYetException {
        throw new NotImplementedYetException();
    }

    @Override
    public Collection<String> getResourcesFromDirectory(Map<ResourceParams, Object> paramsMap) throws Exception {

        Pattern p = Pattern.compile(".*" + paramsMap.get(ResourceParams.DIRECTORY_KEY) + ".*"+paramsMap.get(ResourceParams.FILE_EXTENSION)+"$");
        paramsMap.put(ResourceParams.PATTERN, p);

        return this.getResources(paramsMap);
    }
}
