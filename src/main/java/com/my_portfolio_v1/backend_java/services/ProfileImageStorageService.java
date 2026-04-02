package com.my_portfolio_v1.backend_java.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final int MAX_IMAGE_WIDTH = 5000;
    private static final int MAX_IMAGE_HEIGHT = 5000;
    private static final String CLOUDINARY_FOLDER = "portfolio/profile-images";

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    private final Cloudinary cloudinary;

    public String storeProfileImage(MultipartFile file) {
        validateFile(file);

        byte[] imageBytes = readAndValidateImageBytes(file);
        String publicId = "profile-" + UUID.randomUUID();

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "folder", CLOUDINARY_FOLDER,
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true,
                            "unique_filename", false,
                            "use_filename", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Cloud image URL was not returned."
                );
            }

            return secureUrl.toString();
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to upload image right now."
            );
        }
    }

    public void deleteManagedProfileImage(String fileUrl) {
        String publicId = extractManagedPublicId(fileUrl);
        if (publicId == null) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );
        } catch (Exception ignored) {
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

    private byte[] readAndValidateImageBytes(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

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

            return bytes;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uploaded file is not a readable image."
            );
        }
    }

    private String extractManagedPublicId(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }

        if (!fileUrl.contains("/res.cloudinary.com/") || !fileUrl.contains("/image/upload/")) {
            return null;
        }

        int uploadIndex = fileUrl.indexOf("/image/upload/");
        if (uploadIndex < 0) {
            return null;
        }

        String path = fileUrl.substring(uploadIndex + "/image/upload/".length());

        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            path = path.substring(0, queryIndex);
        }

        if (path.startsWith("v")) {
            int slashAfterVersion = path.indexOf('/');
            if (slashAfterVersion > -1) {
                path = path.substring(slashAfterVersion + 1);
            }
        }

        if (!path.startsWith(CLOUDINARY_FOLDER + "/")) {
            return null;
        }

        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex > 0) {
            path = path.substring(0, extensionIndex);
        }

        return path;
    }
}
