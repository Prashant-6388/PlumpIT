/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;

import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;

/**
 *
 * @author Prashant
 */
class WatermarkPageEvent extends PdfPageEventHelper {
    private BaseFont baseFont = null;
    private PdfGState gState = null;

    public WatermarkPageEvent() {
        try {
            baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            gState = new PdfGState();
            gState.setFillOpacity(0.2f); // Adjust opacity as needed
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte content = writer.getDirectContentUnder();
        content.saveState();
        content.setGState(gState);
        content.beginText();
        content.setFontAndSize(baseFont, 48);
        content.setColorFill(Color.LIGHT_GRAY);
        content.showTextAligned(Element.ALIGN_CENTER, "Spectacular Eng Pvt. Ltd.", 297.5f, 421, 45);
        content.endText();
        content.restoreState();
        
        // Draw footer line
        content = writer.getDirectContent();
        content.saveState();
        content.setLineWidth(1);
        content.moveTo(document.left(), document.bottom() - 10);
        content.lineTo(document.right(), document.bottom() - 10);
        content.stroke();
        content.restoreState();
       
        content = writer.getDirectContent();
        content.beginText();
        content.setFontAndSize(baseFont, 8);
        content.setColorFill(Color.LIGHT_GRAY);
        content.showTextAligned(Element.ALIGN_CENTER, "Spectacular Eng Pvt. Ltd.", document.getPageSize().getWidth()/2, document.bottom() - 20, 0);
        content.showTextAligned(Element.ALIGN_RIGHT, "Page " + writer.getPageNumber(), document.right() - 20, document.bottom() - 20, 0);
        content.endText();
    }
}
