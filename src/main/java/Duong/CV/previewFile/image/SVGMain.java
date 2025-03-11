package Duong.CV.previewFile.image;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SVGMain {
    public static void main(String[] args) {
        try {
            // Đường dẫn đến tệp SVG đầu vào
            String svgFile = "C:\\Users\\Admin\\Downloads\\pexels-sora-shimazaki-5668876.svg";
            // Đường dẫn đến tệp PNG đầu ra
            String pngFile = "D:\\intern-8seneca\\Task-intern\\GIS\\Research\\output.png";

            // Tạo đối tượng PNGTranscoder
            Transcoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);

            // Đọc tệp SVG với các xử lý bổ sung cho phần tử không hợp lệ
            InputStream svgStream = new FileInputStream(svgFile);

            // Cách khác để xử lý SVG phức tạp hơn
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            org.w3c.dom.Document doc = factory.createDocument("file:///" + svgFile);

            // Nếu tệp SVG chứa phần tử không hợp lệ, có thể bỏ qua hoặc xử lý theo cách riêng
            if (doc != null) {
                Element root = doc.getDocumentElement();
                String widthStr = root.getAttribute("width");
                String heightStr = root.getAttribute("height");

                // Loại bỏ các đơn vị không phải là số như "pt", "px"
                widthStr = widthStr.replaceAll("[^\\d.]", "");
                heightStr = heightStr.replaceAll("[^\\d.]", "");

                // Kiểm tra nếu có thông tin chiều rộng và chiều cao
                if (widthStr != null && heightStr != null) {
                    // Chuyển đổi chiều rộng và chiều cao sang kiểu số
                    float width = Float.parseFloat(widthStr);
                    float height = Float.parseFloat(heightStr);

                    float newWidth = 500;
                    float newHeight = (float)(500.0*height/width);

                    // Thiết lập kích thước cho transcoder output
                    transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (newWidth));
                    transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (newHeight));
                }

                // Tạo đối tượng TranscoderInput từ SVG đã đọc
                TranscoderInput input = new TranscoderInput(doc);

                // Tạo tệp PNG đầu ra
                TranscoderOutput output = new TranscoderOutput(new FileOutputStream(pngFile));

                // Chuyển đổi
                transcoder.transcode(input, output);

                // Đóng tệp
                output.getOutputStream().close();

                System.out.println("Chuyển đổi thành công: " + pngFile);
            } else {
                System.out.println("Không thể đọc SVG: " + svgFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
