/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.plumbit.DataCalculator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prashant
 */
public class TestProgram {
    
    public static void main(String args[]) {
        String outputPath = "pdf_with_background_and_text_openpdf.pdf";
        URL imageUrl = DataCalculator.class.getResource("/images/front-page.jpg");
//        String backgroundImagePath = "/images/background-img-1.jpg"; // Path to your background image

        // Create the document
        Document document = new Document(); // A4 sized document

        try {
            // Create PdfWriter instance
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));

            // Open the document
            document.open();

            // Get PdfContentByte from the writer for direct content manipulation
            PdfContentByte canvas = writer.getDirectContentUnder();

            // Add background image
            addBackgroundImage(canvas, imageUrl.getPath());
            // Add text at specific coordinates
            addTextAtPosition(canvas, "Water Demand Calculation", 50, 800, 18);
            addTextAtPosition(canvas, "for", 150, 780, 18);
            addTextAtPosition(canvas, "NYATI", 150, 450, 38);
            addTextAtPosition(canvas, "Created By", 50, 80, 18);
            addTextAtPosition(canvas, "Septyacular Eng Pvt. Ltd.", 50, 60, 18);

            System.out.println("PDF created with background image and text at specific locations!");

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close the document
            document.close();
        }
    }
    

    private static void addBackgroundImage(PdfContentByte canvas, String backgroundImagePath) throws IOException, DocumentException {
    // Load background image
    Image backgroundImage = Image.getInstance(backgroundImagePath);

    // Scale the image to fit the entire page
    backgroundImage.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

    // Set the position (0, 0) to align the image at the bottom-left corner
    backgroundImage.setAbsolutePosition(0, 0);

    // Add the image to the canvas (background layer)
    canvas.addImage(backgroundImage);
}

    private static void addTextAtPosition(PdfContentByte canvas, String text, float x, float y, int size) throws DocumentException {
        try {
            // Begin writing text
            canvas.beginText();
            
            // Set font and size
            canvas.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), size);
            
            // Set the position of the text
            canvas.setTextMatrix(x, y);
            
            // Add the text
            canvas.showText(text);
            
            // End text
            canvas.endText();
        } catch (IOException ex) {
            Logger.getLogger(TestProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
