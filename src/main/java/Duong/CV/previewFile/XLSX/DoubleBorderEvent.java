package Duong.CV.previewFile.XLSX;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfContentByte;

class DoubleBorderEvent implements PdfPCellEvent {
    private final BaseColor color;
    private final float width;

    public DoubleBorderEvent(BaseColor color, float width) {
        this.color = color;
        this.width = width;
    }

    @Override
    public void cellLayout(PdfPCell cell, com.itextpdf.text.Rectangle rect, PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
        canvas.setColorStroke(color);
        canvas.setLineWidth(width);

        // Draw the outer rectangle
        canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        canvas.stroke();

        // Draw the inner rectangle for the double border effect
        float inset = width + 1;  // Adjusted inset to ensure inner border is distinct
        canvas.rectangle(rect.getLeft() + inset, rect.getBottom() + inset, rect.getWidth() - 2 * inset, rect.getHeight() - 2 * inset);
        canvas.stroke();
    }
}
