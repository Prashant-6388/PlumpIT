/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.initializer.DataInitializer;
import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.TowerData;
import com.pc.plumbit.model.WaterConsumption;
import com.pc.utils.DataFormater;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 *
 * @author Prashant
 */
public class PDFGenerator {
    public static final Font courierBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 8);
    public static final Font courier = FontFactory.getFont(FontFactory.COURIER, 8);
    
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
    
    
    public static void generateWaterDemandPDF(String location, HashMap<StandardType, StandardValues> standardValMap, 
            List<TowerData> towersList, List<TreeMap<StandardType, Integer>> officesList, 
            TreeMap<StandardType, Integer> outsideAreaMap, String landscapeAreaPercent, String swimmingPoolCapacityPercent) throws FileNotFoundException{
        File file = new File(location+"/test.pdf");

        Document document = new Document();
        // step 2:
        // we create 3 different writers that listen to the document
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        addWaterCriteriaTable(document, standardValMap, landscapeAreaPercent, swimmingPoolCapacityPercent);
//        createWaterDemandTable(document);
        Map<String, Integer> domsticWaterDemand = new HashMap<>();
        Map<String, Integer> flushWaterDemand = new HashMap<>();
        Map<String, Integer> stpWaterDemand = new HashMap<>();
        
        createWaterDemandDescription(document);
        createTowerWaterDemand(document, standardValMap, towersList);
        createMiscellaneousTable(document, standardValMap, outsideAreaMap, landscapeAreaPercent, swimmingPoolCapacityPercent);
        
//        for (Entry<String, Integer> entry : totalWaterDemand.entrySet()) {
//            Object key = entry.getKey();
//            Object val = entry.getValue();
//            System.out.println(key + " : " + val);
//        } 
        pdfWriter.flush();
        document.close();
    }
    
    private static void addWaterCriteriaTable(Document document, HashMap<StandardType, StandardValues> standardValMap, String landscapeAreaPercent, String swimmingPoolCapacityPercent) {
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

        cell = new PdfPCell(new Phrase("NYATI EVANIA", courier));
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
        addTableCell(table, String.valueOf(landscapeAreaPercent), courier);
        addTableCell(table, "ltr per sq. m of green area", courier);

        //row 10
        addTableCell(table, StandardType.SWIMMING_POOL.getValue(), courier);
        addTableCell(table, DataFormater.getStandardValue(standardValMap.get(StandardType.SWIMMING_POOL)), courier);
        addTableCell(table, "-", courier);

        addTableCell(table, StandardType.SWIMMING_POOL.getValue(), courier);
        addTableCell(table, String.valueOf(swimmingPoolCapacityPercent), courier);
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
        float[] columnDefinitionSize = {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
        PdfPTable table = new PdfPTable(columnDefinitionSize);
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
    
    private static void createTowerWaterDemand(Document document, HashMap<StandardType, StandardValues> standardValMap, List<TowerData> towersList) {
        createTableRow(document, "DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER,
                Element.ALIGN_CENTER, true, true);

        float[] columnDefinitionSize = {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setWidthPercentage(100);
        int totalWaterDemandDomestic = 0;
        int totalWaterDemandFlush = 0;
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
            Set<StandardType> keySet = flatsData.keySet();
            Iterator<StandardType> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                StandardType type = iterator.next();
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
                    index++;
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
            }
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
        addTableCell(table, text, font, new CellStyle(0, colSpan, vAlign, hAlign));
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

    private static void createMiscellaneousTable(Document document, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap, String landscapeAreaPercent, String swimmingPoolCapacityPercent) {
        createTableRow(document, "MISCELLANEOUS ", 12, courierBold, Element.ALIGN_CENTER, Element.ALIGN_CENTER, false, false);
        float[] columnDefinitionSize = {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setWidthPercentage(100);
        int landScapeArea = addLandScapeRow(table, standardValMap, outsideAreaMap, landscapeAreaPercent);
        double swimmingPoolArea = addSwimmingPoolRow(table, standardValMap, outsideAreaMap, swimmingPoolCapacityPercent);
        int clubHouseArea = addClubHouseRow(table, standardValMap, outsideAreaMap);
        document.add(table);
    }
    
    private static int addClubHouseRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap) {
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
        addTableCell(table,  "90", courier);
        int clubHouseFlushing = 20;
        addTableCell(table,  String.valueOf(clubHouseFlushing), courier);
        int clubHouseFlushingWater = clubHousePeople * 20;

        addTableCell(table, String.valueOf(clubHouseFlushingWater), courier);
        addTableCell(table, "100", courier);

        int totalClubHouseWaterReq = domesticWtrReqClubHouse + clubHouseFlushingWater;

        addTableCell(table, String.valueOf(totalClubHouseWaterReq), courier);

        double percentageToSewer = domesticWtrReqClubHouse * 0.9 + clubHouseFlushingWater;
        addTableCell(table, String.valueOf(percentageToSewer), courier);

        return totalClubHouseWaterReq;
    }

    private static double addSwimmingPoolRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap, String swimmingPoolCapacityPercent) {
        addTableCell(table,"2",courier, new CellStyle(true));
        addTableCell(table, StandardType.SWIMMING_POOL.getValue() + "Capacity (cum)",courier);
        int swimmingPoolArea = outsideAreaMap.get(StandardType.SWIMMING_POOL);
        addTableCell(table, String.valueOf(swimmingPoolArea), courier);
        addTableCell(table, "-", courier);
        double wtrReqSwimmingPool = Integer.parseInt(swimmingPoolCapacityPercent);
        addTableCell(table,  wtrReqSwimmingPool + "%", courier);
        //
        addTableCell(table, String.valueOf(swimmingPoolArea * (wtrReqSwimmingPool/100) * 1000), courier);
        addTableCell(table, "0", courier);
        addTableCell(table, "0", courier);
        addTableCell(table, "0", courier);
        addTableCell(table, "0", courier);
        addTableCell(table, String.valueOf(swimmingPoolArea * (wtrReqSwimmingPool/100) * 1000), courier);
        addTableCell(table, "0", courier);

        return wtrReqSwimmingPool;
    }

    private static int addLandScapeRow(PdfPTable table, HashMap<StandardType, StandardValues> standardValMap, TreeMap<StandardType, Integer> outsideAreaMap, String landscapeAreaPercent) {
        addTableCell(table,"1",courier, new CellStyle(true));
        addTableCell(table, StandardType.LANDSCAPE.getValue() + " (sq.m)",courier);
        
        int landscapeArea = outsideAreaMap.get(StandardType.LANDSCAPE);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        
        int peoplePerLandscape = (int)standardValMap.get(StandardType.LANDSCAPE).getValue();
        addTableCell(table, String.valueOf(peoplePerLandscape), courier);
        //
        addTableCell(table, String.valueOf(landscapeArea * peoplePerLandscape), courier);
        addTableCell(table, String.valueOf(landscapeArea * peoplePerLandscape), courier);

        int waterReqForLandscapeArea = Integer.parseInt(landscapeAreaPercent);
        addTableCell(table, String.valueOf(waterReqForLandscapeArea), courier);

        int landscapeFlushWaterReq = landscapeArea * waterReqForLandscapeArea;
        addTableCell(table, String.valueOf(landscapeFlushWaterReq), courier);
        addTableCell(table, "0", courier);

        addTableCell(table, String.valueOf(landscapeFlushWaterReq), courier);
        addTableCell(table, "0", courier);
        return landscapeFlushWaterReq;
    }

}
