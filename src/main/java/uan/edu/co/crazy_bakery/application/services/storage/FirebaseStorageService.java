package uan.edu.co.crazy_bakery.application.services.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageService implements StorageService {

    private final String bucketName;

    public FirebaseStorageService(@Value("${firebase.storage.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFileFromUrl(String imageUrl, String fileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        // Setting a timeout to avoid blocking indefinitely
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (InputStream inputStream = connection.getInputStream()) {
            Blob blob = bucket.create(fileName, inputStream, connection.getContentType());

            // Return the public URL of the uploaded file, valid for 100 years
            return blob.signUrl(365 * 100, TimeUnit.DAYS).toString();
        }
    }
}
