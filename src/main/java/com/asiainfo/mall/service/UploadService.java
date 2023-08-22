package com.asiainfo.mall.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface UploadService {
    void createFile(MultipartFile file, File fileDirectory, File destFile);

    String uploadFile(MultipartFile file);

    String getNewFileName(MultipartFile multipartFile);

    String uploadImage(MultipartFile file) throws IOException;
}
