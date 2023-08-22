package com.asiainfo.mall.service.impl;

import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.service.UploadService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadSeriviceImpl implements UploadService {

    @Value("${file.upload.uri}")
    String uri;

    @Override
    public void createFile(MultipartFile file, File fileDirectory, File destFile) {
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        createFile(file, fileDirectory, destFile);
        String address = uri;
        String result = "http://" + address + "/images/" + newFileName;
        return result;
    }
    @Override
    public String getNewFileName(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        return newFileName;
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        String newFileName = getNewFileName(file);
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        createFile(file, fileDirectory, destFile);
        Thumbnails.of(destFile).size(Constant.IMAGE_SIZE, Constant.IMAGE_SIZE)
                .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(Constant.FILE_UPLOAD_DIR + Constant.WATER_MARK_JPG)), Constant.IMAGE_OPACITY)
                .toFile(new File(Constant.FILE_UPLOAD_DIR+newFileName));
        String address = uri;
        String result = "http://"+address+"/images/"+newFileName;
        return result;
    }
}
