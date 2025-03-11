package Duong.CV.previewFile.XLSX;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

public class LinkInCellEvent implements PdfPCellEvent {
    private String url;

    public LinkInCellEvent(String url) {
        this.url = url;
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvases) {
        PdfWriter writer = canvases[0].getPdfWriter();
        PdfAnnotation link = PdfAnnotation.createLink(
                writer,
                rect,
                PdfAnnotation.HIGHLIGHT_INVERT,
                new PdfAction(url)
        );
        writer.addAnnotation(link);
    }
}
