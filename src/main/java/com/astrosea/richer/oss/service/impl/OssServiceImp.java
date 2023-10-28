package com.astrosea.richer.oss.service.impl;

import com.aliyun.oss.OSS;
import com.astrosea.richer.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class OssServiceImp implements OssService {


    @Autowired
    OSS oss;

    @Value("${oss.bucketName}")
    private String bucketName;


    @Override
    public String getUrlByNum(Integer num) {
        // 将库中的 int 转为 Str
        String numStr = num.toString();

        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName =  numStr + ".jpeg";
//        String objectName = "exampledir/exampleobject.txt";

        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        oss.getObject(bucketName, objectName);

        // 设置签名URL过期时间，单位为毫秒。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);

        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = oss.generatePresignedUrl(bucketName, objectName, expiration);

        // 转为 string
        String urlStr = url.toString();
        System.out.println(url);

        return urlStr;
    }

    @Override
    public String getEndpointUrlByNum(Integer num) {
        // 将数字转为字符串
        String numStr = num.toString();

        // 带域名的 url
        String urlStr = "www.astrosea.io/nft/"+ numStr +".jpeg ";

        return urlStr;
    }

    @Override
    public String getUrlByName(String name) {

        String objectName = name;

        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        oss.getObject(bucketName, objectName);

        // 设置签名URL过期时间，单位为毫秒。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);

        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = oss.generatePresignedUrl(bucketName, objectName, expiration);

        // 转为 string
        String urlStr = url.toString();
        System.out.println(urlStr);

        return urlStr;
    }
}
