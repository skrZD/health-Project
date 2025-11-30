package com.example.health.web;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/files")
    public List<String> listUploadedFiles() {
        List<String> files = new ArrayList<>();
        try {
            Path uploadPath = Paths.get("uploads/recipes");
            if (Files.exists(uploadPath)) {
                Files.list(uploadPath).forEach(path -> {
                    files.add(path.getFileName().toString());
                });
            }
        } catch (Exception e) {
            files.add("Error: " + e.getMessage());
        }
        return files;
    }
    
    @GetMapping("/path")
    public String getUploadPath() {
        Path uploadPath = Paths.get("uploads").toAbsolutePath();
        return "Upload path: " + uploadPath.toString() + 
               "\nExists: " + Files.exists(uploadPath) +
               "\nRecipes path: " + uploadPath.resolve("recipes").toString() +
               "\nRecipes exists: " + Files.exists(uploadPath.resolve("recipes"));
    }
    
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads/recipes/" + filename);
            File imageFile = imagePath.toFile();
            
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(imageFile);
            
            // 根据文件扩展名设置正确的Content-Type
            String contentType = "image/jpeg";
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/debug")
    public Map<String, Object> debugInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            Path uploadPath = Paths.get("uploads").toAbsolutePath();
            Path recipesPath = uploadPath.resolve("recipes");
            
            info.put("uploadPath", uploadPath.toString());
            info.put("recipesPath", recipesPath.toString());
            info.put("uploadExists", Files.exists(uploadPath));
            info.put("recipesExists", Files.exists(recipesPath));
            info.put("canRead", Files.isReadable(recipesPath));
            info.put("canWrite", Files.isWritable(recipesPath));
            
            if (Files.exists(recipesPath)) {
                List<String> files = new ArrayList<>();
                Files.list(recipesPath).forEach(path -> {
                    files.add(path.getFileName().toString());
                });
                info.put("files", files);
            }
            
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}
