package Duong.CV.previewFile.image;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageMain {
    public static void main(String[] args) {
        try {
            File input = new File("C:\\Users\\Admin\\Downloads\\pexels-sora-shimazaki-5668876.heic");
            BufferedImage originalImage = ImageIO.read(input);

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();



            int newHeight = (int)(500.0*height/width);
            BufferedImage resizedImage = new BufferedImage(500, newHeight, BufferedImage.TYPE_INT_ARGB);



            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
            graphics2D.dispose();

            File output = new File("output.png");
            ImageIO.write(resizedImage, "png", output);
            System.out.println("Resize thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
