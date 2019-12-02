package com.example.demo.provider;

import com.example.demo.exception.CustomizeErrorCode;
import com.example.demo.exception.CustomizeException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Component
public class QCloudProvider {

    @Value("${qcloud.cos.secretId}")
    private String secretId = "AKIDgCYoocrEzax5ycCGDuW6SZZNxJRNtXki";

    @Value("${qcloud.cos.secretKey}")
    private String secretKey = "UjjU3QghU6RZS9hz1o8D1ax6ze4FAi2B";


    COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
    // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
    Region region = new Region("ap-guangzhou");
    ClientConfig clientConfig = new ClientConfig(region);
    // 3 生成 cos 客户端。
    COSClient cosClient = new COSClient(cred, clientConfig);

    public String upload(InputStream inputStream, String mimeType, String fileName, long size) {
        String generatedKey;
        String bucketName = "community-1300814003";
        String[] filePath = fileName.split("\\.");
        // 指定要上传到 COS 上对象键
        if (filePath.length > 1) {
            generatedKey = filePath[filePath.length - 1] + "/" + UUID.randomUUID().toString() + "." + filePath[filePath.length - 1];
        } else {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
        try {
            // 指定要上传到的存储桶
            ObjectMetadata metadate = new ObjectMetadata();
            // 设置输入流长度为500
            metadate.setContentLength(size);
            metadate.setContentType(mimeType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, generatedKey, inputStream, metadate);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            if (putObjectRequest != null) {
                GeneratePresignedUrlRequest req =
                        new GeneratePresignedUrlRequest(bucketName, generatedKey, HttpMethodName.GET);
                Date expirationDate = new Date(System.currentTimeMillis() + 10L * 365L * 24L * 60L * 60L * 1000L);
                req.setExpiration(expirationDate);
                URL url = cosClient.generatePresignedUrl(req);
                return url.toString();
            } else {
                throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
            }
        } catch (CosServiceException serverException) {
            serverException.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (CosClientException clientException) {
            clientException.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
    }
}
