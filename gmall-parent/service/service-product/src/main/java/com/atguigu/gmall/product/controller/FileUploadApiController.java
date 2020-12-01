package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import lombok.SneakyThrows;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @time: 2020/12/1 11:29
 * @author: LIANGBO
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class FileUploadApiController {
    @SneakyThrows
    @RequestMapping("fileUpload")
    public Result FileUploadApiController(@RequestParam("file") MultipartFile multipartFile) {
        //拼接地址前缀
        String url = "192.168.200.128:8080";
        //1.新建TrackerClient连接
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        // 2.连接storage
        StorageClient storageClient = new StorageClient(connection, null);
        //3.上传文件

        //4.// 返回url
    }
}
