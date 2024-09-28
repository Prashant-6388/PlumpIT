/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.initializer.DataInitializer;
import com.pc.plumbit.DataCalculator;
import com.pc.plumbit.enums.ConsumptionType;
import com.pc.plumbit.enums.StandardType;
import static com.pc.plumbit.generator.PDFUtils.addTableCell;
import static com.pc.plumbit.generator.PDFUtils.addTableHeader;
import static com.pc.plumbit.generator.PDFUtils.courier;
import static com.pc.plumbit.generator.PDFUtils.courierBold;
import static com.pc.plumbit.generator.PDFUtils.createTableRow;
import static com.pc.plumbit.generator.WaterDemandCalculator.DESCRIPTION_ROW_TOTAL;
import com.pc.plumbit.model.ConsumptionDetail;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.domestic.TowerWaterDemandRowData;
import com.pc.plumbit.model.domestic.DomesticWaterDemandData;
import com.pc.plumbit.model.input.TowerData;
import com.pc.utils.DataFormater;
import com.pc.utils.MathUtils;
import com.thoughtworks.xstream.XStream;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prashant
 */
public class PDFGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(PDFGenerator.class);
    
    private static final String WATER_CONSUMPTION_TYPE_DOMESTIC = "Domestic";
    private static final String WATER_CONSUMPTION_TYPE_FLUSH = "Flush";
    private static final String WATER_CONSUMPTION_TYPE_SEWER = "Sewer";
    private static final float[] COLUMN__SIZES =  {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
    
    public static void main(String args[]) throws FileNotFoundException {
        try {
            File file = new File("test1.pdf");
            
            XStream xstream = DataInitializer.getXstream();
            File inputFile = new File("D:\\Programming\\GUI\\NB\\PlumbIT\\pdf\\New_Input_7.pscs");
            PdfData projectData = (PdfData) xstream.fromXML(inputFile);
            
            Document document = new Document();
            // step 2:
            // we create 3 different writers that listen to the document
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            pdfWriter.setPageEvent(new WatermarkPageEvent());
            document.open();
//            createFrontPage(pdfWriter);
//            document.newPage();
            createProjectDetailsPage(document, projectData);
            // Add background image
            // step 3: we open the document
            //        createWaterDemandTable(document);
            //        createWaterDemandDescription(document);
            //        createTowerWaterDemand(document, towerFlatMap);
            //        createMiscellaneousTable(document, miscAreaMap);
            pdfWriter.flush();
            document.close();
            //        return false;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PDFGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void generatePDF(PdfData pdfData, JFrame parent) {
        File file = null;
        Document document = null;
        try {
            file = new File(pdfData.getPdfLocation() + "/" + pdfData.getProjectName() + ".pdf");
            if (file.exists()) {
                JOptionPane.showMessageDialog(parent, "File already exist", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                document = new Document();
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
                pdfWriter.setPageEvent(new WatermarkPageEvent());
                document.open();
                createFrontPage(pdfWriter, pdfData.getProjectName());
                document.newPage();
                createProjectDetailsPage(document, pdfData);
                document.newPage();
                if(!pdfData.getTowersList().isEmpty()) {
                    DomesticWaterDemandData domesticWaterDemandData = new DomesticWaterDemandData(pdfData);
                    generateWaterDemandPDF(document, pdfData, parent, domesticWaterDemandData);
                    document.newPage();
                }
                if(!pdfData.getOfficesList().isEmpty()) {
                    OfficeShowroomCalculation.createPDF(document, pdfData);
                    document.newPage();
                }
                if(!pdfData.getTowersList().isEmpty()) {
                    SolidWasteCalculation.createSolidWasteHeaderTable(document, pdfData);
                }
                pdfWriter.flush();
                document.close();
                JOptionPane.showMessageDialog(parent, "PDF Created successfully\nLocation: " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            log.error("Error creating pdf : ",ex);
        }
    }
    
    public static void generateWaterDemandPDF(Document document, PdfData pdfData, JFrame parent, 
            DomesticWaterDemandData domesticWaterDemandData) throws FileNotFoundException {
        File file = null;
        try {

            addWaterCriteriaTable(document, pdfData.getStandardValMap(), pdfData.getProjectName());
            createWaterDemandDescription(document);
            createTowerWaterDemand(document, pdfData, domesticWaterDemandData.getTowerRowData());
            if(!pdfData.getOutsideAreaMap().isEmpty()) {
                createMiscellaneousTable(document, pdfData, domesticWaterDemandData);
            }

            createTableRow(document, "Notes : ", 12, courier, Element.ALIGN_LEFT , Element.ALIGN_LEFT, false, false);
            createTableRow(document, "1. landscape area shall be confirmed by landscape consultant.", 12, courier, 
                    Element.ALIGN_LEFT , Element.ALIGN_LEFT, false, true);
            createTableRow(document, "TOTAL DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER , Element.ALIGN_CENTER, false, false);

            int totalLpda =(int) Math.ceil(getTotalLpda(pdfData));
            int totalLpdb = (int) Math.ceil(getTotalLpdb(pdfData));

            int roundLpda = MathUtils.roundUpToNearestTenThousand((int)totalLpda);
            int roundLpdb = MathUtils.roundUpToNearestTenThousand((int)totalLpdb);

            createTotalWaterDemand(document, totalLpda, totalLpdb, roundLpda, roundLpdb);

            int totalFlowToSewer = (int)Math.ceil(getTotalFlowToSewer(pdfData));
            createTableRow(document, "STP Capacity", 12, courierBold, Element.ALIGN_CENTER , Element.ALIGN_CENTER, false, false);
            createSTPCapacity(document, pdfData, totalFlowToSewer, roundLpdb);
            createTableRow(document,"",12,null,0,0,false,false);
            
            TowerWaterDemandRowData miscRowData = WaterDemandCalculator.calculateWaterDemandForMiscTotalForTower(pdfData);
            
            createUGRTable(document, pdfData, (int)miscRowData.getLpda(), pdfData.getGroupedTowerNamesList());
            createOHTCapacity(document, pdfData);

        } catch(DocumentException e) {
            JOptionPane.showMessageDialog(parent, "PDF Generation failed.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            log.error("PDF generation failed", e);
        }
        finally {
            if (file != null && document != null) {
                document.close();
            }
        }
    }
    
    private static void addWaterCriteriaTable(Document document, HashMap<StandardType, StandardValues> standardValMap, String projectName) {
        float[] columnDefinitionSize = {40F, 80F, 30F, 120F, 40F, 80F, 40F, 130F, 90F};
        
        PdfPCell cell = null;

        PdfPTable table = new PdfPTable(columnDefinitionSize);
//            table.getDefaultCell().setBorder(1);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);

        //HEADER
        addTableHeader(table, "WATER DEMAND CALCULATIONS", 9, courierBold);

        //row1
        cell = new PdfPCell(new Phrase("Population criteria", courier));
        cell.setRowspan(11);
        cell.setBackgroundColor(Color.decode("#CCCCFF"));
        cell.setVerticalAlignment(ALIGN_CENTER);
        cell.setHorizontalAlignment(ALIGN_CENTER);
//        cell.setPaddingBottom(50);
        cell.setRotation(90);

        table.addCell(cell);

        addTableCell(table, StandardType.TWO_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.TWO_BHK)), courier);
        addTableCell(table, standardValMap.get(StandardType.TWO_BHK).getDescription(), courier);

        cell = new PdfPCell(new Phrase("Water Requirement", courier));
        cell.setRowspan(11);
        cell.setBackgroundColor(Color.decode("#CCCCFF"));
        cell.setVerticalAlignment(ALIGN_CENTER);
        cell.setHorizontalAlignment(ALIGN_CENTER);
//        cell.setPaddingBottom(50);
        cell.setRotation(90);
        table.addCell(cell);

        addTableCell(table,StandardType.TWO_BHK.getValue(), courier);
        addTableCell(table,DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, standardValMap.get(StandardType.WATER_DEMAND).getDescription(), courier);

        cell = new PdfPCell(new Phrase(projectName, courier));
        cell.setRowspan(11);
        cell.setBackgroundColor(Color.decode("#CCCCFF"));
        cell.setVerticalAlignment(ALIGN_MIDDLE);
        cell.setHorizontalAlignment(ALIGN_MIDDLE);
        table.addCell(cell);

        //row 2
        addTableCell(table,StandardType.THREE_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.THREE_BHK)), courier);
        addTableCell(table, "person average", courier);

        addTableCell(table,StandardType.THREE_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 3
        addTableCell(table, StandardType.THREE_N_HALF_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.THREE_N_HALF_BHK)), courier);
        addTableCell(table, "person average", courier);

        addTableCell(table, StandardType.THREE_N_HALF_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 4
        addTableCell(table, StandardType.FOUR_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.FOUR_BHK)), courier);
        addTableCell(table, "person average", courier);

        addTableCell(table, StandardType.FOUR_BHK.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 5
        addTableCell(table, StandardType.STUDIO.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.STUDIO)), courier);
        addTableCell(table, "person average", courier);

        addTableCell(table, StandardType.STUDIO.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 6
        addTableCell(table, StandardType.PODS.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.PODS)), courier);
        addTableCell(table, "person average", courier);

        addTableCell(table, StandardType.PODS.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 7
        addTableCell(table, StandardType.SERVANT.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.SERVANT)), courier);
        addTableCell(table, "per 4 BHK flat", courier);

        addTableCell(table, StandardType.SERVANT.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 8
        addTableCell(table, StandardType.DRIVER.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.DRIVER)), courier);
        addTableCell(table, "per 4 BHK flat", courier);

        addTableCell(table, StandardType.DRIVER.getValue(), courier);
        addTableCell(table, DataFormater.getFlushWaterRequirement(standardValMap.get(StandardType.WATER_DEMAND)), courier);
        addTableCell(table, "lpcd", courier);

        //row 9
        addTableCell(table, StandardType.LANDSCAPE.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.LANDSCAPE)), courier);
        addTableCell(table, "-", courier);

        addTableCell(table, StandardType.LANDSCAPE.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE)), courier);
        addTableCell(table, "ltr per sq. m of green area", courier);

        //row 10
        addTableCell(table, StandardType.SWIMMING_POOL.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.SWIMMING_POOL)), courier);
        addTableCell(table, "-", courier);

        addTableCell(table, StandardType.SWIMMING_POOL.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.SWIMMING_POOL_AREA_PERCENT)), courier);
        addTableCell(table, "of pool capacity", courier);

        //row 10
        addTableCell(table, StandardType.CLUB_HOUSE.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.CLUB_HOUSE)), courier);
        addTableCell(table, "sq. m. per person", courier);

        addTableCell(table, StandardType.CLUB_HOUSE.getValue(), courier);
        addTableCell(table, String.valueOf(DataFormater.getFlushWaterRequirement(standardValMap.get(StandardType.WATER_DEMAND))), courier);
        addTableCell(table, "of club capacity", courier);

        document.add(table);
    }

    private static void createWaterDemandDescription(Document document) {
        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        addTableCell(table, "s. no. ", courierBold,
                new CellStyle(2, 0, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        addTableCell(table, "description ", courierBold,
                new CellStyle(2, 0, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        addTableCell(table, "Number of flats ", courierBold,
                new CellStyle(2, 0, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        addTableCell(table, "Total population ", courierBold,
                new CellStyle(2, 0, Element.ALIGN_CENTER, Element.ALIGN_CENTER));

        addTableCell(table, "Dom Water Requirement ", courierBold,
                new CellStyle(0, 3, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        addTableCell(table, "Flushing Water Requirement ", courierBold,
                new CellStyle(0, 3, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        addTableCell(table, "Gross Water Req. ", courierBold);
        addTableCell(table, "Total Flow to Sewer", courierBold);

        addTableCell(table, "LPCD", courierBold);
        addTableCell(table, "LDP (A) ", courierBold);
        addTableCell(table, "Flow to Sewer  (%) ", courierBold);
        addTableCell(table, "LPCD ", courierBold);
        addTableCell(table, "LPD (B)", courierBold);
        addTableCell(table, "Flow to Sewer  (%) ", courierBold);
        addTableCell(table, "LPD", courierBold);
        addTableCell(table, "LPD", courierBold);

        document.add(table);
    }
    
    private static void createTowerWaterDemand(Document document, PdfData pdfData, HashMap<String, List<TowerWaterDemandRowData>> towerRowData) {
        createTableRow(document, "DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER,
                Element.ALIGN_CENTER, true, true);

        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        int totalWaterDemandDomestic = 0;
        int totalWaterDemandFlush = 0;
        int totalSewerWater = 0;
        
        for (String key : towerRowData.keySet()) {
            List<TowerWaterDemandRowData> flatDataRows = towerRowData.get(key);
            addTableCell(table, "Tower " + key, courierBold,
                    new CellStyle(0, 12, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
            for(int index = 0; index < flatDataRows.size(); index++) {
                TowerWaterDemandRowData flatDataRow = flatDataRows.get(index);
                if(!flatDataRow.getDescription().equals(DESCRIPTION_ROW_TOTAL)) {
                    createTowerFlatDataRow(flatDataRow, table, index);
                } else { 
                    createTowerFlatTotalRow(flatDataRow, table); 
                }
            }
        }
        document.add(table);
    }
    
    private static void createMiscellaneousTable(Document document, PdfData pdfData, DomesticWaterDemandData domesticWaterDemandData) {
        createTableRow(document, "MISCELLANEOUS ", 12, courierBold, Element.ALIGN_CENTER, Element.ALIGN_CENTER, false, false);

        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);

        addLandScapeRow(domesticWaterDemandData.getLandscapeDataRow(), 1, table);
        addSwimmingPoolRow(domesticWaterDemandData.getSwimmingPoolDataRow(), 2, table);
        addClubHouseRow(domesticWaterDemandData.getClubHouseDataRow(), 3, table);
        addMiscTotalRow(domesticWaterDemandData.getMiscDataRow(), table);
        document.add(table);
    }
    
    private static void addClubHouseRow(TowerWaterDemandRowData clubHouseDataRow, int index, PdfPTable table) {
        addTableCell(table, String.valueOf(index), courier, new CellStyle(true));
        addTableCell(table, clubHouseDataRow.getDescription(), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getQuantity()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getTotalPopulation()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getLpcdDom()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getLpda()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getFlowToSewerDom()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getLpcdFlush()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getLpdb()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getFlowToSewerFlush()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getGrossLPD()), courier);
        addTableCell(table, String.valueOf((int)clubHouseDataRow.getTotalFlowToSewer()), courier);
    }

    private static void addSwimmingPoolRow(TowerWaterDemandRowData swimmingPoolDataRow, int index, PdfPTable table) {
        addTableCell(table, String.valueOf(index),courier, new CellStyle(true));
        addTableCell(table, swimmingPoolDataRow.getDescription(),courier);
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getQuantity()), courier);
        addTableCell(table, "-", courier);
        addTableCell(table, swimmingPoolDataRow.getLpcdDom() + "%", courier);
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getLpda()), courier);
        
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getLpda()), courier);
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getFlowToSewerDom()), courier);
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getLpcdFlush()), courier);
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getLpdb()), courier);
        
        addTableCell(table, String.valueOf((int)swimmingPoolDataRow.getGrossLPD()), courier);
        addTableCell(table, "0", courier);
    }

    private static void addLandScapeRow(TowerWaterDemandRowData landscapeDataRow, int index, PdfPTable table) {
        addTableCell(table, String.valueOf(index),courier, new CellStyle(true));
        addTableCell(table, landscapeDataRow.getDescription(),courier);
        
        addTableCell(table, String.valueOf((int)landscapeDataRow.getQuantity()),courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getTotalPopulation()),courier);
        
        addTableCell(table, String.valueOf((int)landscapeDataRow.getLpcdDom()), courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getLpda()), courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getFlowToSewerDom()), courier);

        addTableCell(table, String.valueOf((int)landscapeDataRow.getLpcdFlush()), courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getLpdb()), courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getFlowToSewerFlush()), courier);

        addTableCell(table, String.valueOf((int)landscapeDataRow.getGrossLPD()), courier);
        addTableCell(table, String.valueOf((int)landscapeDataRow.getTotalFlowToSewer()), courier);
    }

    private static void addMiscTotalRow(TowerWaterDemandRowData miscDataRow, PdfPTable table) {
        addTableCell(table, "", courier);
        addTableCell(table, miscDataRow.getDescription(), courierBold);
        addTableCell(table, "", courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpda()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpdb()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getGrossLPD()), courierBold);
        addTableCell(table, String.valueOf((int)miscDataRow.getTotalFlowToSewer()), courierBold);
    }

    private static void createTotalWaterDemand(Document document, int totalDomesticWaterDemand, int totalFlushWaterDemand, int roundUpDomestic, int roundUpFlush) {
        
        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "Domestic Water Demand", courier, new CellStyle(1, 2));
        addTableCell(table, String.valueOf(totalDomesticWaterDemand), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 3));
        addTableCell(table, "say", courier);
        addTableCell(table, String.valueOf(roundUpDomestic), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier);
        
        //total flush water consumption
        addTableCell(table, "1", courier);
        addTableCell(table, "Flush Water Demand", courier, new CellStyle(1, 2));
        addTableCell(table, String.valueOf(totalFlushWaterDemand), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 3));
        addTableCell(table, "say", courier);
        addTableCell(table, String.valueOf(roundUpFlush), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier);
        
        //total dom + flush water consumption
        addTableCell(table, "1", courier);
        addTableCell(table, "Total Water Demand", courier, new CellStyle(1, 2));
        addTableCell(table, String.valueOf(totalDomesticWaterDemand + totalFlushWaterDemand), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 3));
        addTableCell(table, "say", courier);
        addTableCell(table, String.valueOf(MathUtils.roundUpToNearestTenThousand(totalDomesticWaterDemand + totalFlushWaterDemand)), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier);
        
        document.add(table);
    }

    private static void createSTPCapacity(Document document, PdfData pdfData, int totalSewerWaterDemand, int roundUpFlush) {
        
        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "STP Capacity", courier, new CellStyle(1, 2));
        addTableCell(table, String.valueOf(totalSewerWaterDemand), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 3));
        addTableCell(table, "=", courier);
        int roundUpStp = (int)Math.ceil((totalSewerWaterDemand)/1000);
        addTableCell(table, String.valueOf(roundUpStp), courier, new CellStyle(1, 2));
        addTableCell(table, "KLD", courier);
        
        addTableCell(table, "2", courier);
        addTableCell(table, "FLUSHING TANK", courier, new CellStyle(1, 2));
        
        int days = pdfData.getCapacityDetailsResidential().getuGTCapacity().getFlushingTank();
        addTableCell(table, String.valueOf(days), courier);
        addTableCell(table, "days storage", courier, new CellStyle(1, 6));
        int totalFlushInDay = days*roundUpFlush;
        addTableCell(table, String.valueOf(totalFlushInDay), courier, new CellStyle(1, 2));
        
        document.add(table);
    }
    
    private static int getTotalCapacity(List<ConsumptionDetail> consumptionDetails, ConsumptionType type, String waterConsumptionType) {
        int totalCapacity = 0;
        for(ConsumptionDetail detail : consumptionDetails) {
            if(detail.getType().equals(type)) {
                if(waterConsumptionType.equals(WATER_CONSUMPTION_TYPE_DOMESTIC)) {
                    totalCapacity += detail.getDomestic();
                } 
                if(waterConsumptionType.equals(WATER_CONSUMPTION_TYPE_FLUSH)) {
                    totalCapacity += detail.getFlush();
                } 
                if(waterConsumptionType.equals(WATER_CONSUMPTION_TYPE_SEWER)) {
                    totalCapacity += detail.getSewer();
                } 
            }
        }
        
        return totalCapacity;
    }

    private static void createOHTCapacity(Document document, PdfData pdfData) {
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        
        createTableRow(document,"OHT CAPACITY", COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,false);
        int domesticTank = (int)pdfData.getCapacityDetailsResidential().getoHTCapacity().getDomesticTank();
        
        for(TowerData towerData : pdfData.getTowersList()){
            createTableRow(document, towerData.getName(), COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,true);

            TowerWaterDemandRowData totalDemandForTower = WaterDemandCalculator.calculateTotalDataRowForTower(pdfData, towerData.getName());
            
            PdfPTable table = new PdfPTable(COLUMN__SIZES);
            table.setWidthPercentage(100);

            int fireTankCapacity = (int) pdfData.getCapacityDetailsResidential().getoHTCapacity().getFireFightingTank();
            addTableCell(table, "1", courier);
            addTableCell(table, "FIRE FIGHTING TANK", courier, new CellStyle(1, 3));
            addTableCell(table, "as per NBC", courier, new CellStyle(1, 5));
            addTableCell(table, String.valueOf(fireTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            addTableCell(table, "2", courier);
            addTableCell(table, "DOMESTIC TANK", courier, new CellStyle(1, 3));
            int domesticTankPercent = (int) pdfData.getCapacityDetailsResidential().getuGTCapacity().getDomesticTank();

            addTableCell(table, String.valueOf(domesticTankPercent), courier);
            addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 4));
            int domesticWaterCapacity = (int)((domesticTankPercent * totalDemandForTower.getLpda()) /100);
            addTableCell(table, String.valueOf(domesticWaterCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            addTableCell(table, "3", courier);
            addTableCell(table, "FLUSHING TANK", courier, new CellStyle(1, 3));
            //TODO as input OHT Flush %
            int flushTankPercent = 50;
            addTableCell(table, String.valueOf(flushTankPercent), courier);
            addTableCell(table, "% of Flushing water demand", courier, new CellStyle(1, 4));
            int flushTankCapacity = (int)((flushTankPercent * totalDemandForTower.getLpdb())/100);
            addTableCell(table, String.valueOf(flushTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            addTableCell(table, "", courier);
            addTableCell(table, DESCRIPTION_ROW_TOTAL, courier, new CellStyle(1, 8));
            addTableCell(table, String.valueOf(fireTankCapacity + domesticWaterCapacity + flushTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            document.add(table);
        }
    }

    private static void createUGRTable(Document document, PdfData pdfData, int totalOutsideDomestic, List<List<String>> groupedTowerNamesList) {
        int groupCnt = 0;
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        
        for(List<String> towerNames : groupedTowerNamesList) {
            createTableRow(document, "UGR TOWER " + String.join(" & ", towerNames), COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,true);
            PdfPTable table = new PdfPTable(COLUMN__SIZES);
            table.setWidthPercentage(100);

            int fireTankCapacity = (int) pdfData.getCapacityDetailsResidential().getuGTCapacity().getFireFightingTank();
            addTableCell(table, "1", courier);
            addTableCell(table, "FIRE FIGHTING TANK", courier, new CellStyle(1, 3));
            addTableCell(table, "as per project experience", courier, new CellStyle(1, 5));
            addTableCell(table, String.valueOf(fireTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);
            
            addTableCell(table, "2", courier);
            addTableCell(table, "RAW WATER TANK", courier, new CellStyle(1, 3));
            int rawTankPercent = (int) pdfData.getCapacityDetailsResidential().getuGTCapacity().getRawWaterTank();
            
            float totalLdpaForGroup=0;
            for(String towerName : towerNames){
                float ldpaForTower = WaterDemandCalculator.calculateTotalLpdaByTowerName(pdfData, towerName);
                totalLdpaForGroup += ldpaForTower;
            }
            int roundUpDomestic;
            if(groupCnt == 0) {
                roundUpDomestic = MathUtils.roundUpToNearestTenThousand((int)totalLdpaForGroup + totalOutsideDomestic);
            } else {
                roundUpDomestic = MathUtils.roundUpToNearestTenThousand((int)Math.ceil(totalLdpaForGroup));
            }
            
            addTableCell(table, String.valueOf(rawTankPercent), courier);
            addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 4));
            int rawWaterCapacity = (int)((rawTankPercent * roundUpDomestic) /100);
            addTableCell(table, String.valueOf(rawWaterCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            addTableCell(table, "3", courier);
            addTableCell(table, "DOMESTIC TANK", courier, new CellStyle(1, 3));
            //TODO as input UGR Domestic %
            int domesticTankPercent = 100;
            addTableCell(table, String.valueOf(domesticTankPercent), courier);
            addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 4));
            int domesticTankCapacity = (int)((domesticTankPercent * roundUpDomestic)/100);
            addTableCell(table, String.valueOf(domesticTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            addTableCell(table, "", courier);
            addTableCell(table, DESCRIPTION_ROW_TOTAL, courier, new CellStyle(1, 8));
            addTableCell(table, String.valueOf(fireTankCapacity + rawWaterCapacity + domesticTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            document.add(table);
            groupCnt ++;
        }
             
    }

    private static int getTotalDomesticWaterPerTower(List<ConsumptionDetail> consumptionDetails, List<String> towerNames) {
        double totalDomesticDemand = 0;
        for(ConsumptionDetail detail : consumptionDetails) {
            if(detail.getType() == ConsumptionType.TOWER && towerNames.contains(detail.getName())) {
                totalDomesticDemand += detail.getDomestic();
            }
        }
        return (int)totalDomesticDemand;
    }

    private static void createTowerFlatDataRow(TowerWaterDemandRowData flatDataRow, PdfPTable table, int index) {
        //s.no
        addTableCell(table, String.valueOf(index), courier);
        //flat type
        addTableCell(table, flatDataRow.getDescription(), courier);
        //number of flats
        addTableCell(table, String.valueOf(flatDataRow.getQuantity()), courier);
        //Total population of type
         addTableCell(table, String.valueOf(flatDataRow.getTotalPopulation()), courier);
        //domestic consumption per type
        addTableCell(table, String.valueOf((int)flatDataRow.getLpcdDom()), courier);
        //total domestic water consumption
        addTableCell(table, String.valueOf((int)flatDataRow.getLpda()), courier);
        //flow to sewer for domestin in %
        addTableCell(table, String.valueOf((int)flatDataRow.getFlowToSewerDom()), courier);
        //flushing water consumption per person
        addTableCell(table, String.valueOf((int)flatDataRow.getLpcdFlush()), courier);
        //Total flushing water consumption
        addTableCell(table, String.valueOf((int)flatDataRow.getLpdb()), courier);
        //flushing water flow to sewer in %
        addTableCell(table, String.valueOf((int)flatDataRow.getFlowToSewerFlush()), courier);
        //gross water consumption
        addTableCell(table, String.valueOf((int)flatDataRow.getGrossLPD()), courier);
        //flow to sewer
        addTableCell(table, String.valueOf((int)flatDataRow.getTotalFlowToSewer()), courier);
        
    }

    private static void createTowerFlatTotalRow(TowerWaterDemandRowData flatDataRow, PdfPTable table) {
        addTableCell(table, "", courier);
        addTableCell(table, DESCRIPTION_ROW_TOTAL, courierBold);
        addTableCell(table, String.valueOf((int)flatDataRow.getQuantity()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)flatDataRow.getLpda()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)flatDataRow.getLpdb()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)flatDataRow.getGrossLPD()), courierBold);
        addTableCell(table, String.valueOf((int)flatDataRow.getTotalFlowToSewer()), courierBold);
    }

    private static float getTotalLpda(PdfData pdfData) {
        float totalLpda = WaterDemandCalculator.calculateTotalLpdaForTowers(pdfData);
        float totalLpdb = WaterDemandCalculator.calculateTotalLpdbForTowers(pdfData);
        if(!pdfData.getOutsideAreaMap().isEmpty()) {
            TowerWaterDemandRowData miscDataRow =  WaterDemandCalculator.calculateWaterDemandForMiscTotalForTower(pdfData);
            totalLpda += miscDataRow.getLpda();
            
        }
        return totalLpda;
    }
    
    private static float getTotalLpdb(PdfData pdfData) {
        float totalLpdb = WaterDemandCalculator.calculateTotalLpdbForTowers(pdfData);
        if(!pdfData.getOutsideAreaMap().isEmpty()) {
            TowerWaterDemandRowData miscDataRow =  WaterDemandCalculator.calculateWaterDemandForMiscTotalForTower(pdfData);
            totalLpdb += miscDataRow.getLpdb();
        }
        return totalLpdb;
    }
    
    private static float getTotalFlowToSewer(PdfData pdfData) {
        float totalFlowToSewer = WaterDemandCalculator.calculateTotalFlowToSewerForTowers(pdfData);
        if(!pdfData.getOutsideAreaMap().isEmpty()) {
            TowerWaterDemandRowData miscDataRow =  WaterDemandCalculator.calculateWaterDemandForMiscTotalForTower(pdfData);
            totalFlowToSewer += miscDataRow.getTotalFlowToSewer();
        }
        return totalFlowToSewer;
    }

    private static void createFrontPage(PdfWriter pdfWriter, String projectName) throws IOException {
        // Get PdfContentByte from the writer for direct content manipulation
            PdfContentByte canvas = pdfWriter.getDirectContentUnder();

            // Add background image
            URL imageUrl = DataCalculator.class.getResource("/images/front-page.jpg");
            addBackgroundImage(canvas, imageUrl.getPath());
            // Add text at specific coordinates
            addTextAtPosition(canvas, "Water Demand Calculation", 50, 800, 18);
            addTextAtPosition(canvas, "for", 150, 780, 18);
            addTextAtPosition(canvas, projectName, 150, 450, 38);
            addTextAtPosition(canvas, "Created By", 50, 80, 18);
            addTextAtPosition(canvas, "Spectacular Eng Pvt. Ltd.", 50, 60, 18);
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
            java.util.logging.Logger.getLogger(PDFGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createProjectDetailsPage(Document document, PdfData projectData) {
        float tableCellPadding = 10f;
        boolean cellBorder = false;

        CellStyle styleLeft = new CellStyle(1, 1, Element.ALIGN_CENTER, Element.ALIGN_LEFT);
        styleLeft.setPadding(new CellPadding(0,0,0,tableCellPadding));
        
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 12);
        font.setColor(Color.BLUE);
        Font courier = FontFactory.getFont(FontFactory.COURIER, 10);
        Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 10);
        
        createResidentialPopulationDetails(document, styleLeft, cellBorder, projectData);
        createCommertialPopulationDetails(document, styleLeft, cellBorder, projectData);
    }

    private static void addEmptyTableRow(PdfPTable table, int length, boolean cellBorder) {
        for(int i=0;i< length; i++) {
            addTableCell(table, "", courierBold, cellBorder);
        }
    }

    private static void createResidentialPopulationDetails(Document document, CellStyle styleLeft, boolean cellBorder, PdfData projectData) {
        float[] columnDefinitionSize = {20F, 10F, 40F, 20F, 10F, 40F}; // 50F, 20F, 50F, 20F, 50F, 20F
        Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12);
        headerFont.setColor(Color.BLUE);
        Font courier = FontFactory.getFont(FontFactory.COURIER, 10);
        Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 10);
        
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        addTableHeader(table, "Residential Population Criteria(Person/Flat)", 14, headerFont, cellBorder);
        addTableHeader(table, "", columnDefinitionSize.length, headerFont, cellBorder);
        
        addEmptyTableRow(table, columnDefinitionSize.length, cellBorder);
        
        addTableCell(table, "1 BHK", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.ONE_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "2 BHK", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.TWO_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "2.5 BHK", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.TWO_N_HALF_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "3 BHK", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.THREE_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "3.5 BHK", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.THREE_N_HALF_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "4 BHK", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.FOUR_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "4.5 BHK", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.FOUR_AND_HALF_BHK)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "STUDIO", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.STUDIO)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
                
        addTableCell(table, "POD", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.PODS)), courier, styleLeft, cellBorder);
        addTableCell(table, "Person Average", courier, styleLeft, cellBorder);
        
        addTableCell(table, "SERVANT", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.SERVANT)), courier, styleLeft, cellBorder);
        addTableCell(table, "per 4 BHK flat", courier, styleLeft, cellBorder);
        
        addTableCell(table, "DRIVER ", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.DRIVER)), courier, styleLeft, cellBorder);
        addTableCell(table, "per 4 BHK flat", courier, styleLeft, cellBorder);
        
        addTableCell(table, "Landscape", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.LANDSCAPE)), courier, styleLeft, cellBorder);
        addTableCell(table, "", courier, styleLeft, cellBorder);

        addTableCell(table, "Swimming\nPool :", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.SWIMMING_POOL)), courier, styleLeft, cellBorder);
        addTableCell(table, "", courier, styleLeft, cellBorder);
        
        addTableCell(table, "Club\nHouse :", courierBold, styleLeft,cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.CLUB_HOUSE)), courier, styleLeft, cellBorder);
        addTableCell(table, "sq. mtr per person", courier, styleLeft, cellBorder);
        
        addTableCell(table, "", courierBold, styleLeft,cellBorder);
        addTableCell(table, "", courierBold, styleLeft,cellBorder);
        addTableCell(table, "", courierBold, styleLeft,cellBorder);
        addTableCell(table, "", courierBold, styleLeft,cellBorder);
            
        document.add(table);
    }

    private static void createCommertialPopulationDetails(Document document, CellStyle styleLeft, boolean cellBorder, PdfData projectData) {
        float[] columnDefinitionSize = {100F, 30F, 80F};
        Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12);
        headerFont.setColor(Color.BLUE);
        Font courier = FontFactory.getFont(FontFactory.COURIER, 10);
        Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 10);
        
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        table.setExtendLastRow(true, true);
        
        addTableHeader(table, "Commertial Population Criteria", 14, headerFont, cellBorder);
        addTableHeader(table, "", columnDefinitionSize.length, headerFont, cellBorder);
                
        addEmptyTableRow(table, columnDefinitionSize.length, cellBorder);
       
        addTableCell(table, "Office:", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.SQ_MTR_PER_PERSON_OFFICE)), courier, styleLeft, cellBorder);
        addTableCell(table, "Sq. Mtr. Per Person", courier, styleLeft, cellBorder);
        
        addTableCell(table, "Merchantial Ground floor", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND)), courier, styleLeft, cellBorder);
        addTableCell(table, "Sq. Mtr. Per Person", courier, styleLeft, cellBorder);
        
        addTableCell(table, "Merchantial Above Ground floor", courierBold, styleLeft, cellBorder);
        addTableCell(table, DataFormater.getStandardValue(projectData.getStandardValMap().get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND)), courier, styleLeft, cellBorder);
        addTableCell(table, "Sq. Mtr. Per Person", courier, styleLeft, cellBorder);
        
        document.add(table);
        
    }

}
