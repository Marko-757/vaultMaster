package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.TeamFile;
import vaultmaster.com.vault.service.TeamFileService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-files")
public class TeamFileController {

    private final TeamFileService teamFileService;

    public TeamFileController(TeamFileService teamFileService) {
        this.teamFileService = teamFileService;
    }

    @PostMapping
    public ResponseEntity<?> uploadTeamFile(@RequestBody TeamFile file, @RequestParam UUID uploaderId) {
        try {
            TeamFile uploaded = teamFileService.uploadFile(file, uploaderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getTeamFileById(@PathVariable UUID fileId) {
        try {
            Optional<TeamFile> file = teamFileService.getFileById(fileId);
            if (file.isPresent()) {
                return ResponseEntity.ok(file.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving file.");
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamFile>> getFilesByTeamId(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamFileService.getFilesByTeamId(teamId));
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<TeamFile>> getFilesByFolderId(@PathVariable UUID folderId) {
        return ResponseEntity.ok(teamFileService.getFilesByFolderId(folderId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteTeamFile(@PathVariable UUID fileId) {
        try {
            teamFileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed.");
        }
    }
}
