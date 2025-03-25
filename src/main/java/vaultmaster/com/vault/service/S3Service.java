package vaultmaster.com.vault.service;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "vaultmaster-files";

    public S3Service() {
        DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        try {
            String accessKey = credentialsProvider.resolveCredentials().accessKeyId();
            System.out.println("[DEBUG] AWS Access Key ID found: " + accessKey);
        } catch (Exception e) {
            System.err.println("[ERROR] Could not resolve AWS credentials: " + e.getMessage());
        }

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public String uploadFile(byte[] fileBytes, String originalFilename) {
        try {
            String key = UUID.randomUUID() + "_" + originalFilename;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));
            return key;

        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public byte[] downloadFile(String key) throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(getRequest).asByteArray();
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
