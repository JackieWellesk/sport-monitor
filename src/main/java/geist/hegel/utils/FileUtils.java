package geist.hegel.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class FileUtils {

    public static void deleteFile(String fileName) {
        String projectDir = System.getProperty("user.dir");
        String uploadDir = projectDir + File.separator + "upload";
        File upload = new File(uploadDir);
        File origin = new File(upload.getAbsolutePath()+File.separator+fileName);
        origin.delete();
    }

    public static String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileExt = "";
        if (file.getOriginalFilename() != null) {
            fileExt = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        }
        String projectDir = System.getProperty("user.dir");
        String uploadDir = projectDir + File.separator + "upload";
        File upload = new File(uploadDir);
        if(!upload.exists()) {
            upload.mkdirs();
        }
        String fileName = UUID.randomUUID()+"."+fileExt;
        File dest = new File(upload.getAbsolutePath()+File.separator+fileName);
        file.transferTo(dest);
        return fileName;
    }
}
