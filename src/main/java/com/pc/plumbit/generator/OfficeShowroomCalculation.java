/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Document;
import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pc.initializer.DataInitializer;
import com.pc.plumbit.enums.StandardType;
import static com.pc.plumbit.generator.PDFUtils.addTableCell;
import static com.pc.plumbit.generator.PDFUtils.addTableHeader;
import static com.pc.plumbit.generator.PDFUtils.courier;
import static com.pc.plumbit.generator.PDFUtils.courierBold;
import com.pc.plumbit.model.commertial.CommertialWaterDemandRowData;
import com.pc.plumbit.model.input.OfficeData;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.utils.MathUtils;
import com.thoughtworks.xstream.XStream;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Prashant
 */
public class OfficeShowroomCalculation {

    private static final float[] COLUMN_SIZES = {30F, 78F, 60F, 50F, 65F, 35F, 33F, 45F, 30F, 40F, 45F, 50F, 50F};

    public static void main(String args[]) {
        try {
            System.out.println("creating office PDF");
            File selectedFile = new File("D:\\Programming\\GUI\\NB\\PlumbIT\\pdf\\Test_PC_3.pscs");
            XStream xstream = DataInitializer.getXstream();
            PdfData pdfData = (PdfData) xstream.fromXML(selectedFile);
            Document document = new Document();
            File outputFile = new File("d:\\Programming\\GUI\\NB\\PlumbIT\\pdf\\Test_PC_4.pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            createPDF(document, pdfData);
            pdfWriter.flush();
            document.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void createPDF(Document document, PdfData pdfData) {
        HashMap<String, Integer> totalVals = new HashMap<>();
        createOfficeDemandHeaderTable(document, pdfData);
        createDailyDemandTable(document);
        createShowroomDetails(document, pdfData, totalVals);
        createOfficeDetails(document, pdfData, totalVals);
        createMiscDetails(document, pdfData);
        createTotalDailyWaterDemand(document, pdfData);
        createUGRDemand(document, pdfData);
        createOHTDemand(document, pdfData);
        createSTPDemand(document, pdfData);
    }

    private static void createOfficeDemandHeaderTable(Document document, PdfData pdfData) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);

        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();

        //HEADER
        addTableHeader(table, "WATER DEMAND CALCULATIONS", 13, courierBold);

        //row1
        addTableCell(table, "Population criteria", courierBold, new CellStyle(6, 1, ALIGN_MIDDLE, ALIGN_CENTER, 90, Color.decode("#CCCCFF")));

        int sqMtrPerPersonAtGround = (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND).getValue();
        addTableCell(table, "SHOP/SHOWROOM", courier, new CellStyle(true));
        addTableCell(table, String.valueOf(sqMtrPerPersonAtGround), courier, new CellStyle(1, 2, true));
        addTableCell(table, standardValMap.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND).getDescription(), courier, new CellStyle(1, 2));

        addTableCell(table, "Water Requirement \n as per NBC 2016", courierBold, new CellStyle(6, 1, ALIGN_MIDDLE, ALIGN_CENTER, 90, Color.decode("#CCCCFF")));

        addTableCell(table, "SHOP/SHOWROOM", courier, new CellStyle(1, 2));
        addTableCell(table, "45", courier);
        addTableCell(table, "lpcd", courier);
        addTableCell(table, pdfData.getProjectName() + "-\nSHOWROOM/OFFICES", courier, new CellStyle(6, 2, ALIGN_MIDDLE, ALIGN_CENTER, 0, Color.decode("#CCCCFF")));

        //row 2
        int sqMtrPerPersonAboveGround = (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND).getValue();
        addTableCell(table, "SHOP/SHOWROOM", courier);
        addTableCell(table, String.valueOf(sqMtrPerPersonAboveGround), courier, new CellStyle(1, 2));
        addTableCell(table, standardValMap.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND).getDescription(), courier, new CellStyle(1, 2));
//
        addTableCell(table, "SHOP/SHOWROOM", courier, new CellStyle(1, 2));
        addTableCell(table, "45", courier);
        addTableCell(table, "lpcd", courier);

//        //row 3
        int sqMtrPerPersonOffice = (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_OFFICE).getValue();
        addTableCell(table, "OFFICE", courier);
        addTableCell(table, String.valueOf(sqMtrPerPersonOffice), courier, new CellStyle(1, 2));
        addTableCell(table, standardValMap.get(StandardType.SQ_MTR_PER_PERSON_OFFICE).getDescription(), courier, new CellStyle(1, 2));

        addTableCell(table, "OFFICE", courier, new CellStyle(1, 2));
        addTableCell(table, "45", courier);
        addTableCell(table, "lpcd", courier);

//        //row 4
        float nrOfShifts = 1.5f;
        addTableCell(table, "Shifts for office", courier);
        addTableCell(table, String.valueOf(nrOfShifts), courier, new CellStyle(1, 2));
        addTableCell(table, "Days", courier, new CellStyle(1, 2));
        addTableCell(table, "", courier, new CellStyle(1, 2));
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
//        
//        //row 5
        addTableCell(table, "Visitors", courier);
        addTableCell(table, "10% of all population", courier, new CellStyle(1, 2));
        addTableCell(table, "-", courier, new CellStyle(1, 2));
        addTableCell(table, "Visitor", courier, new CellStyle(1, 2));
        addTableCell(table, "15", courier);
        addTableCell(table, "lcpd", courier);
//        
//        //row 6
        addTableCell(table, "Maintenance & Security Staff", courier);
        addTableCell(table, "LS", courier, new CellStyle(1, 2));
        addTableCell(table, "", courier, new CellStyle(1, 2));
        addTableCell(table, "Maintenance & Security Staff", courier, new CellStyle(1, 2));
        addTableCell(table, "45", courier);
        addTableCell(table, "lcpd", courier);

        document.add(table);
    }

    private static void createDailyDemandTable(Document document) {

        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);

        //HEADER
        addTableHeader(table, "DAILY WATER DEMAND", 13, courierBold);

        //row1
        addTableCell(table, "Sr", courierBold, new CellStyle(3, 1, true));
        addTableCell(table, "Description", courierBold, new CellStyle(3, 1, true));
        addTableCell(table, "Type of Area", courierBold, new CellStyle(3, 1, true));
        addTableCell(table, "Area(m2)", courierBold, new CellStyle(3, 1, true));
        addTableCell(table, "Total Population", courierBold, new CellStyle(3, 1, true));
        addTableCell(table, "Dom Water Requirement", courierBold, new CellStyle(1, 3, true));
        addTableCell(table, "Flushing Water requirement", courierBold, new CellStyle(1, 3, true));
        addTableCell(table, "Gross Water Req. (D)=(A+B+C)", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "Flow to Sewer\n(90% DOM + 100% \nFLUSHING)", courierBold, new CellStyle(2, 1, true));
//        new CellStyle(1,2)
        addTableCell(table, "LPCD", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "LPD (A)", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "Flow to \nSewer in percent", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "LPCD", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "LPD (B)", courierBold, new CellStyle(2, 1, true));
        addTableCell(table, "Flow to \nSewer in percent", courierBold, new CellStyle(2, 1, true));
        //
        addTableCell(table, "LPD", courierBold, new CellStyle(true));
        addTableCell(table, "LPD", courierBold, new CellStyle(true));

        document.add(table);
    }

    private static void createShowroomDetails(Document document, PdfData pdfData, HashMap<String, Integer> totalVals) {
        
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        List<CommertialWaterDemandRowData> showRoomDataRows = WaterDemandCalculator.calculateWaterDemandForShowroom(pdfData);
        int index=1;
        for (CommertialWaterDemandRowData showRoomDataRow : showRoomDataRows) {
            if(!showRoomDataRow.getDescription().equals("Total")) {
                addTableCell(table, String.valueOf(index), courier);
                addTableCell(table, showRoomDataRow.getDescription(), courier);
                addTableCell(table, "Retails/ Showroom", courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getArea()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getTotalPopulation()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpcdDom()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpda()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getFlowToSewerDom()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpcdFlush()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpdb()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getFlowToSewerFlush()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getGrossLPD()), courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getTotalFlowToSewer()), courier);
            } else {
                addTableCell(table, "", courier);
                addTableCell(table, "Total", courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getTotalPopulation()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpda()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getLpdb()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getGrossLPD()), courierBold);
                addTableCell(table, String.valueOf((int)showRoomDataRow.getTotalFlowToSewer()), courierBold);
            }
        }

        /*

        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        List<OfficeData> showrooms = pdfData.getOfficesList().stream()
                .filter(officeData -> officeData.getType().equals(StandardType.SHOWROOM))
                .collect(Collectors.toList());
        int retailFloatingPopulation = 0;
        int totalRetailPopulation = 0;
        int flowToSewerDomestic = (int) pdfData.getCapacityDetailsCommertial().getsTPCapacity().getDomesticFlow();
        int flowToSewerFlush = (int) pdfData.getCapacityDetailsCommertial().getsTPCapacity().getFlushinhFlow();
        int totalLPDA = 0;
        int totalLPDB = 0;
        int totalLPDGross = 0;
        int totalLPDSewer = 0;

        if (!showrooms.isEmpty()) {
            addTableCell(table, "Retails/ Showroom", courierBold, new CellStyle(1, 13, true));
            int index = 0;
            for (; index < showrooms.size(); index++) {
                OfficeData showroom = showrooms.get(index);
                addTableCell(table, String.valueOf(index + 1), courier);
                addTableCell(table, showroom.getFloorNumer(), courier);
                addTableCell(table, "Retails/ Showroom", courier);
                addTableCell(table, String.valueOf(showroom.getArea()), courier);
                double population = getTotalPopulation(showroom, standardValMap);

                double totalPopulation = (int) population / 10;
                int totalWaterCommertial = (int) standardValMap.get(StandardType.WATER_DEMAND_COMMERTIAL).getValue();
                addTableCell(table, String.valueOf(totalPopulation), courier);
                int lpcdDomestic = totalWaterCommertial - 20;
                addTableCell(table, String.valueOf(lpcdDomestic), courier);
                int lpda = (int) totalPopulation * lpcdDomestic;
                addTableCell(table, String.valueOf(lpda), courier);

                addTableCell(table, String.valueOf(flowToSewerDomestic), courier);

                int lpcdFlushing = 20;
                addTableCell(table, String.valueOf(lpcdFlushing), courier);
                int lpdb = (int) totalPopulation * lpcdFlushing;
                addTableCell(table, String.valueOf(lpdb), courier);

                addTableCell(table, String.valueOf(flowToSewerFlush), courier);
                addTableCell(table, String.valueOf(lpda + lpdb), courier);

                int totalFlowToSewer = (int) ((lpda * flowToSewerDomestic / 100) + (lpdb * flowToSewerFlush / 100));
                addTableCell(table, String.valueOf(totalFlowToSewer), courier);

                retailFloatingPopulation += population;
                totalRetailPopulation += totalPopulation;
                totalLPDA += lpda;
                totalLPDB += lpdb;
                totalLPDGross += (lpda + lpdb);
                totalLPDSewer += totalFlowToSewer;
            }

            addTableCell(table, String.valueOf(index), courier);
            addTableCell(table, "Retail Floating Population", courier);
            addTableCell(table, "Retails/ Showroom", courier);
            addTableCell(table, "", courier);
            int floatingPopulation = (int) (retailFloatingPopulation * 0.9);
            addTableCell(table, String.valueOf(floatingPopulation), courier);
            int floatingLPCD = 5;
            addTableCell(table, String.valueOf(floatingLPCD), courier);
            int lpdaFloating = (int) floatingPopulation * 5;
            addTableCell(table, String.valueOf(lpdaFloating), courier);
            addTableCell(table, String.valueOf(flowToSewerDomestic), courier);
            int lpcdFloating = 10;
            addTableCell(table, String.valueOf(lpcdFloating), courier);
            int lpdbFloating = floatingPopulation * lpcdFloating;
            addTableCell(table, String.valueOf(lpdbFloating), courier);
            addTableCell(table, String.valueOf(flowToSewerFlush), courier);
            addTableCell(table, String.valueOf(lpdaFloating + lpdbFloating), courier);
            int totalFlowToSewerFloating = (int) ((lpdaFloating * flowToSewerDomestic / 100) + (lpdbFloating * flowToSewerFlush / 100));
            addTableCell(table, String.valueOf(totalFlowToSewerFloating), courier);
            
            totalRetailPopulation += floatingPopulation;
            totalLPDA += lpdaFloating;
            totalLPDB += lpdbFloating;
            totalLPDGross += (lpdaFloating + lpdbFloating);
            totalLPDSewer += totalFlowToSewerFloating;
            
            addTableCell(table, "", courier);
            addTableCell(table, "Total", courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalRetailPopulation), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDA), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDB), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDGross), courierBold);
            addTableCell(table, String.valueOf(totalLPDSewer), courierBold);
            
            totalVals.put(LPDA, totalLPDA);
            totalVals.put(LPDB, totalLPDB);
            totalVals.put(LPDGROSS, totalLPDGross);
            totalVals.put(LPDSEWER, totalLPDSewer);
        }*/

        document.add(table);
    }

    private static double getTotalPopulation(OfficeData officeData, HashMap<StandardType, StandardValues> standardValMap) {
//        if(officeData.getType().equals(StandardType.SHOWROOM) && officeData.getFloorNumer().equals("0")) {
//            return officeData.getArea() / standardValMap.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND).getValue();
//        } else if(officeData.getType().equals(StandardType.SHOWROOM) && !officeData.getFloorNumer().equals("0")){
//            return officeData.getArea() / standardValMap.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND).getValue();
//        } else {
//            return officeData.getArea() / standardValMap.get(StandardType.SQ_MTR_PER_PERSON_OFFICE).getValue();
//        }
        return (int) officeData.getArea()/officeData.getSqMtrPerPerson();
    }

    private static void createOfficeDetails(Document document, PdfData pdfData, HashMap<String, Integer> totalVals) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);

        addTableCell(table, "OFFICE", courierBold, new CellStyle(1, 13, true));
        List<CommertialWaterDemandRowData> officeDataRowList = WaterDemandCalculator.calculateWaterDemandForOffice(pdfData);
        int index = 1;
        for(CommertialWaterDemandRowData officeDataRow : officeDataRowList) {
            if(!officeDataRow.getDescription().equals("Total")) {
                addTableCell(table, String.valueOf(index), courier);
                addTableCell(table, officeDataRow.getDescription(), courier);
                addTableCell(table, officeDataRow.getTypeOfArea(), courier);
                if(!officeDataRow.getDescription().equals("Visitors")) {
                    addTableCell(table, String.valueOf((int)officeDataRow.getArea()), courier);
                } else {
                    addTableCell(table, String.valueOf((int)officeDataRow.getArea()+"%"), courier);
                }
                addTableCell(table, String.valueOf((int)officeDataRow.getTotalPopulation()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpcdDom()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpda()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getFlowToSewerDom()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpcdFlush()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpdb()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getFlowToSewerFlush()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getGrossLPD()), courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getTotalFlowToSewer()), courier);
            } else {
                addTableCell(table, "", courier);
                addTableCell(table, "Total", courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getTotalPopulation()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpda()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getLpdb()), courierBold);
                addTableCell(table, "", courier);
                addTableCell(table, String.valueOf((int)officeDataRow.getGrossLPD()), courierBold);
                addTableCell(table, String.valueOf((int)officeDataRow.getTotalFlowToSewer()), courierBold);
            }    
        }
        /*HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        List<OfficeData> offices = pdfData.getOfficesList().stream()
                .filter(officeData -> officeData.getType().equals(StandardType.OFFICE))
                .collect(Collectors.toList());
        int retailFloatingPopulation = 0;
        int totalRetailPopulation = 0;
        int flowToSewerDomestic = 90;
        int flowToSewerFlush = 100;
        int totalLPDA = 0;
        int totalLPDB = 0;
        int totalLPDGross = 0;
        int totalLPDSewer = 0;
        int totalOfficeArea = 0;

        if (!offices.isEmpty()) {
            addTableCell(table, "OFFICE", courierBold, new CellStyle(1, 13, true));
            int index = 0;
            for (; index < offices.size(); index++) {
                OfficeData office = offices.get(index);
                addTableCell(table, String.valueOf(index + 1), courier);
                addTableCell(table, office.getFloorNumer(), courier);
                addTableCell(table, "Offices", courier);
                addTableCell(table, String.valueOf(office.getArea()), courier);
                double population = getTotalPopulation(office, standardValMap);

                double totalPopulation = (int) population;

                addTableCell(table, String.valueOf(totalPopulation), courier);
                int totalWaterCommertial = (int) standardValMap.get(StandardType.WATER_DEMAND_COMMERTIAL).getValue();
                int lpcdDomestic = totalWaterCommertial - 20;
                addTableCell(table, String.valueOf(lpcdDomestic), courier);
                int lpda = (int) totalPopulation * lpcdDomestic;
                addTableCell(table, String.valueOf(lpda), courier);

                addTableCell(table, String.valueOf(flowToSewerDomestic), courier);

                int lpcdFlushing = 20;
                addTableCell(table, String.valueOf(lpcdFlushing), courier);
                int lpdb = (int) totalPopulation * lpcdFlushing;
                addTableCell(table, String.valueOf(lpdb), courier);

                addTableCell(table, String.valueOf(flowToSewerFlush), courier);
                addTableCell(table, String.valueOf(lpda + lpdb), courier);

                int totalFlowToSewer = (int) ((lpda * flowToSewerDomestic / 100) + (lpdb * flowToSewerFlush / 100));
                addTableCell(table, String.valueOf(totalFlowToSewer), courier);

                retailFloatingPopulation += population;
                totalRetailPopulation += totalPopulation;
                totalLPDA += lpda;
                totalLPDB += lpdb;
                totalLPDGross += (lpda + lpdb);
                totalLPDSewer += totalFlowToSewer;
                totalOfficeArea += office.getArea();
            }

            addTableCell(table, String.valueOf(index+1), courier);
            addTableCell(table, "Visitors", courier);
            addTableCell(table, "Offices", courier);
            int visitorArea = 10;
            addTableCell(table, String.valueOf(visitorArea) + "%", courier);

            //TODO : visitorPopulation 
            int visitorPopulation = (totalRetailPopulation/visitorArea);
            addTableCell(table, String.valueOf(visitorPopulation), courier);
            int domLPCD = 5;
            addTableCell(table, String.valueOf(domLPCD), courier);
            int lpdaVisitor = (int) visitorPopulation * domLPCD;
            addTableCell(table, String.valueOf(lpdaVisitor), courier);
            
            addTableCell(table, String.valueOf(flowToSewerDomestic), courier);
            
            int flushingLPCD = 10;
            addTableCell(table, String.valueOf(flushingLPCD), courier);
            int lpdbVisitor = visitorPopulation * flushingLPCD;
            addTableCell(table, String.valueOf(lpdbVisitor), courier);
            
            addTableCell(table, String.valueOf(flowToSewerFlush), courier);
            
            addTableCell(table, String.valueOf(lpdaVisitor + lpdbVisitor), courier);
            int totalFlowToSewerVisitor = (int) ((lpdaVisitor * flowToSewerDomestic / 100) + (lpdbVisitor * flowToSewerFlush / 100));
            addTableCell(table, String.valueOf(totalFlowToSewerVisitor), courier);
            
            totalRetailPopulation += visitorPopulation;
            totalLPDA += lpdaVisitor;
            totalLPDB += lpdbVisitor;
            totalLPDGross += (lpdaVisitor + lpdbVisitor);
            totalLPDSewer += totalFlowToSewerVisitor;
            
            addTableCell(table, "", courier);
            addTableCell(table, "Total", courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalRetailPopulation), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDA), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDB), courierBold);
            addTableCell(table, "", courier);
            addTableCell(table, String.valueOf(totalLPDGross), courierBold);
            addTableCell(table, String.valueOf(totalLPDSewer), courierBold);
            
            totalVals.put(LPDA, totalVals.get(LPDA)+totalLPDA);
            totalVals.put(LPDB, totalVals.get(LPDB)+totalLPDB);
            totalVals.put(LPDGROSS, totalVals.get(LPDGROSS)+totalLPDGross);
            totalVals.put(LPDSEWER, totalVals.get(LPDSEWER)+totalLPDSewer);
        }*/

        document.add(table);
    }

    private static void createMiscDetails(Document document, PdfData pdfData) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        List<CommertialWaterDemandRowData> miscRows = WaterDemandCalculator.calculateWaterDemandForOfficeMisc(pdfData);
        CommertialWaterDemandRowData miscDataRow = miscRows.get(0);

        addTableHeader(table, "MISCELLANEOUS", 13, courierBold);
        
        addTableCell(table, "1", courier);
        addTableCell(table, miscDataRow.getDescription(), courier);
        addTableCell(table, "", courier);
        addTableCell(table, "L.S.", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getTotalPopulation()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpcdDom()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpda()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getFlowToSewerDom()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpcdFlush()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpdb()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getFlowToSewerFlush()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getGrossLPD()), courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getTotalFlowToSewer()), courier);
        
        addTableCell(table, "", courier);
        addTableCell(table, "Total", courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getTotalPopulation()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpda()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getLpdb()), courierBold);
        addTableCell(table, "", courier);
        addTableCell(table, String.valueOf((int)miscDataRow.getGrossLPD()), courierBold);
        addTableCell(table, String.valueOf((int)miscDataRow.getTotalFlowToSewer()), courierBold);
        
        document.add(table);
    }

    private static void createTotalDailyWaterDemand(Document document, PdfData pdfData) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        CommertialWaterDemandRowData totalRowData = WaterDemandCalculator.getTotalCommertialData(pdfData);
        
        addTableHeader(table, "TOTAL DAILY WATER DEMAND", 13, courierBold);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "Domestic Water Demand", courier, new CellStyle(1, 3));
        int totalLdpa = (int)totalRowData.getLpda();
        addTableCell(table, String.valueOf(totalLdpa), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 4));
        addTableCell(table, "Say", courier);
        int roundedLPDA = MathUtils.roundUpToNearestTenThousand(totalLdpa);
        addTableCell(table, String.valueOf(roundedLPDA), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "2", courier);
        addTableCell(table, "Domestic Water Demand", courier, new CellStyle(1, 3));
        int totalLdpb = (int)totalRowData.getLpdb();
        addTableCell(table, String.valueOf(totalLdpb), courier, new CellStyle(1, 2));
        addTableCell(table, "Liters", courier, new CellStyle(1, 4));
        addTableCell(table, "Say", courier);
        int roundedLPDB = MathUtils.roundUpToNearestTenThousand(totalLdpb);
        addTableCell(table, String.valueOf(roundedLPDB), courier);
        addTableCell(table, "Liters", courier);
        
  
        addTableCell(table, "", courier);
        addTableCell(table, "Total Water Demand", courier, new CellStyle(1, 3));
        
        int totalWaterDemand = totalLdpa + totalLdpb;
        
        addTableCell(table, String.valueOf(totalWaterDemand), courierBold, new CellStyle(1, 2));
        addTableCell(table, "Liters", courierBold, new CellStyle(1, 4));
        addTableCell(table, "Say", courierBold);
        int roundedWaterDemand = MathUtils.roundUpToNearestTenThousand(roundedLPDA  +roundedLPDB);
        addTableCell(table, String.valueOf(roundedWaterDemand), courierBold);
        addTableCell(table, "Liters", courierBold);
        
        document.add(table);
    }

    private static void createUGRDemand(Document document, PdfData pdfData) {
        CommertialWaterDemandRowData totalRowData = WaterDemandCalculator.getTotalCommertialData(pdfData);
        int totalLdpa = (int)totalRowData.getLpda();
        int totalLdpb = (int)totalRowData.getLpdb();
        int roundedLPDA = MathUtils.roundUpToNearestTenThousand(totalLdpa);
        int roundedLPDB = MathUtils.roundUpToNearestTenThousand(totalLdpb);
        
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        float domTank = pdfData.getCapacityDetailsCommertial().getuGTCapacity().getDomesticTank();
        float rawTank = pdfData.getCapacityDetailsCommertial().getuGTCapacity().getRawWaterTank();
        float flushingTank = pdfData.getCapacityDetailsCommertial().getuGTCapacity().getFlushingTank();
        
        addTableHeader(table, "UGR CAPACITY", 13, courierBold);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "DOMESTIC TANK", courier, new CellStyle(1, 3));
        addTableCell(table, String.valueOf(domTank), courier);
        addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 6));
        int domTankCapacity =  (int)(roundedLPDA * domTank/100);
        addTableCell(table, String.valueOf(domTankCapacity), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "2", courier);
        addTableCell(table, "RAW TANK", courier, new CellStyle(1, 3));
        addTableCell(table, String.valueOf(rawTank), courier);
        addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 6));
        int rawTankCapacity = (int)(roundedLPDA * rawTank / 100);
        addTableCell(table, String.valueOf(rawTankCapacity), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "3", courier);
        addTableCell(table, "FLUSHING TANK", courier, new CellStyle(1, 3));
        addTableCell(table, String.valueOf(flushingTank), courier);
        addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 6));
        int flushingTankCapacity = (int)(roundedLPDB * flushingTank);
        addTableCell(table, String.valueOf(flushingTankCapacity), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "", courier);
        addTableCell(table, "Total", courierBold, new CellStyle(1,10,ALIGN_CENTER, ALIGN_CENTER));
        addTableCell(table, String.valueOf(domTankCapacity + rawTankCapacity + flushingTankCapacity), courierBold);
        addTableCell(table, "Liters", courierBold);
        
        document.add(table);
    }

    private static void createSTPDemand(Document document, PdfData pdfData) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        CommertialWaterDemandRowData totalRowData = WaterDemandCalculator.getTotalCommertialData(pdfData);
        int totalFlowToSewer = (int)totalRowData.getTotalFlowToSewer();
        
        int roundedFlowToSewer = MathUtils.roundUpToNearestTenThousand(totalFlowToSewer);
        
        addTableHeader(table, "STP CAPACITY", 13, courierBold);
        addTableCell(table, "1", courierBold);
        addTableCell(table, "STP CAPACITY", courierBold, new CellStyle(1, 4));
        addTableCell(table, String.valueOf(roundedFlowToSewer), courierBold, new CellStyle(1, 2));
        addTableCell(table, "Liters", courierBold, new CellStyle(1, 3));
        addTableCell(table, "=", courierBold);
        int stpKLD = (int)Math.ceil(roundedFlowToSewer/1000);
        addTableCell(table, String.valueOf(stpKLD), courierBold);
        addTableCell(table, "KLD", courierBold);
        
        document.add(table);
    }

    private static void createOHTDemand(Document document, PdfData pdfData) {
        PdfPTable table = new PdfPTable(COLUMN_SIZES);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(100);
        
        CommertialWaterDemandRowData totalRowData = WaterDemandCalculator.getTotalCommertialData(pdfData);
        int totalLdpa = (int)totalRowData.getLpda();
        int totalLdpb = (int)totalRowData.getLpdb();
        int roundedLPDA = MathUtils.roundUpToNearestTenThousand(totalLdpa);
        int roundedLPDB = MathUtils.roundUpToNearestTenThousand(totalLdpb);
        
        int ohtDomestic = (int)pdfData.getCapacityDetailsCommertial().getoHTCapacity().getDomesticTank();
        int ohtFlushing = (int)pdfData.getCapacityDetailsCommertial().getoHTCapacity().getFlushingTank();
        
        addTableHeader(table, "OVERHEAD TANK", 13, courierBold);
        
        addTableCell(table, "1", courier);
        addTableCell(table, "FIRE FIGHTING TANK", courier, new CellStyle(1, 3));
        addTableCell(table, "As Per NBC", courier, new CellStyle(1, 7));
        addTableCell(table, "N/A", courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "2", courier);
        addTableCell(table, "DOMESTIC WATER TANK", courier, new CellStyle(1, 3));
        addTableCell(table, String.valueOf(ohtDomestic), courier);
        addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 5));
        addTableCell(table, "say", courier);
        int domOHTCapacity = (int) roundedLPDA * ohtDomestic /100;
        addTableCell(table, String.valueOf(domOHTCapacity), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "3", courier);
        addTableCell(table, "FLUSHING WATER TANK", courier, new CellStyle(1, 3));
        addTableCell(table, String.valueOf(ohtFlushing), courier);
        addTableCell(table, "% of Domestic water demand", courier, new CellStyle(1, 5));
        addTableCell(table, "say", courier);
        int flushingOHTCapacity = (int) roundedLPDB * ohtFlushing /100;
        addTableCell(table, String.valueOf(flushingOHTCapacity), courier);
        addTableCell(table, "Liters", courier);
        
        addTableCell(table, "", courierBold);
        addTableCell(table, "Total", courierBold, new CellStyle(1, 10, ALIGN_CENTER, ALIGN_CENTER));
        int totalOHTCapacity = domOHTCapacity + flushingOHTCapacity;
        addTableCell(table, String.valueOf(totalOHTCapacity), courierBold);
        addTableCell(table, "Liters", courierBold);
        
        document.add(table);
    }
}
