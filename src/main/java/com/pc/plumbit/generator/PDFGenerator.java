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
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.plumbit.enums.ConsumptionType;
import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.ConsumptionDetail;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.TowerData;
import com.pc.utils.DataFormater;
import com.pc.utils.MathUtils;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
    
    public static final Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 8);
    public static final Font courier = FontFactory.getFont(FontFactory.COURIER, 8);
    private static final String WATER_CONSUMPTION_TYPE_DOMESTIC = "Domestic";
    private static final String WATER_CONSUMPTION_TYPE_FLUSH = "Flush";
    private static final String WATER_CONSUMPTION_TYPE_SEWER = "Sewer";
    private static final float[] COLUMN__SIZES =  {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
    
    public static boolean generatePDF(String location) throws FileNotFoundException {
        File file = new File("test.pdf");

        Document document = new Document();
        // step 2:
        // we create 3 different writers that listen to the document
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));

        // step 3: we open the document
        document.open();
//        createWaterDemandTable(document);
//        createWaterDemandDescription(document);
//        createTowerWaterDemand(document, towerFlatMap);
//        createMiscellaneousTable(document, miscAreaMap);
        pdfWriter.flush();
        document.close();
        return false;
    }
    
    
    public static void generateWaterDemandPDF(PdfData pdfData, JFrame parent) throws FileNotFoundException {
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

                addWaterCriteriaTable(document, pdfData.getStandardValMap(), pdfData.getProjectName());
                List<ConsumptionDetail> consumptionDetails = new ArrayList();

                createWaterDemandDescription(document);
                createTowerWaterDemand(document, pdfData.getStandardValMap(), pdfData.getTowersList(), consumptionDetails);
                if(!pdfData.getOutsideAreaMap().isEmpty()) {
                    createMiscellaneousTable(document, pdfData.getStandardValMap(), pdfData.getOutsideAreaMap(),consumptionDetails);
                }

                createTableRow(document, "Notes : ", 12, courier, Element.ALIGN_LEFT , Element.ALIGN_LEFT, false, false);
                createTableRow(document, "1. landscape area shall be confirmed by landscape consultant.", 12, courier, 
                        Element.ALIGN_LEFT , Element.ALIGN_LEFT, false, true);
                createTableRow(document, "TOTAL DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER , Element.ALIGN_CENTER, false, false);

                int totalDomesticWaterDemand=getTotalCapacity(consumptionDetails, ConsumptionType.TOWER, WATER_CONSUMPTION_TYPE_DOMESTIC);
                int totalFlushWaterDemand=getTotalCapacity(consumptionDetails, ConsumptionType.TOWER, WATER_CONSUMPTION_TYPE_FLUSH);
                int totalSewerWaterDemand=getTotalCapacity(consumptionDetails, ConsumptionType.TOWER, WATER_CONSUMPTION_TYPE_SEWER);

                List<ConsumptionDetail> outsideAreaDetails = consumptionDetails.stream()
                    .filter(consumptionDetail -> consumptionDetail.getType().equals(ConsumptionType.OUTSIDE_AREA))
                    .collect(Collectors.toList());

                int totalOutsideDomestic = 0;
                int totalOutsideFlush = 0;
                int totalOutsideSewer = 0;
                for(int i=0; i < outsideAreaDetails.size(); i++) {
                    ConsumptionDetail details = outsideAreaDetails.get(i);
                    totalOutsideDomestic += details.getDomestic();
                    totalOutsideFlush += details.getFlush();
                    totalOutsideSewer += details.getSewer();
                }

                int roundUpDomestic = MathUtils.roundUpToNearestTenThousand(totalDomesticWaterDemand + totalOutsideDomestic);
                int roundUpFlush = MathUtils.roundUpToNearestTenThousand(totalFlushWaterDemand + totalOutsideFlush);

                createTotalWaterDemand(document, totalDomesticWaterDemand + totalOutsideDomestic, totalFlushWaterDemand + totalOutsideFlush, roundUpDomestic, roundUpFlush);

                createTableRow(document, "STP Capacity", 12, courierBold, Element.ALIGN_CENTER , Element.ALIGN_CENTER, false, false);
                createSTPCapacity(document, pdfData.getStandardValMap(), totalSewerWaterDemand, roundUpFlush, consumptionDetails);
                createTableRow(document,"",12,null,0,0,false,false);
                createUGRTable(document, pdfData.getStandardValMap(), totalOutsideDomestic, pdfData.getGroupedTowerNamesList(), consumptionDetails);
                createOHTCapacity(document, pdfData.getStandardValMap(), consumptionDetails);
                /*for (ConsumptionDetail details : consumptionDetails) {
                    System.out.println(details.getName() + " : " + details.getDomestic() +", "+details.getFlush() 
                            + ", "+details.getSewer() +", "+details.getType());
                }*/ 
                
                pdfWriter.flush();
                document.close();
                JOptionPane.showMessageDialog(parent , "PDF Created successfully\nLocation: "+file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch(DocumentException | IOException e) {
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
        addTableHeader(table, 9, courierBold);

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
    
    private static void createTowerWaterDemand(Document document, HashMap<StandardType, StandardValues> standardValMap, List<TowerData> towersList,
        List<ConsumptionDetail> consumptionDetails) {
        createTableRow(document, "DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER,
                Element.ALIGN_CENTER, true, true);

        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        int totalWaterDemandDomestic = 0;
        int totalWaterDemandFlush = 0;
        int totalSewerWater = 0;
        for (int i = 0; i < towersList.size(); i++) {
            TowerData towerData = towersList.get(i);
            addTableCell(table, "Tower " + towerData.getName(), courierBold,
                    new CellStyle(0, 12, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
            int totalDomesticConsumption = 0;
            int totalFlushingConsumption = 0;
            int totalGrossConsumption = 0;
            int totalWaterToSewer = 0;
            int totalFlats = 0;
            int index = 1;
            TreeMap<StandardType, Integer> flatsData = towerData.getFlatsData();
            for (StandardType type : flatsData.keySet()) {
                Integer numOfFlats = flatsData.get(type);
                if (numOfFlats > 0) {
                    //s.no
                    addTableCell(table, String.valueOf(index), courier);
                    //flat type
                    addTableCell(table, type.getValue(), courier);
                    //number of flats
                    addTableCell(table, String.valueOf(numOfFlats), courier);
                    //Total population of type
                    StandardValues standardVal = standardValMap.get(type);
                    String standardValue = DataFormater.getStandardValue(standardVal);
                    int pplForType = Integer.parseInt(standardValue);
                    int totalPopulation = numOfFlats * pplForType;
                    addTableCell(table, String.valueOf(totalPopulation), courier);
                    //domestic consumption per type
                    int domesticConsumption = (int) standardValMap.get(StandardType.WATER_DEMAND).getValue() * 2 / 3;
                    addTableCell(table, String.valueOf(domesticConsumption), courier);
                    //total domestic water consumption
                    int totalDomesticWaterConsumption = totalPopulation * domesticConsumption;
                    addTableCell(table, String.valueOf(totalDomesticWaterConsumption), courier);
                    //flow to sewer for domestin in %
                    int domesticToSewerPercentage = 90;
                    addTableCell(table, String.valueOf(domesticToSewerPercentage), courier);
                    //flushing water consumption per person
                    int flushWaterConsumptionPerPerson = (int) standardValMap.get(StandardType.WATER_DEMAND).getValue() * 1 / 3;
                    addTableCell(table, String.valueOf(flushWaterConsumptionPerPerson), courier);
                    //Total flushing water consumption
                    int totalFlushWaterConsumption = flushWaterConsumptionPerPerson * totalPopulation;
                    addTableCell(table, String.valueOf(totalFlushWaterConsumption), courier);
                    //flushing water flow to sewer in %
                    int flushingToSewerPercentage = 100;
                    addTableCell(table, String.valueOf(flushingToSewerPercentage), courier);
                    //gross water consumption
                    int grossWaterConsumption
                            = totalDomesticWaterConsumption + totalFlushWaterConsumption;
                    addTableCell(table, String.valueOf(grossWaterConsumption), courier);
                    //flow to sewer
                    double flowToSewer
                            = totalDomesticWaterConsumption * 0.9 + totalFlushWaterConsumption;
                    addTableCell(table, String.valueOf(flowToSewer), courier);

                    totalDomesticConsumption += totalDomesticWaterConsumption;
                    totalFlushingConsumption += totalFlushWaterConsumption;
                    totalGrossConsumption += grossWaterConsumption;
                    totalWaterToSewer += flowToSewer;
                    totalFlats += numOfFlats;
                    
                    ConsumptionDetail consumptionDetail = new ConsumptionDetail(towerData.getName(), type.getValue(), totalDomesticWaterConsumption, totalFlushWaterConsumption, flowToSewer, ConsumptionType.TOWER);
                    consumptionDetails.add(consumptionDetail);
                    
                    index++;
                }
            }
            addTableCell(table, "", courier);
            addTableCell(table, "Total", courierBold);
            addTableCell(table, String.valueOf(totalFlats), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalDomesticConsumption), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalFlushingConsumption), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalGrossConsumption), courierBold);
            addTableCell(table, String.valueOf(totalWaterToSewer), courierBold);

            totalWaterDemandDomestic += totalDomesticConsumption;
            totalWaterDemandFlush += totalFlushingConsumption;
            totalSewerWater =+ totalWaterToSewer;
        }
//        totalWaterDemand.put("Domestic",totalWaterDemandDomestic);
//        totalWaterDemand.put("Flush",totalWaterDemandFlush);
        document.add(table);
    }
    
    private static void addTableHeader(PdfPTable table, int length, Font courier) {
        PdfPCell cell = new PdfPCell(new Phrase("WATER DEMAND CALCULATIONS", courier));
        cell.setColspan(length);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void createTableRow(Document document, String text, int colSpan, Font font,
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
        table.addCell(cell);
    }

    public static PdfPCell getBasicCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderWidthRight(0.5f);
//        cell.setPadding(5);
        return cell;
    }

    private static void createMiscellaneousTable(Document document, HashMap<StandardType, StandardValues> standardValMap, 
            TreeMap<StandardType, Integer> outsideAreaMap, List<ConsumptionDetail> consumptionDetails) {
        createTableRow(document, "MISCELLANEOUS ", 12, courierBold, Element.ALIGN_CENTER, Element.ALIGN_CENTER, false, false);

        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        int landScapeArea = addLandScapeRow(table, standardValMap, outsideAreaMap, consumptionDetails);
        double swimmingPoolArea = addSwimmingPoolRow(table, standardValMap, outsideAreaMap, consumptionDetails);
        int clubHouseArea = addClubHouseRow(table, standardValMap, outsideAreaMap, consumptionDetails);
        addMiscTotalRow(table, consumptionDetails);
        document.add(table);
    }
    
    private static int addClubHouseRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap,
            List<ConsumptionDetail> consumptionDetails) {
        addTableCell(table, "3", courier, new CellStyle(true));
        addTableCell(table, StandardType.CLUB_HOUSE.getValue(), courier);
        int clubHouseArea = outsideAreaMap.get(StandardType.CLUB_HOUSE);
        addTableCell(table, String.valueOf(clubHouseArea), courier);

        int clubHousePeople = (int) (clubHouseArea/standardValMap.get(StandardType.CLUB_HOUSE).getValue());
        addTableCell(table, String.valueOf(clubHousePeople), courier);

        int waterForClubHouse = 25;
        addTableCell(table,  String.valueOf(waterForClubHouse), courier);

        int domesticWtrReqClubHouse = clubHousePeople * waterForClubHouse;
        addTableCell(table,  String.valueOf(domesticWtrReqClubHouse), courier);
        //
        int domPercentageToSewer = (int)standardValMap.get(StandardType.FLOW_TO_SEWER_DOM).getValue();
        addTableCell(table,  String.valueOf(domPercentageToSewer), courier);
        
        int clubHouseFlushing = 20;
        addTableCell(table,  String.valueOf(clubHouseFlushing), courier);
        
        int clubHouseFlushingWater = clubHousePeople * 20;
        addTableCell(table, String.valueOf(clubHouseFlushingWater), courier);
        
        int flushPercentageToSewer = (int)standardValMap.get(StandardType.FLOW_TO_SEWER_FLS).getValue();
        addTableCell(table, String.valueOf(flushPercentageToSewer), courier);

        int totalClubHouseWaterReq = domesticWtrReqClubHouse + clubHouseFlushingWater;
        addTableCell(table, String.valueOf(totalClubHouseWaterReq), courier);

        double sewerWaterConsumption = (domesticWtrReqClubHouse * domPercentageToSewer/100) + (clubHouseFlushingWater * flushPercentageToSewer/100);
//        System.out.println("club sewer : "+sewerWaterConsumption);
        addTableCell(table, String.valueOf(sewerWaterConsumption), courier);

        ConsumptionDetail details = new ConsumptionDetail("Club house", "", domesticWtrReqClubHouse, clubHouseFlushingWater, sewerWaterConsumption, ConsumptionType.OUTSIDE_AREA);
        consumptionDetails.add(details);
        
        return totalClubHouseWaterReq;
    }

    private static double addSwimmingPoolRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap, 
            List<ConsumptionDetail> consumptionDetails) {
        addTableCell(table,"2",courier, new CellStyle(true));
        addTableCell(table, StandardType.SWIMMING_POOL.getValue() + "Capacity (cum)",courier);
        int swimmingPoolArea = outsideAreaMap.get(StandardType.SWIMMING_POOL);
        addTableCell(table, String.valueOf(swimmingPoolArea), courier);
        addTableCell(table, "-", courier);
        double wtrReqSwimmingPool = (double) standardValMap.get(StandardType.SWIMMING_POOL_AREA_PERCENT).getValue();
        addTableCell(table,  wtrReqSwimmingPool + "%", courier);
        //
        double domesticWaterConsumption = (int)(swimmingPoolArea * (wtrReqSwimmingPool/100) * 1000);
        addTableCell(table, String.valueOf(domesticWaterConsumption), courier);
        int flushWaterConsumption = 0;
        addTableCell(table, String.valueOf(flushWaterConsumption), courier);
        addTableCell(table, String.valueOf(flushWaterConsumption), courier);
        addTableCell(table, String.valueOf(flushWaterConsumption), courier);
        addTableCell(table, String.valueOf(flushWaterConsumption), courier);
        
        double totalWaterConsumption = domesticWaterConsumption + flushWaterConsumption;
        addTableCell(table, String.valueOf(totalWaterConsumption), courier);
        addTableCell(table, "0", courier);

        ConsumptionDetail details = new ConsumptionDetail("Swimming Pool", "", domesticWaterConsumption, flushWaterConsumption, 0, ConsumptionType.OUTSIDE_AREA);
        consumptionDetails.add(details);
        
        return wtrReqSwimmingPool;
    }

    private static int addLandScapeRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap, 
            List<ConsumptionDetail> consumptionDetails) {
        addTableCell(table,"1",courier, new CellStyle(true));
        addTableCell(table, StandardType.LANDSCAPE.getValue() + " (sq.m)",courier);
        
        int landscapeArea = outsideAreaMap.get(StandardType.LANDSCAPE);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        
        int peoplePerLandscape = (int)standardValMap.get(StandardType.LANDSCAPE).getValue();
        addTableCell(table, String.valueOf(peoplePerLandscape), courier);
        //
        int domesticWaterConsumption = landscapeArea * peoplePerLandscape;
        addTableCell(table, String.valueOf(domesticWaterConsumption), courier);
        addTableCell(table, String.valueOf(domesticWaterConsumption), courier);

        int waterReqForLandscapeArea = (int)standardValMap.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE).getValue();
        addTableCell(table, String.valueOf(waterReqForLandscapeArea), courier);

        int landscapeFlushWaterReq = landscapeArea * waterReqForLandscapeArea;
        addTableCell(table, String.valueOf(landscapeFlushWaterReq), courier);
        addTableCell(table, "0", courier);

        addTableCell(table, String.valueOf(landscapeFlushWaterReq), courier);
        addTableCell(table, "0", courier);
        
        ConsumptionDetail details = new ConsumptionDetail("Landscape", "", domesticWaterConsumption, landscapeFlushWaterReq, 0, ConsumptionType.OUTSIDE_AREA);
        consumptionDetails.add(details);
//        consumptionDetails.add()
        return landscapeFlushWaterReq;
    }

    private static void addMiscTotalRow(PdfPTable table, List<ConsumptionDetail> consumptionDetails) {

        List<ConsumptionDetail> outsideAreaDetails = consumptionDetails.stream()
                .filter(consumptionDetail -> consumptionDetail.getType().equals(ConsumptionType.OUTSIDE_AREA))
                .collect(Collectors.toList());
        
        double totalDomestic = 0;
        double totalFlush = 0;
        double totalSewer = 0;
        for(int i=0; i < outsideAreaDetails.size(); i++) {
            ConsumptionDetail details = outsideAreaDetails.get(i);
            totalDomestic += details.getDomestic();
            totalFlush += details.getFlush();
            totalSewer += details.getSewer();
        }
        
        addTableCell(table, "", courier);
        addTableCell(table, "Total", courierBold);
        addTableCell(table, "", courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf(totalDomestic), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf(totalFlush), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf(totalDomestic + totalFlush), courierBold);
        addTableCell(table, String.valueOf(totalSewer), courierBold);
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
        addTableCell(table, String.valueOf(roundUpDomestic+roundUpFlush), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier);
        
        document.add(table);
    }

    private static void createSTPCapacity(Document document, HashMap<StandardType, StandardValues> standardValMap, int totalSewerWaterDemand, int roundUpFlush, List<ConsumptionDetail> consumptionDetails) {
        List<ConsumptionDetail> outsideAreaDetails = consumptionDetails.stream()
        .filter(consumptionDetail -> consumptionDetail.getType().equals(ConsumptionType.OUTSIDE_AREA))
        .collect(Collectors.toList());
        
        int totalOutsideDomestic = 0;
        int totalOutsideFlush = 0;
        int totalOutsideSewer = 0;
        for(int i=0; i < outsideAreaDetails.size(); i++) {
            ConsumptionDetail details = outsideAreaDetails.get(i);
            totalOutsideDomestic += details.getDomestic();
            totalOutsideFlush += details.getFlush();
            totalOutsideSewer += details.getSewer();
        }
        
        PdfPTable table = new PdfPTable(COLUMN__SIZES);
        table.setWidthPercentage(100);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "STP Capacity", courier, new CellStyle(1, 2));
        addTableCell(table, String.valueOf(totalSewerWaterDemand + totalOutsideSewer), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 3));
        addTableCell(table, "=", courier);
        int roundUpStp = (int)Math.ceil((totalSewerWaterDemand + totalOutsideSewer)/1000);
        addTableCell(table, String.valueOf(roundUpStp), courier, new CellStyle(1, 2));
        addTableCell(table, "KLD", courier);
        
        addTableCell(table, "2", courier);
        addTableCell(table, "FLUSHING TANK", courier, new CellStyle(1, 2));
        int days = (int)standardValMap.get(StandardType.STP_FLUSHING_TANK).getValue();
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

    private static void createOHTCapacity(Document document, HashMap<StandardType, StandardValues> standardValMap, List<ConsumptionDetail> consumptionDetails) {
        HashMap<String, Integer> perTowerDomesticConsumption = new HashMap<>();
        HashMap<String, Integer> perTowerFlushConsumption = new HashMap<>();
        for(ConsumptionDetail detail : consumptionDetails) {
            if(detail.getType().equals(ConsumptionType.TOWER)) {
                String towerName = detail.getName();
                if(perTowerDomesticConsumption.containsKey(towerName)) {
                    int total = perTowerDomesticConsumption.get(towerName);
                    perTowerDomesticConsumption.put(towerName, total += detail.getDomestic());
                } else {
                    perTowerDomesticConsumption.put(towerName, (int)detail.getDomestic());
                }
                
                if(perTowerFlushConsumption.containsKey(towerName)) {
                    int total = perTowerFlushConsumption.get(towerName);
                    perTowerFlushConsumption.put(towerName, total += detail.getFlush());
                } else {
                    perTowerFlushConsumption.put(towerName, (int)detail.getFlush());
                }
            }
        }
        int domesticTank = (int)standardValMap.get(StandardType.DOMESTIC_TANK).getValue();
        
        createTableRow(document,"OHT CAPACITY", COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,false);
        if(!perTowerDomesticConsumption.isEmpty()) {
            for(String key : perTowerDomesticConsumption.keySet()) {
                createTableRow(document, key, COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,true);

                PdfPTable table = new PdfPTable(COLUMN__SIZES);
                table.setWidthPercentage(100);
                
                int fireTankCapacity = DataFormater.getNormalizedStandardValue(standardValMap.get(StandardType.OHT_FIRE_FIGHTING_TANK));
                addTableCell(table, "1", courier);
                addTableCell(table, "FIRE FIGHTING TANK", courier, new CellStyle(1, 3));
                addTableCell(table, "as per NBC", courier, new CellStyle(1, 5));
                addTableCell(table, String.valueOf(fireTankCapacity), courier, new CellStyle(1, 2));
                addTableCell(table, "Liters", courier);
             
                addTableCell(table, "2", courier);
                addTableCell(table, "DOMESTIC TANK", courier, new CellStyle(1, 3));
                int domesticTankPercent = DataFormater.getNormalizedStandardValue(standardValMap.get(StandardType.DOMESTIC_TANK));
                
                addTableCell(table, String.valueOf(domesticTankPercent), courier);
                addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 4));
                int domesticWaterCapacity = (int)((domesticTankPercent * perTowerDomesticConsumption.get(key)) /100);
                addTableCell(table, String.valueOf(domesticWaterCapacity), courier, new CellStyle(1, 2));
                addTableCell(table, "Liters", courier);
                
                addTableCell(table, "3", courier);
                addTableCell(table, "FLUSHING TANK", courier, new CellStyle(1, 3));
                //TODO as input OHT Flush %
                int flushTankPercent = 50;
                addTableCell(table, String.valueOf(flushTankPercent), courier);
                addTableCell(table, "% of Flushing water demand", courier, new CellStyle(1, 4));
                int flushTankCapacity = (int)((flushTankPercent * perTowerFlushConsumption.get(key))/100);
                addTableCell(table, String.valueOf(flushTankCapacity), courier, new CellStyle(1, 2));
                addTableCell(table, "Liters", courier);
                
                addTableCell(table, "", courier);
                addTableCell(table, "Total", courier, new CellStyle(1, 8));
                addTableCell(table, String.valueOf(fireTankCapacity + domesticWaterCapacity + flushTankCapacity), courier, new CellStyle(1, 2));
                addTableCell(table, "Liters", courier);
                
                document.add(table);
            }
        }
    }

    private static void createUGRTable(Document document, HashMap<StandardType, StandardValues> standardValMap, int totalOutsideDomestic, List<List<String>> groupedTowerNamesList, List<ConsumptionDetail> consumptionDetails) {
        int groupCnt = 0;
        for(List<String> towerNames : groupedTowerNamesList) {
            createTableRow(document, "UGR TOWER " + String.join(" & ", towerNames), COLUMN__SIZES.length, courierBold, ALIGN_CENTER, ALIGN_CENTER,true,true);
            PdfPTable table = new PdfPTable(COLUMN__SIZES);
            table.setWidthPercentage(100);

            int fireTankCapacity = DataFormater.getNormalizedStandardValue(standardValMap.get(StandardType.UGR_FIRE_FIGHTING_TANK));
            addTableCell(table, "1", courier);
            addTableCell(table, "FIRE FIGHTING TANK", courier, new CellStyle(1, 3));
            addTableCell(table, "as per project experience", courier, new CellStyle(1, 5));
            addTableCell(table, String.valueOf(fireTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);
            
            addTableCell(table, "2", courier);
            addTableCell(table, "RAW WATER TANK", courier, new CellStyle(1, 3));
            int rawTankPercent = DataFormater.getNormalizedStandardValue(standardValMap.get(StandardType.RAW_WATER_TANK));

            int totalDomesticWater = getTotalDomesticWaterPerTower(consumptionDetails,towerNames);
            int roundUpDomestic;
            if(groupCnt == 0) {
                roundUpDomestic = MathUtils.roundUpToNearestTenThousand(totalDomesticWater + totalOutsideDomestic);
            } else {
                roundUpDomestic = MathUtils.roundUpToNearestTenThousand(totalDomesticWater);
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
            addTableCell(table, "Total", courier, new CellStyle(1, 8));
            addTableCell(table, String.valueOf(fireTankCapacity + rawWaterCapacity + domesticTankCapacity), courier, new CellStyle(1, 2));
            addTableCell(table, "Liters", courier);

            document.add(table);
            groupCnt ++;
        }
             
    }

    private static int getTotalDomesticWaterPerTower(List<ConsumptionDetail> consumptionDetails, List<String> towerNames) {
        double totalDomesticDemand = 0;
        for(ConsumptionDetail detail : consumptionDetails){
            if(detail.getType() == ConsumptionType.TOWER && towerNames.contains(detail.getName())) {
                totalDomesticDemand += detail.getDomestic();
            }
        }
        return (int)totalDomesticDemand;
    }
}
