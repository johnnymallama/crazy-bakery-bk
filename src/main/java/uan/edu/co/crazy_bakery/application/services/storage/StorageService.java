package uan.edu.co.crazy_bakery.application.services.storage;

import java.io.IOException;

public interface StorageService {
    /**
     * Downloads a file from a given URL and uploads it to a storage service.
     *
     * @param imageUrl The URL of the image to download.
     * @param fileName The desired name for the file in the storage.
     * @return The public URL of the uploaded file.
     * @throws IOException If an error occurs during download or upload.
     */
    String uploadFileFromUrl(String imageUrl, String fileName) throws IOException;
}
