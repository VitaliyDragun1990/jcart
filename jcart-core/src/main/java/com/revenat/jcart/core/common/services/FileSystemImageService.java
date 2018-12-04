package com.revenat.jcart.core.common.services;

import com.revenat.jcart.core.common.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("!test")
@Transactional
public class FileSystemImageService implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public void saveImage(String imageName, byte[] imageContent) {
        imageRepository.saveImage(imageContent, imageName);
    }

    @Override
    public byte[] loadImage(String imageName) {
        return imageRepository.loadImage(imageName);
    }
}
