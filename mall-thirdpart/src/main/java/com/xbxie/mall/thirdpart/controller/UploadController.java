package com.xbxie.mall.thirdpart.controller;


import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.thirdpart.utils.OssUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * created by xbxie on 2024/5/13
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    @PostMapping("/img")
    public R<Map<String, String>> login(@RequestParam("file") MultipartFile multipartFile) {//得到文件的名字
        String fileUrl = OssUtils.uploadImg(multipartFile);
        Map<String, String> map = new HashMap<>();
        map.put("url", fileUrl);
        return R.success(map);
    }
}
