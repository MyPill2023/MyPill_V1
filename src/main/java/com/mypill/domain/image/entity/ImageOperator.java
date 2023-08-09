package com.mypill.domain.image.entity;

public interface ImageOperator {
    String getFolderName();

    Image getImage();

    void addImage(Image image);

}
