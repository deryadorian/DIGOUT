package com.digout.utils;

import com.digout.model.common.ImageFormat;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ImageUtils {

    public static OutputStream resizeTo(final byte[] bytes, final String outDir, final String outFilename, final ImageFormat imageFormat)
            throws IOException {
        File dir = new File(outDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(dir.getAbsolutePath() + File.separator + outFilename);
        BufferedImage img = Scalr.resize(ImageIO.read(new ByteArrayInputStream(bytes)), Scalr.Method.QUALITY,
                Scalr.Mode.FIT_EXACT, imageFormat.getWidth(), imageFormat.getHeight());
        ImageIO.write(img, "JPEG", out);
        return out;
    }

    public static OutputStream resizeToOriginal(final byte[] bytes, final String outDir, final String outFilename) throws IOException {
        return resizeTo(bytes, outDir, outFilename, ImageFormat.ORIGINAL);
    }

    public static OutputStream resizeToStandard(final byte[] bytes, final String outDir, final String outFilename) throws IOException {
        return resizeTo(bytes, outDir, outFilename, ImageFormat.STANDARD);
    }

    public static OutputStream resizeToThumbnail(final byte[] bytes, final String outDir, final String outFilename) throws IOException {
        return resizeTo(bytes, outDir, outFilename, ImageFormat.THUMB);
    }

    private ImageUtils() {
    }
}
