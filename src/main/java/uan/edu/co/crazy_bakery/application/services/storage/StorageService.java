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

    /**
     * Uploads raw bytes to storage.
     *
     * @param data        The file content as a byte array.
     * @param fileName    The desired path/name for the file in storage.
     * @param contentType The MIME type of the file (e.g. "application/pdf").
     * @return The public URL of the uploaded file.
     * @throws IOException If an error occurs during upload.
     */
    String uploadBytes(byte[] data, String fileName, String contentType) throws IOException;
}
