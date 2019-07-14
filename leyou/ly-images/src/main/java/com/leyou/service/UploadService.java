package com.leyou.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.gateway.config.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UploadService {
    @Autowired
    private OSSProperties prop;

    @Autowired
    private OSS client;

    final static List<String> list = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    public String uploadImage(MultipartFile file) {

        //检验图片类型
        String type = file.getContentType();
        if (!list.contains(type)) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //检查图片内容
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_NOT_FOUND);
        }
        if (image == null) {
            throw new LyException(ExceptionEnum.INVALID_NOT_FOUND);
        }
        //保存图片到本地路径
        File file1 = new File("D:/nginx-1.12.2/html");
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(file1, file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://image.leyou.com" + file.getOriginalFilename();
    }

    public Map<String, Object>  getSignature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}

