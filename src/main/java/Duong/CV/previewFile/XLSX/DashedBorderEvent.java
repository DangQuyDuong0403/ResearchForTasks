package Duong.CV.previewFile.XLSX;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfContentByte;

public class DashedBorderEvent implements PdfPCellEvent {
    private final BaseColor color;
    private final float width;
    private final float dashLength;
    private final float gapLength;

    public DashedBorderEvent(BaseColor color, float width, float dashLength, float gapLength) {
        this.color = color;
        this.width = width;
        this.dashLength = dashLength;
        this.gapLength = gapLength;
    }

    @Override
    public void cellLayout(PdfPCell cell, com.itextpdf.text.Rectangle rect, PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
        canvas.setLineDash(dashLength, gapLength);
        canvas.setColorStroke(color);
        canvas.setLineWidth(width);
        canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        canvas.stroke();
    }
}
