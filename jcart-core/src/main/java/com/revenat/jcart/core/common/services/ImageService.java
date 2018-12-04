package com.revenat.jcart.core.common.services;

public interface ImageService {

    void saveImage(String imageName, byte[] imageContent);

    byte[] loadImage(String imageName);
}
