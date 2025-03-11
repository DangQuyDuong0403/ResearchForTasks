package Duong.CV.previewFile.XLSX;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import org.apache.poi.ss.usermodel.BorderStyle;

import static com.itextpdf.text.pdf.PdfPCell.*;

public class BorderStylesHandler {

    public static void applyBorderStyle(PdfPCell pdfCell, BorderStyle borderStyle, BaseColor borderColor, float borderWidth) {
        switch (borderStyle) {
            case THIN:
                setSolidBorder(pdfCell, borderColor, 0.5f);
                break;
            case MEDIUM:
                setSolidBorder(pdfCell, borderColor, 1.0f);
                break;
            case THICK:
                setSolidBorder(pdfCell, borderColor, 1.5f);
                break;
            case DASHED:
                setDashedBorder(pdfCell, borderColor, borderWidth, 4f, 2f);  // Adjusted for better visual dash effect
                break;
            case DOTTED:
                setDashedBorder(pdfCell, borderColor, borderWidth, 2f, 1f);  // Updated gap for more distinct dots
                break;
            case DASH_DOT:
                setDashedBorder(pdfCell, borderColor, borderWidth, 4f, 1f);  // Modified for clearer dash-dot effect
                break;
            case DASH_DOT_DOT:
                setDashedBorder(pdfCell, borderColor, borderWidth, 4f, 1f);  // Adjusted for a balanced look
                break;
            case DOUBLE:
                setDoubleBorder(pdfCell, borderColor, borderWidth);
                break;
            case HAIR:
                setSolidBorder(pdfCell, borderColor, 0.2f);  // Very thin line for 'hair' style
                break;
            case MEDIUM_DASH_DOT:
                setDashedBorder(pdfCell, borderColor, borderWidth, 6f, 3f);  // Custom pattern for medium dash-dot
                break;
            case MEDIUM_DASH_DOT_DOT:
                setDashedBorder(pdfCell, borderColor, borderWidth, 6f, 2f);  // Custom pattern for medium dash-dot-dot
                break;
            case MEDIUM_DASHED:
                setDashedBorder(pdfCell, borderColor, borderWidth, 5f, 3f);  // Custom pattern for medium dashed
                break;
            case SLANTED_DASH_DOT:
                setDashedBorder(pdfCell, borderColor, borderWidth, 6f, 2.5f);  // Custom pattern for slanted dash-dot
                break;
            default:
                pdfCell.disableBorderSide(LEFT | RIGHT | TOP | BOTTOM);
        }
    }


    private static void setSolidBorder(PdfPCell pdfCell, BaseColor color, float width) {
        pdfCell.setBorderColor(color);
        pdfCell.setBorderWidth(width);
    }

    private static void setDashedBorder(PdfPCell pdfCell, BaseColor color, float width, float dash, float gap) {
        pdfCell.setCellEvent(new DashedBorderEvent(color, width, dash, gap));
    }

    private static void setDoubleBorder(PdfPCell pdfCell, BaseColor color, float width) {
        pdfCell.setCellEvent(new DoubleBorderEvent(color, width));
    }
}

