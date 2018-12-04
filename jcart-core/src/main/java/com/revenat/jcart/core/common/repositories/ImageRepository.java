package com.revenat.jcart.core.common.repositories;

public interface ImageRepository {

    void saveImage(byte[] imageContent, String imageName);

    byte[] loadImage(String imageName);
}
