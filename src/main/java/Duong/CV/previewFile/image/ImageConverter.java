package Duong.CV.previewFile.image;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Set;

public class ImageConverter {
    private static final Set<String> IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/bmp",
            "image/gif",
            "image/heif",
            "image/heic",
            "image/tiff",
            "image/svg+xml",
            "image/webp"
    );

    public static void main(String[] args) {


        try {
            String inputFilePath = "C:\\Users\\Admin\\Downloads\\pexels-sora-shimazaki-5668876.heif";
            String outputFilePath = "D:\\intern-8seneca\\Task-intern\\GIS\\Research\\output.jpeg";

            String extension = getFileExtension(inputFilePath);

            switch (extension.toLowerCase()) {
                case "svg" -> processSVGImage(new FileInputStream(inputFilePath), outputFilePath);
                case "webp" -> processWebP(inputFilePath, outputFilePath);
                case "jpg", "jpeg", "png", "bmp", "gif", "heif", "heic", "tiff" ->
                        processImage(inputFilePath, outputFilePath);
                default -> System.err.println("Định dạng không được hỗ trợ: " + extension);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        return dotIndex == -1 ? "" : filePath.substring(dotIndex + 1);
    }

    private static void processSVGImage(InputStream inputStream, String filePath) throws Exception {
        byte[] fileDataBytes = inputStream.readAllBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileDataBytes);

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        org.w3c.dom.Document svgDocument = factory.createDocument("", byteArrayInputStream);
        Element root = svgDocument.getDocumentElement();

        float width, height;
        String viewBox = root.getAttribute("viewBox");

        if (!viewBox.isEmpty()) {
            String[] values = viewBox.split("\\s+");
            width = Float.parseFloat(values[2]);
            height = Float.parseFloat(values[3]);
        } else {
            width = Float.parseFloat(root.getAttribute("width").replaceAll("[^\\d.]", ""));
            height = Float.parseFloat(root.getAttribute("height").replaceAll("[^\\d.]", ""));
        }

        // Convert SVG -> PNG (hoặc bất kỳ định dạng BufferedImage nào)
        Transcoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            transcoder.transcode(new TranscoderInput(svgDocument), new TranscoderOutput(outputStream));
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));

            resizeAndSaveImage(originalImage, filePath);
        }
    }


    private static void processWebP(String inputFilePath, String outputFilePath) throws Exception {
        System.out.println("Xử lý WebP...");
        File webpFile = new File(inputFilePath);

        try (ImageInputStream input = ImageIO.createImageInputStream(webpFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                System.err.println("Không tìm thấy trình đọc WebP.");
                return;
            }

            ImageReader reader = readers.next();
            reader.setInput(input);
            BufferedImage webpImage = reader.read(0);

            resizeAndSaveImage(webpImage, outputFilePath);
            System.out.println("Chuyển đổi WebP thành công!");
        }
    }

    private static void processImage(String inputFilePath, String outputFilePath) throws Exception {
        System.out.println("Xử lý hình ảnh...");
        File inputFile = new File(inputFilePath);
        BufferedImage originalImage = ImageIO.read(inputFile);

        resizeAndSaveImage(originalImage, outputFilePath);
        System.out.println("Chuyển đổi hình ảnh thành công!");
    }

    private static void resizeAndSaveImage(BufferedImage originalImage, String outputFilePath) throws Exception {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        int newHeight = 256;
        int newWidth = (int) (256.0 * width / height);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
        graphics2D.dispose();

        FileOutputStream output = new FileOutputStream(outputFilePath);
        ByteArrayOutputStream jpegOutputStream = convertToJpegOutputStream(resizedImage, 0.85f);
//        ImageIO.write(resizedImage, "jpeg", output);
        output.write(jpegOutputStream.toByteArray());
    }

    private static ByteArrayOutputStream convertToJpegOutputStream(BufferedImage image, float quality) throws IOException {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        writer.write(null, new IIOImage(rgbImage, null, null), param);
        ios.close();
        writer.dispose();

        return baos;
    }
}
