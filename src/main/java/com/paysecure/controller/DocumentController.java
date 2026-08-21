package com.paysecure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * FINDING 7 (Part G) — REMEDIATED.
 * - The client-supplied filename is NEVER used to build a path; a random
 *   server-generated name is used instead, so ../ sequences have nothing to act on.
 * - A basic content-type allow-list rejects non-PDF uploads.
 * - A size cap prevents unbounded uploads.
 * - The absolute resolved path is verified to still be inside the intended
 *   storage folder before writing, as defense in depth.
 * Mohammad Ismail CJ-1134-C
 */
@Controller
public class DocumentController {

    private static final long MAX_UPLOAD_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final Path STORAGE_ROOT =
            Paths.get("src/main/resources/static/customer-files/").toAbsolutePath().normalize();

    @PostMapping("/documents/upload")
    @ResponseBody
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "Upload rejected: empty file.";
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            return "Upload rejected: file exceeds the 5 MB limit.";
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return "Upload rejected: only PDF files are accepted (detected type: " + contentType + ").";
        }

        // Server-generated filename - the client's claimed name is never used for the path.
        String safeName = UUID.randomUUID() + ".pdf";
        Path destination = STORAGE_ROOT.resolve(safeName).normalize();

        // Defense in depth: even with a generated name, verify we're still inside STORAGE_ROOT.
        if (!destination.startsWith(STORAGE_ROOT)) {
            throw new SecurityException("Path traversal attempt detected.");
        }

        Files.createDirectories(STORAGE_ROOT);
        file.transferTo(destination.toFile());

        return "Upload complete. Stored as: " + safeName;
    }
}