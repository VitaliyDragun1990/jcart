package com.revenat.jcart.core.common.repositories;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.exceptions.JCartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class FileSystemImageRepositoryTest {
    private static final byte[] IMAGE_CONTENT = new byte[] {1, 2, 0, 0, 0, 25};
    private static final byte[] EMPTY_CONTENT = new byte[0];
    private static final String IMAGE_NAME = "dummy.jpg";
    private static final String WRONG_IMAGE_NAME = "unknown.jpg";
    private static final String WRONG_IMAGE_DIRECTORY = "/dummy/";

    @Autowired
    private FileSystemImageRepository repository;


    @Test
    public void saveImage_ContentPresent_ImageSaved() {
        repository.saveImage(IMAGE_CONTENT, IMAGE_NAME);

        byte[] imageFromDisk = repository.loadImage(IMAGE_NAME);

        assertThat("Image content should be equal to that was saved.", imageFromDisk, equalTo(IMAGE_CONTENT));
    }

    @Test
    public void saveImage_EmptyContent_ImageNotSaved() {
        String emptyImageName = "test.jpg";
        repository.saveImage(EMPTY_CONTENT, emptyImageName);

        byte[] imageFromDisk = repository.loadImage(emptyImageName);

        assertThat("Image content should be empty.", imageFromDisk.length, equalTo(0));
    }

    @Test
    public void loadImage_WrongName_EmptyContentReturned() {
        byte[] imageFromDisk = repository.loadImage(WRONG_IMAGE_NAME);

        assertThat("For wrong image name content should be empty.", imageFromDisk.length, equalTo(0));
    }

    @Test(expected = JCartException.class)
    public void saveImage_WrongImageDirectory_ExceptionThrown() {
        String oldImagesDir = repository.getImagesDir();
        repository.setImagesDir(WRONG_IMAGE_DIRECTORY);

        try {
            repository.saveImage(IMAGE_CONTENT, IMAGE_NAME);
        } finally {
            repository.setImagesDir(oldImagesDir);
        }
    }
}