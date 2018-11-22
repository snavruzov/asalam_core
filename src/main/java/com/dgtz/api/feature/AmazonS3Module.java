package com.dgtz.api.feature;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.opsworks.model.StopInstanceRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.utils.FileUtil;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 9/14/14
 */
public class AmazonS3Module {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AmazonS3Module.class);
    public final static String VIDEO_BUCKET = Constants.AWS_VIDEO_URL;
    public final static String IMAGE_BUCKET = Constants.AWS_IMAGE_URL;
    //final static AmazonS3 s3;
    private static String CACHE_CONTROL = "public, max-age=604800";


    public EnumErrors uploadVideoFile(String filename, String filepath) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        /*AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);
        ObjectMetadata newObjectMetadata = new ObjectMetadata();
        newObjectMetadata.setCacheControl(CACHE_CONTROL);
        //newObjectMetadata.setExpirationTime();
        if (filepath.endsWith("webm"))
            newObjectMetadata.setContentType("video/webm");
        */
        try {
            FileUtil.copyFile(filepath, VIDEO_BUCKET+filename);
        } catch (IOException e) {
            log.error("ERROR ", e);
        }
        /*PutObjectResult result = s3.putObject(new PutObjectRequest(VIDEO_BUCKET, filename,
                new File(filepath)).withMetadata(newObjectMetadata).withAccessControlList(acl));

        System.out.println("ETAG:::: " + result.getETag());
        */
        return errors;
    }

    public EnumErrors removeVideoFile(String filename) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            *//*CopyObjectRequest copyRequest =
                    new CopyObjectRequest(VIDEO_BUCKET, filename, VIDEO_BUCKET, filename + "_" + RMemoryAPI.getInstance().currentTimeMillis());

            s3.copyObject(copyRequest);*//*

            //Delete the original
            DeleteObjectRequest deleteRequest = new DeleteObjectRequest(VIDEO_BUCKET, filename);
            s3.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("ERROR IN DELTEING VIDEO::: {}", e);
        }*/
        return errors;
    }

    public EnumErrors removeVideoFolder(String foldername) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            FileUtil.deleteDirectory(foldername);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            //Delete the folders files first
            s3.listObjects(VIDEO_BUCKET, foldername).getObjectSummaries()
                    .forEach(file->s3.deleteObject(VIDEO_BUCKET, file.getKey()));

        } catch (Exception e) {
            log.error("ERROR IN DELTEING VIDEO FOLDER::: {}", e);
        }*/
        return errors;
    }

    public EnumErrors removeImageFile(String filename) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            log.debug("REMOVE IMAGE FROM S3 {}", filename);
            *//*CopyObjectRequest copyRequest =
                    new CopyObjectRequest(IMAGE_BUCKET, filename, IMAGE_BUCKET, filename + "_" + RMemoryAPI.getInstance().currentTimeMillis());

            s3.copyObject(copyRequest);*//*

            //Delete the original
            DeleteObjectRequest deleteRequest = new DeleteObjectRequest(IMAGE_BUCKET, filename);
            s3.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("ERROR IN DELTEING IMAGE::: {}", e);
        }*/
        return errors;
    }

    public EnumErrors copyImageFile(String source, String dest) {
        try {
            FileUtil.copyFile(IMAGE_BUCKET+source, IMAGE_BUCKET+dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EnumErrors errors = EnumErrors.NO_ERRORS;
        log.debug("COPY IMAGE FROM S3 {}", source);
        /*AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);

        ObjectMetadata newObjectMetadata = new ObjectMetadata();
        newObjectMetadata.setCacheControl(CACHE_CONTROL);
        CopyObjectRequest copyRequest =
                new CopyObjectRequest(IMAGE_BUCKET, source, IMAGE_BUCKET, dest)
                        .withNewObjectMetadata(newObjectMetadata)
                        .withAccessControlList(acl);

        s3.copyObject(copyRequest);*/

        return errors;
    }

    public EnumErrors copyVideoFile(String source, String dest) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        log.debug("COPY VIDEO FROM S3 {}", source);
        try {
            FileUtil.copyFile(VIDEO_BUCKET+source, VIDEO_BUCKET+dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);

        ObjectMetadata newObjectMetadata = new ObjectMetadata();
        newObjectMetadata.setCacheControl(CACHE_CONTROL);
        CopyObjectRequest copyRequest =
                new CopyObjectRequest(VIDEO_BUCKET, source, VIDEO_BUCKET, dest)
                        .withNewObjectMetadata(newObjectMetadata)
                        .withAccessControlList(acl);

        s3.copyObject(copyRequest);*/

        return errors;
    }

    public EnumErrors uploadImageFile(String filename, String filepath) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        /*try {
            ObjectMetadata newObjectMetadata = new ObjectMetadata();
            newObjectMetadata.setCacheControl(CACHE_CONTROL);

            if (filepath.endsWith("webp"))
                newObjectMetadata.setContentType("image/webp");

            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);

            s3.putObject(new PutObjectRequest(IMAGE_BUCKET, filename,
                    new File(filepath)).withMetadata(newObjectMetadata).withAccessControlList(acl));


        } catch (AmazonServiceException ase) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());

        }*/

        try {
            FileUtil.copyFile(filepath, IMAGE_BUCKET+filename);
        } catch (IOException e) {
            log.error("ERROR ", e);
        }
        return errors;
    }

    public EnumErrors uploadOriginalFile(String filename, String filepath, int bucketType) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        //TransferManager tx = new TransferManager(s3);
        try {
            FileUtil.copyFile(filepath, bucketType == 0 ? VIDEO_BUCKET : IMAGE_BUCKET+filename);
        } catch (IOException e) {
            log.error("ERROR ", e);
        }
        /*try {
            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);
            PutObjectRequest obj = new PutObjectRequest(bucketType == 0 ? VIDEO_BUCKET : IMAGE_BUCKET, filename,
                    new File(filepath)).withAccessControlList(acl);


            Upload up = tx.upload(obj);
            while (!up.isDone()) {
                log.info("Transfer: " + up.getDescription());
                log.info("  - State: " + up.getState());
                log.info("  - Progress: " + up.getProgress().getPercentTransferred());
                     // Do work while we wait for our upload to complete...
                Thread.sleep(1000);
            }

            try {
                Files.deleteIfExists(Paths.get(filepath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.debug("ORIGINAL UPLOADING IS DONE. {}", filename);

        } catch (AmazonServiceException ase) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (InterruptedException e) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            e.printStackTrace();
        }
        finally {
            tx.shutdownNow(false);
        }*/

        return errors;
    }

    public EnumErrors uploadOriginalAudioFile(String filename, String filepath) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            FileUtil.copyFile(filepath, VIDEO_BUCKET+filename);
        } catch (IOException e) {
            log.error("ERROR ", e);
        }
        /*try {
            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AuthenticatedUsers, Permission.Read);
            PutObjectRequest obj = new PutObjectRequest(VIDEO_BUCKET, filename,
                    new File(filepath)).withAccessControlList(acl);

            TransferManager tx = new TransferManager(s3);
            Upload up = tx.upload(obj);

            while (!up.isDone()) {
                log.debug("ORIGINAL AUDIO UPLOADING...{}", filename);
                Thread.sleep(500);
            }

            log.debug("ORIGINAL AUDIO UPLOADING IS DONE. {}", filename);
            tx.shutdownNow(false);

        } catch (AmazonServiceException ase) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (InterruptedException e) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            e.printStackTrace();
        }*/

        return errors;
    }

    @Deprecated
    /*
     *
     *  HLS is delivered from local server
    */
    public EnumErrors uploadFolder(Long idMedia, String dirName, String dirPath) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            FileUtil.copyDirectory(dirPath, VIDEO_BUCKET+dirName);
            /*HLS has been built and uploaded*/
            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + "hls:" + idMedia, "status", "done");
            /*Removing Files and Dirs*/
            FileUtil.deleteDirectory(dirPath);
        } catch (IOException e) {
            log.error("ERROR ", e);
        }
        /*try {
            System.out.println(VIDEO_BUCKET);
            TransferManager tx = new TransferManager(s3);
            MultipleFileUpload up = tx.uploadDirectory(VIDEO_BUCKET, dirName, new File(dirPath), true);

            while (!up.isDone()) {
                log.debug("HLS UPLOADING...{}", idMedia);
                Thread.sleep(5000);
            }

            log.debug("HLS UPLOADING IS DONE. {}", idMedia);
            tx.shutdownNow(false);

            *//*HLS has been built and uploaded*//*
            RMemoryAPI.getInstance().pushHashToMemory(Constants.MEDIA_KEY + "hls:" + idMedia, "status", "done");
            *//*Removing Files and Dirs*//*
            FileUtil.deleteDirectory(dirPath);


        } catch (AmazonServiceException ase) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (InterruptedException e) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return errors;
    }

    public EnumErrors uploadHLSFolder(Long idMedia, String dirName, String dirPath) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        FileUtil.copyDirectory(dirPath, VIDEO_BUCKET+dirName);

        /*try {
            log.info("HLS FOLDER UPLOAD {}",VIDEO_BUCKET);
            TransferManager tx = new TransferManager(s3);
            MultipleFileUpload up = tx.uploadDirectory(VIDEO_BUCKET, dirName, new File(dirPath), true);

            while (!up.isDone()) {
                log.info("HLS UPLOADING...{}", idMedia);
                Thread.sleep(5000);
            }

            log.info("HLS UPLOADING IS DONE. {}", idMedia);
            tx.shutdownNow(false);

        } catch (AmazonServiceException ase) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (InterruptedException e) {
            errors = EnumErrors.FILE_UPLOAD_ERROR;
            e.printStackTrace();
        }*/

        return errors;
    }

    public static boolean isValidVideoFile(String path) throws AmazonClientException {
        boolean isValidFile = true;
        if(!new File(VIDEO_BUCKET+path.replace(" ", "")).exists()){
            isValidFile = false;
        }
        /*try {

            //ObjectMetadata objectMetadata = s3.getObjectMetadata(VIDEO_BUCKET, path.replace(" ", ""));
        } catch (AmazonS3Exception s3e) {
            if (s3e.getStatusCode() == 404) {
                // i.e. 404: NoSuchKey - The specified key does not exist
                isValidFile = false;
            } else {
                throw s3e;    // rethrow all S3 exceptions other than 404
            }
        }*/

        return isValidFile;
    }

    public static boolean isValidImageFile(String path) throws AmazonClientException {
        boolean isValidFile = true;
        if(!new File(IMAGE_BUCKET+path.replace(" ", "")).exists()){
            isValidFile = false;
        }
        /*try {
            ObjectMetadata objectMetadata = s3.getObjectMetadata(IMAGE_BUCKET, path.replace(" ", ""));
        } catch (AmazonS3Exception s3e) {
            if (s3e.getStatusCode() == 404) {
                // i.e. 404: NoSuchKey - The specified key does not exist
                isValidFile = false;
            } else {
                throw s3e;    // rethrow all S3 exceptions other than 404
            }
        }*/

        return isValidFile;
    }

    /*public void getKeyLIst() {
        try {
            System.out.println("Listing objects");

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(VIDEO_BUCKET);

            ObjectListing objectListing;
            do {
                objectListing = s3.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary :
                        objectListing.getObjectSummaries()) {
                    System.out.println(" - " + objectSummary.getKey() + "  " +
                            "(size = " + objectSummary.getSize() +
                            ")");
                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());
            } while (objectListing.isTruncated());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }*/

    /*public String generateTokenUrl(String uri, String bucketName) {
        String link = "";
        try {
            System.out.println("Generating pre-signed URL.");
            java.util.Date expiration = new java.util.Date();
            long milliSeconds = expiration.getTime();
            milliSeconds += 1000 * 60 * 180; // Add 1 hour.
            expiration.setTime(milliSeconds);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, uri);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);

            URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

            link = url.toString().replace("https", "http");
            System.out.println("Pre-Signed URL = " + url.toString());
        } catch (AmazonServiceException exception) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message: " + exception.getMessage());
            System.out.println("HTTP  Code: " + exception.getStatusCode());
            System.out.println("AWS Error Code:" + exception.getErrorCode());
            System.out.println("Error Type:    " + exception.getErrorType());
            System.out.println("Request ID:    " + exception.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

        return link;

    }*/

}
