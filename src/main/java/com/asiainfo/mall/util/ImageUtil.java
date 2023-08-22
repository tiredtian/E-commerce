package com.asiainfo.mall.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;

public class ImageUtil {
    public static void main(String[] args) throws IOException {
        String path = "/Users/tianziping/Desktop/";
        //裁剪
        Thumbnails.of(path + "like.jpg").sourceRegion(Positions.BOTTOM_RIGHT,50,50).size(200,200)
                .toFile(path+"fix.jpg");


        //缩放
        Thumbnails.of(path+"like.jpg").scale(0.7).toFile(path+"scale1.jpg");
        Thumbnails.of(path + "like.jpg").scale(1.5).toFile(path + "scale2.jpg");
        Thumbnails.of(path+"like.jpg").size(500,500).keepAspectRatio(false)
                .toFile(path+"size1.jpg");
        Thumbnails.of(path+"like.jpg").size(500,500).keepAspectRatio(true)
                .toFile(path+"size2.jpg");
        Thumbnails.of(path+"like.jpg").rotate(90).size(500,500).keepAspectRatio(false)
                .toFile(path+"rotate1.jpg");
        Thumbnails.of(path+"like.jpg").rotate(180).size(500,500).keepAspectRatio(true)
                .toFile(path+"rotate2.jpg");
    }
}
