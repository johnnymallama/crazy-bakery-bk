package uan.edu.co.crazy_bakery.application.services.storage;

import java.io.IOException;

public interface StorageService {
    String uploadBytes(byte[] data, String fileName, String contentType) throws IOException;

    String moveFile(String sourceFileName, String destFileName) throws IOException;
}
