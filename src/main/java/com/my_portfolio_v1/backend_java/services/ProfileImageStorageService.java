package com.my_portfolio_v1.backend_java.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfileImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final int MAX_IMAGE_WIDTH = 5000;
    private static final int MAX_IMAGE_HEIGHT = 5000;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    private final Path profileImagesRoot;

    public ProfileImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        try {
            this.profileImagesRoot = Paths.get(uploadDir, "profile-images")
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(this.profileImagesRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize upload directory.", exception);
        }
    }

    public String storeProfileImage(MultipartFile file) {
        validateFile(file);

        BufferedImage image = readImage(file);
        String format = resolveFormat(file.getContentType());
        String extension = "png".equals(format) ? ".png" : ".jpg";

        String fileName = "profile-" + UUID.randomUUID() + extension;
        Path targetFile = profileImagesRoot.resolve(fileName).normalize();

        if (!targetFile.startsWith(profileImagesRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path.");
        }

        try (OutputStream outputStream = Files.newOutputStream(targetFile)) {
            boolean written = ImageIO.write(image, format, outputStream);

            if (!written) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unable to process image format."
                );
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to store image right now."
            );
        }

        return "/uploads/profile-images/" + fileName;
    }

    public void deleteManagedProfileImage(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String marker = "/uploads/profile-images/";
        int markerIndex = fileUrl.indexOf(marker);

        if (markerIndex < 0) {
            return;
        }

        String fileName = fileUrl.substring(markerIndex + marker.length());
        int queryIndex = fileName.indexOf('?');

        if (queryIndex >= 0) {
            fileName = fileName.substring(0, queryIndex);
        }

        if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            return;
        }

        Path targetFile = profileImagesRoot.resolve(fileName).normalize();
        if (!targetFile.startsWith(profileImagesRoot)) {
            return;
        }

        try {
            Files.deleteIfExists(targetFile);
        } catch (IOException ignored) {
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please choose an image file.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image size must be 5MB or less.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG and PNG images are allowed."
            );
        }
    }

    private BufferedImage readImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Uploaded file is not a valid image."
                );
            }

            if (image.getWidth() > MAX_IMAGE_WIDTH || image.getHeight() > MAX_IMAGE_HEIGHT) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Image dimensions are too large."
                );
            }

            return image;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uploaded file is not a readable image."
            );
        }
    }

    private String resolveFormat(String contentType) {
        if ("image/png".equalsIgnoreCase(contentType)) {
            return "png";
        }

        return "jpg";
    }
}
