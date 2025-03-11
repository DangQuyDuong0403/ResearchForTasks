package Duong.CV.previewFile.image;



import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class WEBPMain {
    public static void main(String[] args) throws IOException {
        String webpFilePath = "C:\\Users\\Admin\\Downloads\\pexels-sora-shimazaki-5668876.webp";
        String pngFilePath = "D:\\intern-8seneca\\Task-intern\\GIS\\Research\\output.png";


        File webpFile = new File(webpFilePath);

        try (ImageInputStream input = ImageIO.createImageInputStream(webpFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                System.err.println("No WebP reader found. Please ensure the library is added.");
                return;
            }

            ImageReader reader = readers.next();
            reader.setInput(input);

            BufferedImage webpImage = reader.read(0); // Read the first image in case of animation

            int height = webpImage.getHeight();
            int width = webpImage.getWidth();

            int newHeight = (int)(500.0*height/width);
            BufferedImage resizedImage = new BufferedImage(500, newHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(webpImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
            graphics2D.dispose();

            File output = new File("output.png");
            ImageIO.write(resizedImage, "png", output);
            System.out.println("Resize thành công!");
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

