package Duong.CV.previewFile.image;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Set;

public class LIDSTest {
    private static final Set<String> IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/bmp",
            "image/gif",
            "image/heif",
            "image/tiff",
            "image/svg+xml",
            "image/webp"
    );
    public static void main(String[] args) throws FileNotFoundException {
        String inputFilePath = "C:\\Users\\Admin\\Downloads\\man39_1727004869.svg";
        String mimeType = URLConnection.guessContentTypeFromName(inputFilePath);
        System.out.println(mimeType);


        // Mở file và tạo InputStream
        File inputFile = new File(inputFilePath);
        InputStream inputStream = new FileInputStream(inputFile);
        FileData x = generateImagePreview(inputFilePath, inputStream);
        System.out.println("q");
    }
    private static FileData generateImagePreview(String fileName, InputStream inputStream) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (mimeType == null) {
                throw new IllegalArgumentException("Cannot determine the MIME type of the file: " + fileName);
            }

            BufferedImage resizedImage = null;

            if (mimeType.equals("image/svg+xml")) {
                 // Process SVG
                String parser = XMLResourceDescriptor.getXMLParserClassName();
                SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
                org.w3c.dom.Document svgDocument = factory.createDocument("", inputStream);

                Element root = svgDocument.getDocumentElement();

                String viewBox = root.getAttribute("viewBox");
                float newHeight;
                float newWidth;
                if (!viewBox.isEmpty()) {
                    String[] values = viewBox.split(" ");
                    float originalWidth = Float.parseFloat(values[2]); // width từ viewBox
                    float originalHeight = Float.parseFloat(values[3]); // height từ viewBox


                    newHeight = 500;
                    newWidth = (500 * originalWidth / originalHeight);
                }else {
                    String widthStr = root.getAttribute("width").replaceAll("[^\\d.]", "");
                    String heightStr = root.getAttribute("height").replaceAll("[^\\d.]", "");

                    float width = Float.parseFloat(widthStr);
                    float height = Float.parseFloat(heightStr);

                    newHeight = 500;
                    newWidth = (500 * width / height);
                }


                Transcoder transcoder = new PNGTranscoder();
                transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, newWidth);
                transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, newHeight);

                TranscoderInput transcoderInput = new TranscoderInput(svgDocument);
                TranscoderOutput transcoderOutput = new TranscoderOutput(byteArrayOutputStream);

                transcoder.transcode(transcoderInput, transcoderOutput);

                InputStream pngInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                resizedImage = ImageIO.read(pngInputStream);

            } else if (mimeType.equals("image/webp")) {
                // Process WebP
                Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType("image/webp");
                if (!readers.hasNext()) {
                    throw new UnsupportedOperationException("No WebP reader available");
                }
                ImageReader reader = readers.next();
                reader.setInput(ImageIO.createImageInputStream(inputStream));
                BufferedImage webpImage = reader.read(0);
                resizedImage = resizeImage(webpImage);

            } else if (IMAGE_MIME_TYPES.contains(mimeType)) {
                // Process other image formats
                BufferedImage originalImage = ImageIO.read(inputStream);
                resizedImage = resizeImage(originalImage);
            } else {
                throw new UnsupportedOperationException("Unsupported image type: " + mimeType);
            }

            if (resizedImage != null) {
                File output = new File("output.png");
                ImageIO.write(resizedImage, "png", output);
            }

            return new FileData(fileName, "image/png", false, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newHeight = 500;
        int newWidth = (int) (500 * originalWidth / (float) originalHeight);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}

