package com.sabo.sabostorev2.Common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FileUtils {

    public static String getFilePathFromUri(Context context, Uri selectedFileUri) {
        String fileName = getFileName(selectedFileUri);
        File dir = new File(Environment.getExternalStorageDirectory() + "");
        if (!dir.exists())
            dir.mkdirs();
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(dir + File.separator + fileName);
            copy(context, selectedFileUri, copyFile);
            return copyFile.getAbsolutePath();
        }

        return null;
    }

    private static void copy(Context context, Uri selectedFileUri, File copyFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(selectedFileUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(copyFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        int BUFFER_SIZE = 1024 * 1024;
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(inputStream, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private static String getFileName(Uri selectedFileUri) {
        if (selectedFileUri == null) return null;
        String fileName = null;
        try {
            String path = selectedFileUri.getPath();
            fileName = path != null ? path.substring(path.lastIndexOf('/') + 1) : "unknown";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /** Get File Size */
    public static long getFileSizeFromUri(Context context, Uri selectedFileUri){
        String path = getFilePathFromUri(context, selectedFileUri);
        File fileSize = new File(path);
        return fileSize.length();
    }

    /** Get File Name */
    public static String getFileNameFromUri(Uri selectedFileUri) {
        if (selectedFileUri == null) return null;
        String fileName = null;
        try {
            String path = selectedFileUri.getPath();
            fileName = path != null ? path.substring(path.lastIndexOf('/') + 1) : "unknown";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }


}
