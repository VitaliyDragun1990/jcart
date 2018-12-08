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

    @Autowired
    private FileSystemImageRepository repository;


    @Test
    public void testSaveLoadImage_OK() {
        repository.saveImage(IMAGE_CONTENT, "dummy.jpg");

        byte[] imageFromDisk = repository.loadImage("dummy.jpg");

        assertThat(imageFromDisk, equalTo(IMAGE_CONTENT));
    }

    @Test
    public void testLoadImage_Fail_WrongName() {
        byte[] imageFromDisk = repository.loadImage("unknown.jpg");

        assertThat(imageFromDisk.length, equalTo(0));
    }

    @Test(expected = JCartException.class)
    public void testSaveImageToDisk_Fail_WrongDirectory() {
        String oldImagesDir = ((FileSystemImageRepository)repository).getImagesDir();
        ((FileSystemImageRepository)repository).setImagesDir("/dummy/");

        try {
            repository.saveImage(IMAGE_CONTENT, "dummy.jpg");
        } finally {
            ((FileSystemImageRepository)repository).setImagesDir(oldImagesDir);
        }
    }
}