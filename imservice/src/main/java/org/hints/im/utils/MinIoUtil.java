package org.hints.im.utils;

/**
 * Created by hints on 2021/10/19 19:16
 */

import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hints.im.config.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class MinIoUtil {

    @Autowired
    MinioConfig minioConfig;

    private static MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            if (StringUtils.isNotEmpty(minioConfig.getUrl())) {
                minioClient = new MinioClient.Builder().endpoint(minioConfig.getUrl())
                        .credentials( minioConfig.getAccessKey(), minioConfig.getSecretKey())
                        .build();
                createBucket(minioConfig.getBucketName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化minio配置异常: 【{}】", e.fillInStackTrace());
        }
    }

    /** 判断 bucket是否存在
     */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /** 创建 bucket
     */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /** 获取全部bucket
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /** 文件上传
     */
    @SneakyThrows(Exception.class)
    public static boolean upload(String bucketName, String fileName, InputStream stream) {
        long size = stream.available();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(stream, size, -1)
                    .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static final String getExtension(MultipartFile file)
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension))
        {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    public static final String extractFilename(MultipartFile file)
    {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = UUID.randomUUID().toString().replaceAll("-", "")+ "." + extension;
        return fileName;
    }

    /** 文件上传
     */
    @SneakyThrows(Exception.class)
    public static String upload(String baseDir, String bucketName, MultipartFile file) {
        String fileName = extractFilename(file);
        fileName = baseDir+ "/" + fileName;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1).contentType(file.getContentType())
                    .build());
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 删除文件*/
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName)
                .object(fileName).build());
    }

    /**
     * 下载文件*/
    @SneakyThrows(Exception.class)
    public static void download(String bucketName, String fileName, HttpServletResponse response) {
        //获取文件源信息
        StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
        //设置响应的内容类型 --浏览器对应不同类型做不同处理
        response.setContentType(statObject.contentType());
        //设置响应头
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(fileName, "UTF-8"));

        //文件下载
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build());

        IOUtils.copy(inputStream, response.getOutputStream());

        inputStream.close();
    }

    /** 获取minio文件的下载地址
     */
    @SneakyThrows(Exception.class)
    public static String getFileUrl(String bucketName, String fileName) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName)
        .object(fileName).build());
    }

}