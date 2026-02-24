package geist.hegel.controller;

import geist.hegel.common.R;
import geist.hegel.utils.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    @PostMapping("/image")
    public R<Object> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return R.ok("/upload/" + FileUtils.upload(file));
    }

}
