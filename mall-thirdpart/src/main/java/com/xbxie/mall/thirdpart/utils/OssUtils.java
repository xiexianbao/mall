package com.xbxie.mall.thirdpart.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

/**
 * created by xbxie on 2024/5/17
 */
public class OssUtils {
    private static final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI5t7iLLPCoF36meA9rKoG";
    private static final String accessKeySecret = "Yqkcuqy9zuBXeZChjAF0dR2yc0VpqO";
    private static final String bucketName = "gulimall-source";
    private static final String bucketDomain  = "https://gulimall-source.oss-cn-beijing.aliyuncs.com";

    public static String uploadImg(MultipartFile multipartFile) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            InputStream inputStream = multipartFile.getInputStream();
            String objectName = "mall/" + System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            return bucketDomain + "/" + objectName;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return "";
    }
}
