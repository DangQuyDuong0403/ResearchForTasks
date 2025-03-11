package Duong.CV.previewFile.XLSX;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, DocumentException {
        FontFactory.registerDirectory("C:\\Windows\\Fonts");
        String excelFilePath = "D:\\intern-8seneca\\Task-intern\\GIS\\Research\\FCE73500.xlsx";
        String pdfFilePath = "D:\\intern-8seneca\\Task-intern\\GIS\\Research\\ConvertedPDF.pdf";
        convertExcelToPDF(excelFilePath, pdfFilePath);
    }

    public static void convertExcelToPDF(String excelFilePath, String pdfFilePath) throws IOException, DocumentException {
        try (FileInputStream excelFile = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(excelFile);
             FileOutputStream pdfFile = new FileOutputStream(pdfFilePath)) {


            Sheet sheet = workbook.getSheetAt(0);
            int numColumns = getNumColumns(sheet);

            double marginLeft = sheet.getMargin(PageMargin.LEFT);
            double marginRight = sheet.getMargin(PageMargin.RIGHT);
            double marginTop = sheet.getMargin(PageMargin.TOP);
            double marginBottom = sheet.getMargin(PageMargin.BOTTOM);

            float pdfMarginLeft = (float) (marginLeft * 72);
            float pdfMarginRight = (float) (marginRight * 72);
            float pdfMarginTop = (float) (marginTop * 72);
            float pdfMarginBottom = (float) (marginBottom * 72);

            Document document = new Document(PageSize.A3.rotate(), pdfMarginLeft, pdfMarginRight, pdfMarginTop, pdfMarginBottom);

            PdfWriter writer = PdfWriter.getInstance(document, pdfFile);
            document.open();


            // Create a PDF table with the same number of columns as the Excel sheet
            PdfPTable table = new PdfPTable(numColumns);
            table.setWidthPercentage(100); // Set table width to fill the page

            // Set column widths based on the Excel sheet's column widths
            setColumnWidths(table, sheet, numColumns);

            // Populate the PDF table with data from the Excel sheet
            populateTableWithData(sheet, table, numColumns, workbook);
            handleMergedRegions(sheet, table, workbook);
            processImages(sheet, document, writer, pdfMarginLeft, pdfMarginTop);

            document.add(table);
            document.close();
            System.out.println("PDF created successfully!");
        }
    }

    // Add merged regions to the PDF table
    private static void handleMergedRegions(Sheet sheet, PdfPTable table, Workbook workbook) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            int firstRow = mergedRegion.getFirstRow();
            int lastRow = mergedRegion.getLastRow();
            int firstCol = mergedRegion.getFirstColumn();
            int lastCol = mergedRegion.getLastColumn();

            // Calculate rowspan and colspan
            int rowspan = lastRow - firstRow + 1;
            int colspan = lastCol - firstCol + 1;

            // Add a merged cell
            Row row = sheet.getRow(firstRow);
            if (row != null) {
                Cell cell = row.getCell(firstCol);
                if (cell != null) {
                    String cellValue = getCellValue(cell);

                    PdfPCell pdfCell = new PdfPCell(new Phrase(cellValue));
                    pdfCell.setRowspan(rowspan);
                    pdfCell.setColspan(colspan);

                    // Add styles and alignment
                    CellStyle cellStyle = cell.getCellStyle();
                    setCellFontAttributes(pdfCell.getPhrase().getFont(), cellStyle, workbook);
                    setCellTextAlignment(pdfCell, cellStyle);

                    table.addCell(pdfCell);
                }
            }
        }
    }

    // Get cell value as a String
    private static String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }


    private static void processImages(Sheet sheet, Document document, PdfWriter writer, float pdfMarginLeft, float pdfMarginTop) throws IOException, DocumentException {
        Drawing<?> drawing = sheet.getDrawingPatriarch();
        if (drawing != null) {
            for (org.apache.poi.ss.usermodel.Shape shape : drawing) {
                if (shape instanceof Picture) {
                    Picture picture = (Picture) shape;
                    ClientAnchor anchor = picture.getClientAnchor();

                    // Get image data and create iText image instance
                    byte[] imageData = picture.getPictureData().getData();
                    Image image = Image.getInstance(imageData);

                    // Determine the position and size of the image
                    float x = pdfMarginLeft;
                    for (int col = 0; col < anchor.getCol1(); col++) {
                        x += sheet.getColumnWidthInPixels(col);
                    }

                    float y = document.getPageSize().getHeight() - pdfMarginTop;
                    for (int row = 0; row < anchor.getRow1(); row++) {
                        y -= sheet.getRow(row).getHeightInPoints();
                    }

                    // Determine the width and height of the cell to fit the image
                    float cellWidth = 0;
                    for (int col = anchor.getCol1(); col <= anchor.getCol2(); col++) {
                        cellWidth += sheet.getColumnWidthInPixels(col);
                    }

                    float cellHeight = 0;
                    for (int row = anchor.getRow1(); row <= anchor.getRow2(); row++) {
                        cellHeight += sheet.getRow(row).getHeightInPoints();
                    }

                    // Position and scale image to fit within the calculated cell dimensions
                    image.setAbsolutePosition(x, y - cellHeight);
                    image.scaleToFit(cellWidth, cellHeight);

                    document.add(image);
                }else if (shape instanceof XSSFSimpleShape) {
                    // Xử lý các hình dạng đơn giản như TextBox
                    XSSFSimpleShape simpleShape = (XSSFSimpleShape) shape;
                    ClientAnchor anchor = (ClientAnchor) simpleShape.getAnchor();


                    float x = pdfMarginLeft;
                    for (int col = 0; col < anchor.getCol1(); col++) {
                        x += sheet.getColumnWidthInPixels(col);
                    }

                    float y = document.getPageSize().getHeight() - pdfMarginTop;
                    for (int row = 0; row < anchor.getRow1(); row++) {
                        y -= sheet.getRow(row).getHeightInPoints();
                    }

                    float cellWidth = 0;
                    for (int col = anchor.getCol1(); col <= anchor.getCol2(); col++) {
                        cellWidth += sheet.getColumnWidthInPixels(col);
                    }

                    float cellHeight = 0;
                    for (int row = anchor.getRow1(); row <= anchor.getRow2(); row++) {
                        cellHeight += sheet.getRow(row).getHeightInPoints();
                    }

                    // Kiểm tra và lấy văn bản từ TextBox
                    String text = simpleShape.getText();
                    if (text != null && !text.isEmpty()) {
                        PdfContentByte cb = writer.getDirectContent();

                        // Tạo một hình chữ nhật cho TextBox và thiết lập vị trí
                        Rectangle textboxRectangle = new Rectangle(x, y - cellHeight, x + cellWidth, y);
                        textboxRectangle.setBackgroundColor(BaseColor.WHITE);
                        cb.rectangle(textboxRectangle);
                        cb.fill(); // Đổ màu nền cho TextBox

                        // Thêm văn bản vào PDF
                        ColumnText columnText = new ColumnText(cb);
                        columnText.setSimpleColumn(textboxRectangle);
                        Font font = new Font(Font.FontFamily.HELVETICA, 10);
                        Paragraph paragraph = new Paragraph(text, font);
                        columnText.addElement(paragraph);

                        // Căn chỉnh văn bản
                        columnText.setAlignment(Element.ALIGN_LEFT); // Căn trái, có thể thay đổi theo nhu cầu
                        int status = columnText.go(true); // Thực hiện việc thêm văn bản vào PDF
                        if (ColumnText.hasMoreText(status)) {
                            // Nếu có nhiều văn bản hơn có thể hiển thị, bạn có thể xử lý ở đây
                        }
                    }
                }
            }
        }
    }

    // Method to get the number of columns in the Excel sheet
    private static int getNumColumns(Sheet sheet) {
        int numColumns = 0;
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row headerRow = sheet.getRow(i);
            if (headerRow != null && headerRow.getLastCellNum() > numColumns) {
                numColumns = headerRow.getLastCellNum();
            }
        }
        return numColumns;
    }

    // Method to set PDF table column widths based on Excel sheet column widths
    private static void setColumnWidths(PdfPTable table, Sheet sheet, int numColumns) {
        float[] columnWidths = new float[numColumns];
        for (int i = 0; i < numColumns; i++) {
            columnWidths[i] = sheet.getColumnWidthInPixels(i);
        }
        try {
            table.setWidths(columnWidths);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static void populateTableWithData(Sheet sheet, PdfPTable table, int numColumns, Workbook workbook) {

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < numColumns; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    PdfPCell pdfCell = createPdfCell(cell, i, row.getHeightInPoints(), workbook, j, sheet);
                    table.addCell(pdfCell);
                }
            } else {
                for (int j = 0; j < numColumns; j++) {
                    PdfPCell pdfCell = new PdfPCell(new Phrase(""));
                    table.addCell(pdfCell);
                }
            }
        }
    }

    // Method to create a PDF cell with styles and content based on the Excel cell
    private static PdfPCell createPdfCell(Cell cell, int rowIndex, float rowHeight, Workbook workbook, int colIndex, Sheet sheet) {
        PdfPCell pdfCell = new PdfPCell();
        if (cell != null) {


            CellStyle cellStyle = cell.getCellStyle();
            String fontName = workbook.getFontAt(cellStyle.getFontIndex()).getFontName();

            Font cellFont = getFontFromWorkbook(fontName);
            setCellBackgroundColor(pdfCell, cellStyle);
            setCellFontAttributes(cellFont, cellStyle, workbook);
            pdfCell.setFixedHeight(rowHeight);

            // Set cell value (text, numeric, boolean, etc.)
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            setCellValue(pdfCell, cell, cellFont, evaluator);

            // Set cell borders
            setCellBorders(pdfCell, cellStyle, sheet, rowIndex, colIndex);
            setCellTextAlignment(pdfCell, cellStyle);
            setCellTextOrientation(pdfCell, cellStyle);

        } else {
            pdfCell.setPhrase(new Phrase(""));
        }
        return pdfCell;
    }

    public static Font getFontFromWorkbook(String workbookFontName) {
        return FontFactory.getFont(workbookFontName);
    }

    private static void setCellTextOrientation(PdfPCell pdfCell, CellStyle cellStyle) {
        short rotation = cellStyle.getRotation();

        // Convert Excel rotation (0-180, -90) to PDF rotation (degrees)
        if (rotation != 0) {
            if (rotation <= 90) {
                pdfCell.setRotation(rotation); // Positive rotation (0-90)
            } else {
                pdfCell.setRotation(rotation - 360); // Negative rotation (-90 to -180)
            }
        }
    }


    private static void setCellTextAlignment(PdfPCell pdfCell, CellStyle cellStyle) {
        // Set horizontal alignment
        switch (cellStyle.getAlignment()) {
            case LEFT -> pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            case CENTER -> pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            case RIGHT -> pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            case JUSTIFY -> pdfCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            default -> pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Default alignment
        }

        // Set vertical alignment
        switch (cellStyle.getVerticalAlignment()) {
            case TOP -> pdfCell.setVerticalAlignment(Element.ALIGN_TOP);
            case CENTER -> pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            case BOTTOM -> pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            default -> pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM); // Default alignment
        }
    }


    // Method to set the background color for a PDF cell
    private static void setCellBackgroundColor(PdfPCell pdfCell, CellStyle cellStyle) {
        XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
        if (bgColor != null) {
            byte[] rgb = bgColor.getRGB();
            if (rgb != null) {
                int argb = (255 << 24) | ((rgb[0] & 0xFF) << 16) | ((rgb[1] & 0xFF) << 8) | (rgb[2] & 0xFF);
                pdfCell.setBackgroundColor(new BaseColor(argb)); // Set background color
            }
        }
    }

    // Method to set font attributes (family, size, color) for a PDF cell based on Excel font style
    private static void setCellFontAttributes(Font cellFont, CellStyle cellStyle, Workbook workbook) {
        org.apache.poi.ss.usermodel.Font excelFont = workbook.getFontAt(cellStyle.getFontIndex());
        cellFont.setSize(excelFont.getFontHeightInPoints());

        if (excelFont.getBold()) {
            cellFont.setStyle(Font.BOLD);
        }

        if (excelFont.getItalic()) {
            cellFont.setStyle(cellFont.getStyle() | Font.ITALIC);
        }

        XSSFColor fontColor = ((XSSFCellStyle) cellStyle).getFont().getXSSFColor();
        if (fontColor != null) {
            byte[] rgb = fontColor.getRGB();
            if (rgb != null) {
                cellFont.setColor(new BaseColor(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF));
            }
        }

        if (excelFont.getUnderline() != org.apache.poi.ss.usermodel.Font.U_NONE) {
            cellFont.setStyle(cellFont.getStyle() | Font.UNDERLINE);
        }
    }


    // Method to set the value of a PDF cell based on the Excel cell content type
    private static void setCellValue(PdfPCell pdfCell, Cell cell, Font cellFont, FormulaEvaluator evaluator) {
        switch (cell.getCellType()) {
            case STRING:
                if (cell.getHyperlink() != null) {
                    String link = cell.getHyperlink().getAddress();
                    Phrase phrase = new Phrase(cell.getStringCellValue(), cellFont);
                    pdfCell.setPhrase(phrase);

                    // Gán sự kiện liên kết cho ô
                    pdfCell.setCellEvent(new LinkInCellEvent(link));
                } else {
                    pdfCell.setPhrase(new Phrase(cell.getStringCellValue(), cellFont));
                }
                break;
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    pdfCell.setPhrase(new Phrase(String.valueOf((long) numericValue), cellFont));
                } else {
                    pdfCell.setPhrase(new Phrase(String.valueOf(numericValue), cellFont));
                }
                break;
            case BOOLEAN:
                pdfCell.setPhrase(new Phrase(String.valueOf(cell.getBooleanCellValue()), cellFont));  // Boolean cell
                break;
            case FORMULA:
                CellValue cellValue = evaluator.evaluate(cell);

                switch (cellValue.getCellType()) {
                    case NUMERIC:
                        double numericFormulaValue = cell.getNumericCellValue();
                        if (numericFormulaValue == (long) numericFormulaValue) {
                            pdfCell.setPhrase(new Phrase(String.valueOf((long) numericFormulaValue), cellFont));
                        } else {
                            pdfCell.setPhrase(new Phrase(String.valueOf(numericFormulaValue), cellFont));
                        }
                        break;
                    case STRING:
                        pdfCell.setPhrase(new Phrase(cellValue.getStringValue(), cellFont));  // String result from formula
                        break;
                    case BOOLEAN:
                        pdfCell.setPhrase(new Phrase(String.valueOf(cellValue.getBooleanValue()), cellFont));  // Boolean result from formula
                        break;
                    default:
                        pdfCell.setPhrase(new Phrase("", cellFont));  // Empty or unknown result
                }
                break;
            default:
                pdfCell.setPhrase(new Phrase("", cellFont));  // Empty cell
        }
    }


    private static void setCellBorders(PdfPCell pdfCell, CellStyle cellStyle, Sheet sheet, int rowIndex, int colIndex) {
        BorderStyle borderTop = cellStyle.getBorderTop();
        BorderStyle borderBottom = cellStyle.getBorderBottom();
        BorderStyle borderLeft = cellStyle.getBorderLeft();
        BorderStyle borderRight = cellStyle.getBorderRight();

        BorderStyle topNeighborBorder = BorderStyle.NONE;
        if (rowIndex > 0) {
            Row topRow = sheet.getRow(rowIndex - 1);
            if (topRow != null) {
                Cell topCell = topRow.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (topCell != null) {
                    topNeighborBorder = topCell.getCellStyle().getBorderBottom();
                }
            }
        }

        BorderStyle bottomNeighborBorder = BorderStyle.NONE;
        if (rowIndex < sheet.getLastRowNum()) {
            Row bottomRow = sheet.getRow(rowIndex + 1);
            if (bottomRow != null) {
                Cell bottomCell = bottomRow.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (bottomCell != null) {
                    bottomNeighborBorder = bottomCell.getCellStyle().getBorderTop();
                }
            }
        }

        BorderStyle leftNeighborBorder = BorderStyle.NONE;
        if (colIndex > 0) {
            Cell leftCell = sheet.getRow(rowIndex).getCell(colIndex - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (leftCell != null) {
                leftNeighborBorder = leftCell.getCellStyle().getBorderRight();
            }
        }

        BorderStyle rightNeighborBorder = BorderStyle.NONE;
        if (colIndex < sheet.getRow(rowIndex).getLastCellNum() - 1) {
            Cell rightCell = sheet.getRow(rowIndex).getCell(colIndex + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (rightCell != null) {
                rightNeighborBorder = rightCell.getCellStyle().getBorderLeft();
            }
        }

        XSSFColor borderColorTop = ((XSSFCellStyle) cellStyle).getBorderColor(XSSFCellBorder.BorderSide.TOP);
        XSSFColor borderColorBottom = ((XSSFCellStyle) cellStyle).getBorderColor(XSSFCellBorder.BorderSide.BOTTOM);
        XSSFColor borderColorLeft = ((XSSFCellStyle) cellStyle).getBorderColor(XSSFCellBorder.BorderSide.LEFT);
        XSSFColor borderColorRight = ((XSSFCellStyle) cellStyle).getBorderColor(XSSFCellBorder.BorderSide.RIGHT);

        // Biến để lưu màu sắc của các border từ các ô hàng xóm
        XSSFColor neighborBorderColorTop = null;
        XSSFColor neighborBorderColorBottom = null;
        XSSFColor neighborBorderColorLeft = null;
        XSSFColor neighborBorderColorRight = null;

        // Kiểm tra và lấy kiểu border và màu sắc của ô bên trên
        if (rowIndex > 0) {
            Row topRow = sheet.getRow(rowIndex - 1);
            if (topRow != null) {
                Cell topCell = topRow.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (topCell != null) {
                    topNeighborBorder = topCell.getCellStyle().getBorderBottom();
                    // Lấy màu border của ô hàng xóm bên trên
                    neighborBorderColorTop = ((XSSFCellStyle) topCell.getCellStyle()).getBorderColor(XSSFCellBorder.BorderSide.BOTTOM);
                }
            }
        }

        // Kiểm tra và lấy kiểu border và màu sắc của ô bên dưới
        if (rowIndex < sheet.getLastRowNum()) {
            Row bottomRow = sheet.getRow(rowIndex + 1);
            if (bottomRow != null) {
                Cell bottomCell = bottomRow.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (bottomCell != null) {
                    bottomNeighborBorder = bottomCell.getCellStyle().getBorderTop();
                    // Lấy màu border của ô hàng xóm bên dưới
                    neighborBorderColorBottom = ((XSSFCellStyle) bottomCell.getCellStyle()).getBorderColor(XSSFCellBorder.BorderSide.TOP);
                }
            }
        }

        // Kiểm tra và lấy kiểu border và màu sắc của ô bên trái
        if (colIndex > 0) {
            Cell leftCell = sheet.getRow(rowIndex).getCell(colIndex - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (leftCell != null) {
                leftNeighborBorder = leftCell.getCellStyle().getBorderRight();
                // Lấy màu border của ô hàng xóm bên trái
                neighborBorderColorLeft = ((XSSFCellStyle) leftCell.getCellStyle()).getBorderColor(XSSFCellBorder.BorderSide.RIGHT);
            }
        }

        // Kiểm tra và lấy kiểu border và màu sắc của ô bên phải
        if (colIndex < sheet.getRow(rowIndex).getLastCellNum() - 1) {
            Cell rightCell = sheet.getRow(rowIndex).getCell(colIndex + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (rightCell != null) {
                rightNeighborBorder = rightCell.getCellStyle().getBorderLeft();
                // Lấy màu border của ô hàng xóm bên phải
                neighborBorderColorRight = ((XSSFCellStyle) rightCell.getCellStyle()).getBorderColor(XSSFCellBorder.BorderSide.LEFT);
            }
        }



        // Set border colors if they exist
        BaseColor pdfBorderColorTop = (borderColorTop != null && borderColorTop.getRGB() != null)
                ? new BaseColor(borderColorTop.getRGB()[0] & 0xFF, borderColorTop.getRGB()[1] & 0xFF, borderColorTop.getRGB()[2] & 0xFF)
                : BaseColor.BLACK;

        BaseColor pdfBorderColorBottom = (borderColorBottom != null && borderColorBottom.getRGB() != null)
                ? new BaseColor(borderColorBottom.getRGB()[0] & 0xFF, borderColorBottom.getRGB()[1] & 0xFF, borderColorBottom.getRGB()[2] & 0xFF)
                : BaseColor.BLACK;

        BaseColor pdfBorderColorLeft = (borderColorLeft != null && borderColorLeft.getRGB() != null)
                ? new BaseColor(borderColorLeft.getRGB()[0] & 0xFF, borderColorLeft.getRGB()[1] & 0xFF, borderColorLeft.getRGB()[2] & 0xFF)
                : BaseColor.BLACK;

        BaseColor pdfBorderColorRight = (borderColorRight != null && borderColorRight.getRGB() != null)
                ? new BaseColor(borderColorRight.getRGB()[0] & 0xFF, borderColorRight.getRGB()[1] & 0xFF, borderColorRight.getRGB()[2] & 0xFF)
                : BaseColor.BLACK;



        BaseColor pdfNeighborBorderColorTop = (neighborBorderColorTop != null) ? new BaseColor(neighborBorderColorTop.getRGB()[0] & 0xFF, neighborBorderColorTop.getRGB()[1] & 0xFF, neighborBorderColorTop.getRGB()[2] & 0xFF) : BaseColor.BLACK;
        BaseColor pdfNeighborBorderColorBottom = (neighborBorderColorBottom != null) ? new BaseColor(neighborBorderColorBottom.getRGB()[0] & 0xFF, neighborBorderColorBottom.getRGB()[1] & 0xFF, neighborBorderColorBottom.getRGB()[2] & 0xFF) : BaseColor.BLACK;
        BaseColor pdfNeighborBorderColorLeft = (neighborBorderColorLeft != null) ? new BaseColor(neighborBorderColorLeft.getRGB()[0] & 0xFF, neighborBorderColorLeft.getRGB()[1] & 0xFF, neighborBorderColorLeft.getRGB()[2] & 0xFF) : BaseColor.BLACK;
        BaseColor pdfNeighborBorderColorRight = (neighborBorderColorRight != null) ? new BaseColor(neighborBorderColorRight.getRGB()[0] & 0xFF, neighborBorderColorRight.getRGB()[1] & 0xFF, neighborBorderColorRight.getRGB()[2] & 0xFF) : BaseColor.BLACK;


        // Top border
        if (borderTop != BorderStyle.NONE || topNeighborBorder != BorderStyle.NONE) {
            BorderStyle effectiveBorderStyle;
            BaseColor effectiveBorderColor;
            if (borderTop != BorderStyle.NONE) {
                effectiveBorderStyle = borderTop;
                effectiveBorderColor = pdfBorderColorTop;
            }
            else {
                effectiveBorderStyle = topNeighborBorder;
                effectiveBorderColor = pdfNeighborBorderColorTop;
            }

            setBorder(pdfCell, effectiveBorderStyle, effectiveBorderColor);
        } else {
            pdfCell.disableBorderSide(Rectangle.TOP);
        }

        // Bottom border
        if ((sheet.getRow(rowIndex+1) == null || sheet.getRow(rowIndex+1).getCell(colIndex) == null) || (borderBottom != BorderStyle.NONE || bottomNeighborBorder != BorderStyle.NONE)) {
            BorderStyle effectiveBorderStyle;
            BaseColor effectiveBorderColor;
            if (borderBottom != BorderStyle.NONE) {
                effectiveBorderStyle = borderBottom;
                effectiveBorderColor = pdfBorderColorBottom;
            }
            else {
                effectiveBorderStyle = bottomNeighborBorder;
                effectiveBorderColor = pdfNeighborBorderColorBottom;
            }

            setBorder(pdfCell, effectiveBorderStyle, effectiveBorderColor);
        } else {
            pdfCell.disableBorderSide(Rectangle.BOTTOM);
        }

        // Left border
        if (borderLeft != BorderStyle.NONE || leftNeighborBorder != BorderStyle.NONE) {

            BorderStyle effectiveBorderStyle;
            BaseColor effectiveBorderColor;
            if (borderLeft != BorderStyle.NONE) {
                effectiveBorderStyle = borderLeft;
                effectiveBorderColor = pdfBorderColorLeft;
            }
            else {
                effectiveBorderStyle = leftNeighborBorder;
                effectiveBorderColor = pdfNeighborBorderColorLeft;
            }
            setBorder(pdfCell, effectiveBorderStyle, effectiveBorderColor);
        } else {
            pdfCell.disableBorderSide(Rectangle.LEFT);
        }

        // Right border
        if (sheet.getRow(rowIndex) == null || sheet.getRow(rowIndex).getCell(colIndex + 1) == null || (borderRight != BorderStyle.NONE || rightNeighborBorder != BorderStyle.NONE)) {
            BorderStyle effectiveBorderStyle;
            BaseColor effectiveBorderColor;
            if (borderRight != BorderStyle.NONE) {
                effectiveBorderStyle = borderRight;
                effectiveBorderColor = pdfBorderColorRight;
            }
            else {
                effectiveBorderStyle = rightNeighborBorder;
                effectiveBorderColor = pdfNeighborBorderColorRight;
            }

            setBorder(pdfCell, effectiveBorderStyle, effectiveBorderColor);
        } else {
            pdfCell.disableBorderSide(Rectangle.RIGHT);
        }
    }


    private static void setBorder(PdfPCell pdfCell, BorderStyle borderStyle, BaseColor borderColor) {
        float borderWidth = getPdfBorderWidth(borderStyle); // Get appropriate width based on BorderStyle
        if (borderWidth > 0) {
            // Delegate to applyBorderStyle from BorderStylesHandler
            BorderStylesHandler.applyBorderStyle(pdfCell, borderStyle, borderColor, borderWidth);
        } else {
            // Disable the border if width is zero
            pdfCell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
        }
    }

    // Method to get PDF border width based on Excel BorderStyle
    private static float getPdfBorderWidth(BorderStyle borderStyle) {
        return switch (borderStyle) {
            case THIN -> 0.5f; // Thin and minimal
            case MEDIUM -> 1.0f; // Medium, default line width for emphasis
            case THICK -> 1.5f; // Thick, for strong emphasis
            case DASHED -> 0.75f; // Moderate line width for dash style
            case DOTTED -> 0.5f; // Dotted, light and less intrusive
            case DASH_DOT -> 1.0f; // Dash-dot, moderate weight for emphasis
            case DASH_DOT_DOT -> 1.0f; // Dash-dot-dot, moderate weight, clearer pattern
            case DOUBLE -> 1.5f; // Double-line border, strong emphasis
            case HAIR -> 0.2f; // Hairline, minimal visibility
            case MEDIUM_DASH_DOT -> 1.25f; // Medium dash-dot, slightly thicker for visibility
            case MEDIUM_DASH_DOT_DOT -> 1.25f; // Medium dash-dot-dot, slightly thicker, balanced pattern
            case MEDIUM_DASHED -> 1.25f; // Medium dashed, moderate thickness for dash
            case SLANTED_DASH_DOT -> 1.25f; // Slanted dash-dot, balanced thickness for unique pattern
            case NONE -> 0.0f; // No border, invisible
            default -> 0f; // Default if unrecognized
        };
    }

}