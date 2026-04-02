package com.vince.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.vince.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OssController {

    // 1. 注入 OSS 客户端（Spring Cloud Starter 会根据配置自动创建该 Bean）
    @Autowired
    private OSS ossClient;

    // 2. 从配置文件中注入相关参数
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @RequestMapping("/oss/policy")
    public R policy() {

        // 拼接上传域名：https://bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;

        // 设置上传到 OSS 的文件夹名，按日期归档
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format + "/";

        Map<String, String> respMap = null;
        try {
            long expireTime = 30; // 签名 30 秒内有效
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            PolicyConditions policyConds = new PolicyConditions();
            // 设置上传文件的大小范围：0 - 1GB
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            // 设置上传的文件名必须以指定目录开头
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 生成策略
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);

            // 生成签名
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 封装返回数据
            respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.ok().put("data", respMap);
    }
}