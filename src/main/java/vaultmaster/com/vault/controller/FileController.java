package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import vaultmaster.com.vault.service.S3Service;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String key = s3Service.uploadFile(bytes, file.getOriginalFilename());
        return ResponseEntity.ok(
                Map.of(
                        "message", "File uploaded successfully",
                        "key", key
                )
        );
    }
}
