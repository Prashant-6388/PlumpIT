/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.plumbit.enums.StandardType;
import static com.pc.plumbit.enums.StandardType.SOLID_WASTE_DRY_GARBAGE;
import static com.pc.plumbit.enums.StandardType.SOLID_WASTE_WET_GARBAGE;
import static com.pc.plumbit.generator.PDFUtils.addTableCell;
import static com.pc.plumbit.generator.PDFUtils.courierBold;
import static com.pc.plumbit.generator.PDFUtils.addTableHeader;
import static com.pc.plumbit.generator.PDFUtils.courier;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.input.TowerData;
import com.pc.utils.DataFormater;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prashant
 */
public class SolidWasteCalculation {
    private static final Logger log = LoggerFactory.getLogger(PDFGenerator.class);
    
    public static void main(String args[]){
        try {
            String location = "D:\\Programming\\GUI\\NB\\PlumbIT\\pdf\\solidWaste.pdf";
            generatePDF(location, "NYATI EVANIA", 40, 60, 0.312f);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
    }
    
    public static boolean generatePDF(String location, String projectName, int dryGarbagePercent, int wetGarbagePercent, float wasteGenerationPerPerson) throws FileNotFoundException {
        File file = new File(location);

        Document document = new Document();
        // step 2:
        // we create 3 different writers that listen to the document
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));

        // step 3: we open the document
        document.open();
//        createSolidWasteHeaderTable(document, projectName, wasteGenerationPerPerson, dryGarbagePercent, wetGarbagePercent);
        pdfWriter.flush();
        document.close();
        return false;
    }

    public static void createSolidWasteHeaderTable(Document document, PdfData pdfData) {
        int dryGarbagePercent = DataFormater.getNormalizedStandardValue(pdfData.getStandardValMap().get(SOLID_WASTE_DRY_GARBAGE));
        int wetGarbagePercent = DataFormater.getNormalizedStandardValue(pdfData.getStandardValMap().get(SOLID_WASTE_WET_GARBAGE));
        
        float[] columnDefinitionSize = {40F, 120F, 120F, 120F, 120F, 140F};
        
        PdfPCell cell = null;

        PdfPTable table = new PdfPTable(columnDefinitionSize);
//            table.getDefaultCell().setBorder(1);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);

        //HEADER
        addTableHeader(table, "SOLID WASTE CALCULATIONS ", 6, courierBold);

        //row1
        cell = new PdfPCell(new Phrase("DESIGN CONSIDERATIONS", courier));
        cell.setRowspan(3);
        cell.setBackgroundColor(Color.decode("#CCCCFF"));
        cell.setVerticalAlignment(ALIGN_MIDDLE);
        cell.setHorizontalAlignment(ALIGN_MIDDLE);
//        cell.setPaddingBottom(50);
        cell.setRotation(90);

        table.addCell(cell);

        float solidWastePerPerson = (float)pdfData.getStandardValMap().get(StandardType.SOLID_WASTE_KG_PERSON_DAY).getValue();
        addTableCell(table, "Waste Generation per person", courier);
        addTableCell(table, String.valueOf(solidWastePerPerson), courier);
        addTableCell(table, "kg/day ", courier);


        cell = new PdfPCell(new Phrase(pdfData.getProjectName(), courier));
        cell.setRowspan(3);
        cell.setColspan(2);
        cell.setBackgroundColor(Color.decode("#CCCCFF"));
        cell.setVerticalAlignment(ALIGN_MIDDLE);
//        cell.setPaddingLeft(10);
        cell.setHorizontalAlignment(ALIGN_CENTER);
        table.addCell(cell);

        //row 2
        addTableCell(table, "Dry Garbage", courier);
        addTableCell(table, String.valueOf(dryGarbagePercent), courier);
        addTableCell(table, "lpcd", courier);

        //row 3
        addTableCell(table, "Wet Garbage", courier);
        addTableCell(table, String.valueOf(wetGarbagePercent), courier);
        addTableCell(table, "of Total Waste Generated   ", courier);

        //row 4
        addTableCell(table, "Sr", courierBold);
        addTableCell(table, "Description", courierBold);
        addTableCell(table, "Total No of Flats", courierBold);
        addTableCell(table, "Total Population", courierBold);
        addTableCell(table, "Total Waste Generated", courierBold);
        addTableCell(table, "Volume of waste generated (Liters)", courierBold);

        //row 5
        int sr=1;
        float totalWasteForAllTowers=0;
        float totalVolumeForAllTowers=0;
        
        for(TowerData towerData : pdfData.getTowersList()) {
            addTableHeader(table, towerData.getName(), 6, courier);
            float totalWasteGeneratedForTower = 0;
            float totalWasteVolumnForTower = 0;
            TreeMap<StandardType, Integer> flatsData = towerData.getFlatsData();
            for(StandardType type : flatsData.keySet()) {
                Integer numOfFlats = flatsData.get(type);
                if (numOfFlats > 0) {
                    addTableCell(table, String.valueOf(sr++), courier);
                    addTableCell(table, type.getValue(), courier);
                    addTableCell(table, String.valueOf(numOfFlats), courier);
                    //Total population of type
                    StandardValues standardVal = pdfData.getStandardValMap().get(type);
                    String standardValue = DataFormater.getStandardValue(standardVal);
                    int pplForType = Integer.parseInt(standardValue);
                    int totalPopulation = numOfFlats * pplForType;
                    addTableCell(table, String.valueOf(totalPopulation), courier);
                    //Waste Generated per flat type
                    float wasteGeneratedPerFlat = totalPopulation * solidWastePerPerson;
                    addTableCell(table, String.valueOf(wasteGeneratedPerFlat), courier);
                    //volumn of waste
                    double solidWastePer20Family = 0;
                    double volumnOfWaste = solidWastePer20Family * numOfFlats/20;
                    addTableCell(table, String.valueOf(volumnOfWaste), courier);
                    
                    totalWasteGeneratedForTower += wasteGeneratedPerFlat;
                    totalWasteVolumnForTower += volumnOfWaste;
                }
            }
            addTableCell(table, "", courier, new CellStyle(1, 3));
            addTableCell(table, "Total", courierBold);
            addTableCell(table, String.valueOf(totalWasteGeneratedForTower), courierBold);
            addTableCell(table, String.valueOf(totalWasteVolumnForTower), courierBold);
            totalWasteForAllTowers += totalWasteGeneratedForTower;
            totalVolumeForAllTowers += totalWasteVolumnForTower;
        }
        
        addTableHeader(table, "", 6,courierBold);
        
        addTableCell(table, "1", courierBold);
        addTableCell(table, "Total Garbage Generated", courierBold, new CellStyle(1, 2));
        addTableCell(table, "=", courierBold);
        addTableCell(table, String.valueOf(totalWasteForAllTowers), courierBold, new CellStyle(1, 2));   
        
        addTableCell(table, "1", courierBold);
        addTableCell(table, "Total Dry Garbage Generated", courierBold, new CellStyle(1, 2));
        addTableCell(table, "=", courierBold);
        float totalDryGarbageGenerated = totalWasteForAllTowers * dryGarbagePercent / 100;
        addTableCell(table, String.valueOf(totalDryGarbageGenerated), courierBold, new CellStyle(1, 2));

        addTableCell(table, "1", courierBold);
        addTableCell(table, "Total Garbage Generated", courierBold, new CellStyle(1, 2));
        addTableCell(table, "=", courierBold);
        float totalWetGarbageGenerated = totalWasteForAllTowers * wetGarbagePercent / 100;
        addTableCell(table, String.valueOf(totalWetGarbageGenerated), courierBold, new CellStyle(1, 2));
        
        addTableCell(table, "1", courierBold);
        addTableCell(table, "Total Volume of Garbage Generated", courierBold, new CellStyle(1, 2));
        addTableCell(table, "=", courierBold);
        addTableCell(table, String.valueOf(totalVolumeForAllTowers), courierBold, new CellStyle(1, 2));
        
        document.add(table);
    }

}
