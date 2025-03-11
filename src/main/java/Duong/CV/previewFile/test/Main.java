package Duong.CV.previewFile.test;

import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
public class Main {
    public static void main(String[] args) throws IOException {
//        Workbook workbook = new Workbook();
//        workbook.loadFromFile("C:\\Users\\Admin\\Downloads\\tests-example.xlsx");
//        Worksheet sheet = workbook.getWorksheets().get(2);
//        BufferedImage bufferedImage = sheet.toImage(1, 1, 60, 20);
//        ImageIO.write(bufferedImage,"PNG",new File("output.png"));


        String input = "19999999999999999999999999999999999999999999999999999999999999999989999999999999999999999923";
        if (input.matches("^-?\\d+(,\\d+)?$") && input.length() <= 100) {
            System.out.println("Valid input");
        } else {
            System.out.println("Invalid input");
        }
    }
}

