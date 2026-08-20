package com.paysecure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * FINDING 7 (Part G): no filename sanitization, no type/size validation,
 * saved with client-controlled name into a statically-served directory.
 * Try filenames with ../ traversal, or a .jsp/.html filename.
 */
@Controller
public class DocumentController {

    @PostMapping("/documents/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        String directory = "src/main/resources/static/customer-files/";
        File destination = new File(directory + file.getOriginalFilename());
        file.transferTo(destination);
        return "upload-success";
    }
}
