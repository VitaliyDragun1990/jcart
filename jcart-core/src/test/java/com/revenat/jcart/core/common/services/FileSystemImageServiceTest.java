package com.revenat.jcart.core.common.services;

import com.revenat.jcart.core.common.repositories.ImageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemImageServiceTest {

    private static final String IMAGE_NAME = "dummy.jpg";
    private static final byte[] IMAGE_CONTENT = {1};

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private FileSystemImageService imageService;

    @Test
    public void testSaveImage() {
        imageService.saveImage(IMAGE_NAME, IMAGE_CONTENT);

        verify(imageRepository, times(1)).saveImage(IMAGE_CONTENT, IMAGE_NAME);
    }

    @Test
    public void testLoadImage() {
        when(imageRepository.loadImage(IMAGE_NAME)).thenReturn(IMAGE_CONTENT);

        byte[] image = imageService.loadImage(IMAGE_NAME);

        assertThat(image, equalTo(IMAGE_CONTENT));
        verify(imageRepository, times(1)).loadImage(IMAGE_NAME);
    }
}