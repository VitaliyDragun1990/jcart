package com.revenat.jcart.core.common.repositories;

import com.revenat.jcart.core.exceptions.JCartException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class FileSystemImageRepository implements ImageRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemImageRepository.class);

    @Value("${images.dir}")
    private String imagesDir;

    void setImagesDir(String imagesDir) {
        this.imagesDir = imagesDir;
    }

    String getImagesDir() {
        return imagesDir;
    }

    @Override
    public void saveImage(byte[] imageContent, String imageName) {
        if (imageContent != null && imageContent.length > 0) {
            String path = imagesDir + imageName;
            try  {
                writeToStream(path, imageContent);
            } catch (IOException e) {
                LOGGER.error("Error while saving image to disk.", e);
                throw new JCartException(e);
            }
        }
    }

    private void writeToStream(String path, byte[] data) throws IOException {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(path)))) {
            outputStream.write(data);
        }
    }

    @Override
    public byte[] loadImage(String imageName) {
        String path = imagesDir + imageName;
        try {
            return loadFromStream(path);
        } catch (IOException e) {
            LOGGER.error("Error while reading image from disk.", e);
        }
        return new byte[0];
    }

    private byte[] loadFromStream(String path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            return out.toByteArray();
        }
    }
}
