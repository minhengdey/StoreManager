package com.example.backend.utils;

import com.example.backend.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

public class FileUtility {

    public static FileType getFileType (MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null) {
            return FileType.UNKNOWN;
        }
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("application/vnd.ms-excel")) {
            return FileType.EXCEL;
        }
        if (contentType.equals("text/csv") || contentType.equals("application/csv")) {
            return FileType.CSV;
        }
        return FileType.UNKNOWN;
    }
}
