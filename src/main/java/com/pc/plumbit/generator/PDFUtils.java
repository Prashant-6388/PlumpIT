/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 *
 * @author Prashant
 */
public class PDFUtils {
    
    public static final Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 8);
    public static final Font courier = FontFactory.getFont(FontFactory.COURIER, 8);
    
    private PDFUtils(){
    }
    
    public static void addTableHeader(PdfPTable table, String content, int length, Font courier) {
        PdfPCell cell = new PdfPCell(new Phrase(content, courier));
        cell.setColspan(length);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    public static void createTableRow(Document document, String text, int colSpan, Font font,
            int vAlign, int hAlign, boolean emptyRowBefore, boolean emptyRowAfter) {
        PdfPTable table = new PdfPTable(colSpan);
        table.setWidthPercentage(100);
        if (emptyRowBefore) {
            addTableCell(table, "", courierBold, new CellStyle(0, colSpan));
        }
        if(text!=null && font != null){
            addTableCell(table, text, font, new CellStyle(0, colSpan, vAlign, hAlign));
        }
        if (emptyRowAfter) {
            addTableCell(table, "", courierBold, new CellStyle(0, colSpan));
        }
        document.add(table);
    }
    
    public static void addTableCell(PdfPTable table, String text, Font font) {
        addTableCell(table, text, font, new CellStyle());
    }

    public static void addTableCell(PdfPTable table, String text, Font font, CellStyle style) {
        PdfPCell cell = getBasicCell(text, font);
        if (style.getRowSpan() != 0) {
            cell.setRowspan(style.getRowSpan());
        }
        if (style.getColSpan() != 0) {
            cell.setColspan(style.getColSpan());
        }
        if (style.getHorizontalAlignment() != -1) {
            cell.setHorizontalAlignment(style.getHorizontalAlignment());
        }
        if (style.getVerticalAlignment() != -1) {
            cell.setVerticalAlignment(style.getVerticalAlignment());
        }
        if (style.getRotation() != 0) {
            cell.setRotation(style.getRotation());
        }
        cell.setBackgroundColor(style.getBackgroundColor());
        table.addCell(cell);
    }

    public static PdfPCell getBasicCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderWidthRight(0.5f);
//        cell.setPadding(5);
        return cell;
    }

}
