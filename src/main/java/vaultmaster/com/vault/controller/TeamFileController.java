package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vaultmaster.com.vault.model.TeamFile;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.service.S3Service;
import vaultmaster.com.vault.service.TeamFileService;
import vaultmaster.com.vault.security.PermissionChecker;

import java.util.*;

@RestController
@RequestMapping("/api/team-files")
public class TeamFileController {

    private final TeamFileService teamFileService;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final PermissionChecker permissionChecker;

    public TeamFileController(
            TeamFileService teamFileService,
            JwtService jwtService,
            S3Service s3Service,
            PermissionChecker permissionChecker
    ) {
        this.teamFileService = teamFileService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
        this.permissionChecker = permissionChecker;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTeamFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folderId", required = false) UUID folderId,
            @RequestParam("teamId") UUID teamId,
            HttpServletRequest request
    ) {
        final List<String> disallowedExtensions = List.of("exe", "bat", "sh", "js", "jar", "msi", "cmd", "scr", "com");
        final long maxSize = 10 * 1024 * 1024;
        final int maxFiles = 10;

        UUID uploaderId = UUID.fromString(jwtService.getAuthenticatedUserId(request));

        if (!permissionChecker.userHasPermission(uploaderId, teamId, "MANAGE_TEAM_FILES")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
        }

        if (files.size() > maxFiles) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "You can upload up to " + maxFiles + " files at a time."));
        }

        List<Map<String, String>> uploaded = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase() : "";

            if (disallowedExtensions.contains(extension)) {
                skipped.add(originalFilename + " (disallowed type)");
                continue;
            }

            if (file.getSize() > maxSize) {
                skipped.add(originalFilename + " (too large)");
                continue;
            }

            try {
                TeamFile saved = teamFileService.uploadTeamFile(file, teamId, uploaderId, folderId);
                uploaded.add(Map.of(
                        "filename", saved.getOriginalFilename(),
                        "fileId", saved.getFileId().toString(),
                        "key", saved.getFileKey()
                ));
            } catch (Exception e) {
                skipped.add(originalFilename + " (upload error: " + e.getMessage() + ")");
            }
        }

        return ResponseEntity.ok(Map.of(
                "uploaded", uploaded,
                "skipped", skipped,
                "message", "Upload completed with " + uploaded.size() + " file(s) and " + skipped.size() + " skipped."
        ));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getFilesByTeam(@PathVariable UUID teamId) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, teamId, "FILE_VIEW")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            List<TeamFile> files = teamFileService.getFilesByTeamId(teamId);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch team files.");
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getTeamFileById(@PathVariable UUID fileId) {
        try {
            Optional<TeamFile> fileOptional = teamFileService.getFileById(fileId);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            TeamFile file = fileOptional.get();
            UUID userId = permissionChecker.getCurrentUserId();
            if (!permissionChecker.userHasItemPermission(userId, file.getTeamId(), fileId, "file", "FILE_VIEW")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }

            return ResponseEntity.ok(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving file.");
        }
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable UUID fileId) {
        try {
            Optional<TeamFile> fileOptional = teamFileService.getFileById(fileId);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            TeamFile file = fileOptional.get();
            UUID userId = permissionChecker.getCurrentUserId();
            if (!permissionChecker.userHasItemPermission(userId, file.getTeamId(), fileId, "file", "DOWNLOAD_TEAM_FILE")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }

            byte[] fileBytes = s3Service.downloadFile(file.getFileKey());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                    .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file.");
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteTeamFile(@PathVariable UUID fileId) {
        try {
            Optional<TeamFile> fileOptional = teamFileService.getFileById(fileId);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            TeamFile file = fileOptional.get();
            UUID userId = permissionChecker.getCurrentUserId();

            boolean allowed = permissionChecker.hasEffectivePermission(
                    userId,
                    file.getTeamId(),
                    file.getFileId(),
                    file.getFolderId(),
                    "file",
                    "MANAGE_TEAM_FILES"
            );

            if (!allowed) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }

            s3Service.deleteFile(file.getFileKey());
            teamFileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed.");
        }
    }


    @PutMapping("/{fileId}/move-folder")
    public ResponseEntity<?> moveFileToFolder(
            @PathVariable UUID fileId,
            @RequestParam(required = false) UUID folderId
    ) {
        try {
            Optional<TeamFile> fileOptional = teamFileService.getFileById(fileId);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            TeamFile file = fileOptional.get();
            UUID userId = permissionChecker.getCurrentUserId();
            if (!permissionChecker.userHasItemPermission(userId, file.getTeamId(), fileId, "file", "MANAGE_TEAM_FILES")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }

            boolean moved = teamFileService.moveFileToFolder(fileId, folderId);
            return moved ? ResponseEntity.ok("File moved successfully.") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move file.");
        }
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<?> getFilesByFolder(@PathVariable UUID folderId) {
        try {
            UUID userId = permissionChecker.getCurrentUserId();
            UUID teamId = teamFileService.getTeamIdByFolderId(folderId);

            boolean hasPermission = permissionChecker.hasEffectivePermission(
                    userId, teamId, null, folderId, "file", "FILE_VIEW"
            );

            if (!hasPermission) {
                return ResponseEntity.status(403).body("Access denied.");
            }

            return ResponseEntity.ok(teamFileService.getFilesByFolderId(folderId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving files.");
        }
    }

}
