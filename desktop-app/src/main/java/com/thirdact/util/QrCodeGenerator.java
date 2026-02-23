package com.thirdact.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility class to generate QR codes.
 */
public class QrCodeGenerator {

    /**
     * Generates a QR code for the given URL/text and returns it as a JavaFX Image.
     *
     * @param text   The text or URL to encode.
     * @param width  The width of the QR code image.
     * @param height The height of the QR code image.
     * @return The generated JavaFX Image, or null if generation fails.
     */
    public static Image generate(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Allow styling later with hints (like margin)
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            byte[] pngData = pngOutputStream.toByteArray();
            return new Image(new ByteArrayInputStream(pngData));

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
