package com.hiflink.common.utils.resources.impl;


import com.hiflink.common.utils.exceptions.NotImplementedYetException;
import com.hiflink.common.utils.resources.Resource;
import com.hiflink.common.utils.resources.ResourceParams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.AbortedException;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.AmazonServiceException;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.auth.BasicAWSCredentials;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.AmazonS3;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.AmazonS3Client;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.S3ClientOptions;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.model.*;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.transfer.TransferManager;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.apache.flink.fs.s3base.shaded.com.amazonaws.services.s3.transfer.Upload;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class S3Resources implements Resource, Serializable {


    private final String S3_SECRET_KEY;
    private final String S3_ACCESS_KEY;
    private final String S3_END_POINT_URL;
    private transient AmazonS3 clientS3;

    private transient Log log;

    public S3Resources() {
        S3_SECRET_KEY = null;
        S3_END_POINT_URL = null;

        S3_ACCESS_KEY = null;
    }

    public S3Resources(Map<ResourceParams, Object> params) {
        log = LogFactory.getLog(S3Resources.class);
        validateParamsConstructor(params);

       /* AWSCredentials credentials = new BasicAWSCredentials((String) params.get(ResourceParams.S3_ACCESS_KEY), (String) params.get(ResourceParams.S3_SECRET_KEY));
        AmazonS3 clientS3 = AmazonS3ClientBuilder.standard().
                        withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("us-east-1")
                .withClientConfiguration(new ClientConfiguration().with)
                        //.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration((String) params.get(ResourceParams.S3_END_POINT_URL),"us-east-1"))
                        .build();
        clientS3.setEndpoint((String) params.get(ResourceParams.S3_END_POINT_URL));
        this.clientS3 = new AmazonS3Client(credentials);
*/
        S3_ACCESS_KEY = (String) params.get(ResourceParams.S3_ACCESS_KEY);
        S3_SECRET_KEY = (String) params.get(ResourceParams.S3_SECRET_KEY);
        S3_END_POINT_URL = (String) params.get(ResourceParams.S3_END_POINT_URL);


        crearClienteS3();
        log.info("cliente creado");

    }

    private void crearClienteS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY);

        clientS3 = new AmazonS3Client(awsCreds);
        clientS3.setEndpoint(S3_END_POINT_URL);

        log.debug("endpoint = [" + S3_END_POINT_URL + "]");
//        log.debug("getURL() = [" + s3client.getUrl("mibucketName", "unaKey") + "]");

        clientS3.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));

    }


    @Override
    public Collection<String> getResources(Map<ResourceParams, Object> configMap) throws MalformedURLException, Exception {
        throw new NotImplementedYetException();
    }

    @Override
    public String getContentFromResources(Map<ResourceParams, Object> paramsMap) throws IOException {


        String bucketName = (String) paramsMap.get(ResourceParams.S3_BUCKET_NAME);
        String fileKey = (String) paramsMap.get(ResourceParams.S3_FILE_KEY);

        if (bucketName == null)
            throw new IllegalArgumentException("The parameter [" + ResourceParams.S3_BUCKET_NAME + "] can't be null");
        if (fileKey == null)
            throw new IllegalArgumentException("The field [" + ResourceParams.S3_FILE_KEY + "] can't be null");

        //fileKey = FilenameUtils.normalize(fileKey);

        try {
            log.debug("Downloading in " + FilenameUtils.getFullPath(fileKey) + " the object: " + FilenameUtils.getName(fileKey));
            log.debug("URL File = [" + clientS3.getUrl(bucketName, fileKey) + "]");

            S3Object fullObject = clientS3.getObject(new GetObjectRequest(bucketName, fileKey));
            log.debug("Content-Type: " + fullObject.getObjectMetadata().getContentType());

            return IOUtils.toString(fullObject.getObjectContent());

        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IOException(e);
        }


    }

    @Override
    public Properties getPropertiesFromResources(Map<ResourceParams, Object> configMap) throws IOException, Exception {
        throw new NotImplementedYetException();
    }

    @Override
    public Reader getReaderFromResources(Map<ResourceParams, Object> paramsMap) throws IOException {
        try {
            String bucketName = (String) paramsMap.get(ResourceParams.S3_BUCKET_NAME);
            String fileKey = (String) paramsMap.get(ResourceParams.S3_FILE_KEY);
            String encoding = (String) paramsMap.getOrDefault(ResourceParams.TEXT_ENCODING, "UTF-8");

            if (bucketName == null)
                throw new IllegalArgumentException("The parameter [" + ResourceParams.S3_BUCKET_NAME + "] can't be null");
            if (fileKey == null)
                throw new IllegalArgumentException("The field [" + ResourceParams.S3_FILE_KEY + "] can't be null");

            //fileKey = FilenameUtils.normalize(fileKey);

            log.debug("Downloading inputStream in [" + FilenameUtils.getFullPath(fileKey) + "] the object: [" + FilenameUtils.getName(fileKey) + "]");
            try {
                log.debug("URL File = [" + clientS3.getUrl(bucketName, fileKey) + "]");
                S3Object fullObject = clientS3.getObject(new GetObjectRequest(bucketName, FilenameUtils.normalizeNoEndSeparator(fileKey, true)));
                log.debug("Content-Type: " + fullObject.getObjectMetadata().getContentType());
                S3ObjectInputStream inputStream = fullObject.getObjectContent();

                return new BufferedReader(new InputStreamReader(inputStream, encoding));
            } catch (AbortedException e) {
                e.printStackTrace();
                Thread.interrupted();
                clientS3.shutdown();
                crearClienteS3();
                log.debug("URL File = [" + clientS3.getUrl(bucketName, fileKey) + "]");
                S3Object fullObject = clientS3.getObject(new GetObjectRequest(bucketName, FilenameUtils.normalizeNoEndSeparator(fileKey, true)));
                log.debug("Content-Type: " + fullObject.getObjectMetadata().getContentType());
                S3ObjectInputStream inputStream = fullObject.getObjectContent();

                return new BufferedReader(new InputStreamReader(inputStream, encoding));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }


    @Override
    public Collection<String> getResourcesFromDirectory(Map<ResourceParams, Object> paramsMap) throws IOException {

        String bucketName = (String) paramsMap.get(ResourceParams.S3_BUCKET_NAME);
        String directoyKey = (String) paramsMap.get(ResourceParams.DIRECTORY_KEY);
        String fileExtension = (paramsMap.get(ResourceParams.FILE_EXTENSION) != null) ? (String) paramsMap.get(ResourceParams.FILE_EXTENSION) : "";

        if (bucketName == null)
            throw new IllegalArgumentException("The parameter [" + ResourceParams.S3_BUCKET_NAME + "] can't be null");
        if (directoyKey == null)
            throw new IllegalArgumentException("The field [" + ResourceParams.DIRECTORY_KEY + "] can't be null");

        //fileKey = FilenameUtils.normalize(fileKey);

        try {
            log.debug("URL File = [" + clientS3.getUrl(bucketName, directoyKey) + "]");


            ObjectListing objectListing = clientS3.listObjects(bucketName, directoyKey);


            return objectListing.getObjectSummaries().stream()
                    .filter((obj) -> {
                        if (!obj.getKey().equalsIgnoreCase(objectListing.getPrefix()))
                            return obj.getKey().endsWith(fileExtension);
                        return false;

                    })
                    .map((obj) -> obj.getKey())
                    .collect(Collectors.toList());


        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

    }


    // ************************************************************************
    // ************************************************************************
    // *************************   WRITE METHODS   ****************************
    // ************************************************************************
    // ************************************************************************

    @Override
    public void putContentFromResources(String file, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException {
        String bucketName = (String) paramsMap.get(ResourceParams.S3_BUCKET_NAME);
        String fileKey = (String) paramsMap.get(ResourceParams.S3_FILE_KEY);

        if (bucketName == null || "".equals(bucketName))
            throw new IllegalArgumentException("The parameter [" + ResourceParams.S3_BUCKET_NAME + "] can't be null or empty");
        if (fileKey == null || "".equals(fileKey))
            throw new IllegalArgumentException("The field [" + ResourceParams.S3_FILE_KEY + "] can't be null or empty");
        if (file == null)
            throw new IllegalArgumentException("The field [contentFile] can't be null");


        try {
            if (clientS3.doesBucketExist(bucketName)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength((long) file.length());

                PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, new ByteArrayInputStream(file.getBytes()), metadata);
                request.withCannedAcl(CannedAccessControlList.PublicRead);
                clientS3.putObject(request);
            } else {
                throw new IOException("The bucket [" + bucketName + "] don't exist");
            }

        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }

    }


    @Override
    public void putContentFromResources(File file, Map<ResourceParams, Object> paramsMap) throws IllegalArgumentException, IOException {
        String bucketName = (String) paramsMap.get(ResourceParams.S3_BUCKET_NAME);
        String fileKey = (String) paramsMap.get(ResourceParams.S3_FILE_KEY);

        if (bucketName == null || "".equals(bucketName))
            throw new IllegalArgumentException("The parameter [" + ResourceParams.S3_BUCKET_NAME + "] can't be null or empty");
        if (fileKey == null || "".equals(fileKey))
            throw new IllegalArgumentException("The field [" + ResourceParams.S3_FILE_KEY + "] can't be null or empty");
        if (file == null)
            throw new IllegalArgumentException("The field [input] can't be null");


        try {
            if (clientS3.doesBucketExist(bucketName)) {
                ObjectMetadata metadata = new ObjectMetadata();

                PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, file);
                request.withCannedAcl(CannedAccessControlList.PublicRead);
//                clientS3.putObject(request);

                TransferManager tm = TransferManagerBuilder.standard()
                        .withS3Client(clientS3)
                        .build();

                // TransferManager processes all transfers asynchronously,
                // so this call returns immediately.
                Upload upload = tm.upload(request);

                System.out.println("Object upload started");

                // Optionally, wait for the upload to finish before continuing.
                upload.waitForCompletion();
                System.out.println("Object upload complete");


            } else {
                throw new IOException("The bucket [" + bucketName + "] don't exist");
            }

        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


// ************************************************************************
// ************************************************************************
// *************************   OTHER METHODS   ****************************
// ************************************************************************
// ************************************************************************


    private void validateParamsConstructor(Map<ResourceParams, Object> params) {

        boolean illegalArg = false;
        ResourceParams param = null;

        if (params.get(ResourceParams.S3_ACCESS_KEY) == null || "".equals(params.get(ResourceParams.S3_ACCESS_KEY))) {
            illegalArg = true;
            param = ResourceParams.S3_ACCESS_KEY;
        }
        if (params.get(ResourceParams.S3_SECRET_KEY) == null || "".equals(params.get(ResourceParams.S3_SECRET_KEY))) {
            illegalArg = true;
            param = ResourceParams.S3_SECRET_KEY;
        }
        if (params.get(ResourceParams.S3_END_POINT_URL) == null || "".equals(params.get(ResourceParams.S3_END_POINT_URL))) {
            illegalArg = true;
            param = ResourceParams.S3_END_POINT_URL;
        }


        if (illegalArg) {
            throw new IllegalArgumentException("The field [" + param + "] can't be null or empty");
        }


    }


}
