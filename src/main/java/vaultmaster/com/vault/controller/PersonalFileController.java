package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import vaultmaster.com.vault.model.PersonalFile;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.service.S3Service;
import vaultmaster.com.vault.service.PersonalFileService;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/files")
public class PersonalFileController {

    private final S3Service s3Service;
    private final PersonalFileService personalFileService;
    private final JwtService jwtService;

    @Autowired
    public PersonalFileController(S3Service s3Service, PersonalFileService personalFileService, JwtService jwtService) {
        this.s3Service = s3Service;
        this.personalFileService = personalFileService;
        this.jwtService = jwtService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folderId", required = false) UUID folderId,
            HttpServletRequest request
    ) throws IOException {
        final List<String> disallowedExtensions = List.of("exe", "bat", "sh", "js", "jar", "msi", "cmd", "scr", "com");
        final long maxSize = 10 * 1024 * 1024; // 10MB
        final int maxFiles = 10;

        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));

        if (files.size() > maxFiles) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "You can upload up to " + maxFiles + " files at a time."));
        }

        List<Map<String, String>> uploaded = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            }

            // Validation checks
            if (disallowedExtensions.contains(extension)) {
                skipped.add(originalFilename + " (disallowed type)");
                continue;
            }

            if (file.getSize() > maxSize) {
                skipped.add(originalFilename + " (too large)");
                continue;
            }

            try {
                // Upload to S3 and save metadata
                PersonalFile savedFile = personalFileService.uploadPersonalFile(file, userId, folderId);

                uploaded.add(Map.of(
                        "filename", savedFile.getOriginalFilename(),
                        "fileId", savedFile.getFileId().toString(),
                        "key", savedFile.getFileKey()
                ));
            } catch (Exception e) {
                skipped.add(originalFilename + " (upload error: " + e.getMessage() + ")");
                e.printStackTrace(); // Optional: log to logger instead in production
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("uploaded", uploaded);
        response.put("skipped", skipped);
        response.put("message", "Upload completed with " + uploaded.size() + " file(s) and " + skipped.size() + " skipped.");

        return ResponseEntity.ok(response);
    }




    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
        try {
            byte[] data = s3Service.downloadFile(key);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFileById(@PathVariable UUID fileId) {
        try {
            personalFileService.deletePersonalFile(fileId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete file."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PersonalFile>> getAllFilesForUser(HttpServletRequest request) {
        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        List<PersonalFile> files = personalFileService.getFilesByUser(userId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<PersonalFile>> getFilesByFolder(@PathVariable UUID folderId) {
        List<PersonalFile> files = personalFileService.getFilesByFolder(folderId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<PersonalFile> getFileById(@PathVariable UUID fileId) {
        return ResponseEntity.ok(personalFileService.getFileById(fileId));
    }
}
