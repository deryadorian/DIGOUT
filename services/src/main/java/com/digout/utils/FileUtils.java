package com.digout.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.digout.exception.ApplicationException;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public final class FileUtils {

    private static final String FILENAME_PATTERN = "[^/?*:;{}\\]+\\.[^/?*:;{}\\]+";

    public static boolean deleteQuietly(final String path) {
        File file = new File(path);
        return org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    public static String generateUniqueFileName(final String fileName) {
        String fileExtention = Files.getFileExtension(fileName);
        fileExtention = Strings.isNullOrEmpty(fileExtention) ? ".jpeg" : "." + fileExtention;
        return new StringBuilder(SecureUtils.generateSecureId()).append(fileExtention).toString();
    }

    public static FileItem getFile(final HttpServletRequest request) throws ApplicationException {
        if (ServletFileUpload.isMultipartContent(request)) {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            try {
                List<?> items = upload.parseRequest(request);
                Iterator<?> iterator = items.iterator();
                if (iterator.hasNext()) {
                    return (FileItem) iterator.next();
                } else {
                    throw new FileUploadException("No file uploaded");
                }
            } catch (Exception e) {
                throw new ApplicationException("File upload failed", e);
            }
        }
        return null;
    }

    private FileUtils() {
    }

}
