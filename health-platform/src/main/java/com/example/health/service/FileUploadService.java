package com.example.health.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {
    
    // 文件存储根目录
    private static final String UPLOAD_DIR = "uploads";
    // 食谱图片子目录
    private static final String RECIPE_IMAGES_DIR = "recipes";
    
    /**
     * 上传食谱图片
     * @param file 上传的文件
     * @return 图片访问URL
     */
    public String uploadRecipeImage(MultipartFile file) throws IOException {
        // 验证文件类型
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("只支持 JPG、PNG 格式的图片");
        }
        
        // 验证文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过 2MB");
        }
        
        // 创建上传目录
        Path uploadPath = Paths.get(UPLOAD_DIR, RECIPE_IMAGES_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 返回访问URL
        return "/static/" + RECIPE_IMAGES_DIR + "/" + uniqueFilename;
    }
    
    /**
     * 删除食谱图片
     * @param imageUrl 图片URL
     */
    public void deleteRecipeImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        try {
            // 从URL中提取文件名
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(UPLOAD_DIR, RECIPE_IMAGES_DIR, filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (Exception e) {
            // 删除失败不影响主流程
            System.err.println("删除图片失败: " + imageUrl + ", 错误: " + e.getMessage());
        }
    }
    
    /**
     * 验证是否为有效的图片文件
     */
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/jpg"));
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return ".jpg";
        }
        return filename.substring(lastDotIndex);
    }
}

