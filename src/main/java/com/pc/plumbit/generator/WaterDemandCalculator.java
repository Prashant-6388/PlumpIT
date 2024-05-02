/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

/**
 *
 * @author Prashant
 */
public class WaterDemandCalculator {
    /*    private static void createMiscellaneousTable(Document document, TreeMap<ConsumptionType, Integer> miscAreaMap) {
        createTableRow(document, "MISCELLANEOUS ", 12, courierBold, Element.ALIGN_CENTER, Element.ALIGN_CENTER, false, false);
        float[] columnDefinitionSize = {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setWidthPercentage(100);
        int landScapeArea = addLandScapeRow(table, miscAreaMap);
        double swimmingPoolArea = addSwimmingPoolRow(table, miscAreaMap);
        int clubHouseArea = addClubHouseRow(table, miscAreaMap);
        document.add(table);
    }

    private static int addClubHouseRow(PdfPTable table, TreeMap<ConsumptionType, Integer> miscAreaMap) {
        addTableCell(table, "3", courier, new CellStyle(true));
        addTableCell(table, ConsumptionType.CLUB_HOUSE.getValue(), courier);
        int clubHouseArea = miscAreaMap.get(ConsumptionType.CLUB_HOUSE);
        addTableCell(table, String.valueOf(clubHouseArea), courier);

        int clubHousePeople = clubHouseArea/peoplePerType.get(ConsumptionType.CLUB_HOUSE);
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

    private static double addSwimmingPoolRow(PdfPTable table, TreeMap<ConsumptionType, Integer> miscAreaMap) {
        addTableCell(table,"2",courier, new CellStyle(true));
        addTableCell(table, ConsumptionType.SWIMMING_POOL.getValue() + "Capacity (cum)",courier);
        int swimmingPoolArea = miscAreaMap.get(ConsumptionType.SWIMMING_POOL);
        addTableCell(table, String.valueOf(swimmingPoolArea), courier);
        addTableCell(table, "-", courier);
        double wtrReqSwimmingPool = waterRequiredPerType.get(ConsumptionType.SWIMMING_POOL);
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

    private static int addLandScapeRow(PdfPTable table,
            TreeMap<ConsumptionType, Integer> miscAreaMap) {
        addTableCell(table,"1",courier, new CellStyle(true));
        addTableCell(table, ConsumptionType.LANDSCAPE.getValue() + " (sq.m)",courier);
        int landscapeArea = miscAreaMap.get(ConsumptionType.LANDSCAPE);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        addTableCell(table, String.valueOf(landscapeArea),courier);
        int peoplePerLandscape = peoplePerType.get(ConsumptionType.LANDSCAPE);
        addTableCell(table, String.valueOf(peoplePerLandscape), courier);
        //
        addTableCell(table, String.valueOf(landscapeArea * peoplePerLandscape), courier);
        addTableCell(table, String.valueOf(landscapeArea * peoplePerLandscape), courier);

        int waterReqForLandscapeArea = waterRequiredPerType.get(ConsumptionType.LANDSCAPE);
        addTableCell(table, String.valueOf(waterReqForLandscapeArea), courier);

        int landscapeWaterReq = landscapeArea * waterReqForLandscapeArea;
        addTableCell(table, String.valueOf(landscapeWaterReq), courier);
        addTableCell(table, "0", courier);

        addTableCell(table, String.valueOf(landscapeWaterReq), courier);
        addTableCell(table, "0", courier);
        return landscapeWaterReq;
    }

    private static void createTowerWaterDemand(Document document,
            TreeMap<ConsumptionType, Integer> towerFlatMap, int count) {

        createTableRow(document, "DAILY WATER DEMAND", 12, courierBold, Element.ALIGN_CENTER,
                Element.ALIGN_CENTER, true, true);

        float[] columnDefinitionSize = {30F, 80F, 50F, 65F, 35F, 50F, 40F, 35F, 50F, 40F, 50F, 60F};
        PdfPTable table = new PdfPTable(columnDefinitionSize);
        table.setWidthPercentage(100);
        for (int i = 0; i < count; i++) {
            addTableCell(table, "TOWER " + (i + 1), courierBold,
                    new CellStyle(0, 12, Element.ALIGN_CENTER, Element.ALIGN_CENTER));
            int index = 1;
            int totalDomesticConsumption = 0;
            int totalFlushingConsumption = 0;
            int totalGrossConsumption = 0;
            int totalWaterToSewer = 0;
            int totalFlats = 0;
            for (Entry<ConsumptionType, Integer> towerFlatMapEntry : towerFlatMap.entrySet()) {
                ConsumptionType consumptionType = towerFlatMapEntry.getKey();
                //s.no
                addTableCell(table, String.valueOf(index), courier);
                //flat type
                addTableCell(table, consumptionType.getValue(), courier);
                //number of flats
                int numOfFlats = towerFlatMapEntry.getValue();
                addTableCell(table, String.valueOf(numOfFlats), courier);
                //Total population of type
                Integer pplForType = peoplePerType.get(consumptionType);
                int totalPopulation = numOfFlats * pplForType;
                addTableCell(table, String.valueOf(totalPopulation), courier);
                //domestic consumption per type
                Integer domesticConsumptionForType = domesticWaterReqPerType.get(consumptionType);
                addTableCell(table, String.valueOf(domesticConsumptionForType), courier);
                //total domestic water consumption
                int totalDomesticWaterConsumption = totalPopulation * domesticConsumptionForType;
                addTableCell(table, String.valueOf(totalDomesticWaterConsumption), courier);
                //flow to sewer for domestin in %
                int domesticToSewerPercentage = 90;
                addTableCell(table, String.valueOf(domesticToSewerPercentage), courier);
                //flushing water consumption per person
                int flushWaterConsumptionPerPerson = flushingWaterReqPerType.get(consumptionType);
                addTableCell(table, String.valueOf(flushWaterConsumptionPerPerson), courier);
                //Total flushing water consumption
                int totalFlushWaterConsumption = flushWaterConsumptionPerPerson * totalPopulation;
                addTableCell(table, String.valueOf(totalFlushWaterConsumption), courier);
                //flushing water flow to sewer in %
                int flushingToSewerPercentage = 100;
                addTableCell(table, String.valueOf(flushingToSewerPercentage), courier);
                //gross water consumption
                int grossWaterConsumption =
                        totalDomesticWaterConsumption + totalFlushWaterConsumption;
                addTableCell(table, String.valueOf(grossWaterConsumption), courier);
                //flow to sewer
                double flowToSewer =
                        totalDomesticWaterConsumption * 0.9 + totalFlushWaterConsumption;
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
        }

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
    }*/
}
