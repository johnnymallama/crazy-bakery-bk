package uan.edu.co.crazy_bakery.application.services.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseStorageService implements StorageService {

    private final String bucketName;

    public FirebaseStorageService(@Value("${firebase.storage.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String uploadBytes(byte[] data, String fileName, String contentType) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            Blob blob = bucket.create(fileName, inputStream, contentType);
            return buildPublicUrl(blob.getName());
        }
    }

    @Override
    public String moveFile(String sourceFileName, String destFileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);
        Blob source = bucket.get(sourceFileName);
        if (source == null) {
            throw new IOException("Archivo no encontrado en storage: " + sourceFileName);
        }
        Storage storage = source.getStorage();
        storage.copy(Storage.CopyRequest.of(
                BlobId.of(bucketName, sourceFileName),
                BlobId.of(bucketName, destFileName)
        ));
        source.delete();
        return buildPublicUrl(destFileName);
    }

    private String buildPublicUrl(String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + encodedFileName + "?alt=media";
    }
}