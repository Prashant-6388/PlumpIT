/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import java.util.List;
import java.util.ArrayList;
import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.commertial.CommertialWaterDemandRowData;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.domestic.TowerWaterDemandRowData;
import com.pc.plumbit.model.input.OfficeData;
import com.pc.plumbit.model.input.TowerData;
import com.pc.utils.DataFormater;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author Prashant
 */
public class WaterDemandCalculator {
    
    public static final String DESCRIPTION_ROW_TOTAL = "Total";

    public static HashMap<String, List<TowerWaterDemandRowData>> calculateWaterDemandForTowers(PdfData pdfData) {
        HashMap<String, List<TowerWaterDemandRowData>> towerDemandList = new HashMap<>();
        List<TowerData> towersList = pdfData.getTowersList();
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        
        //same value of all towers
        float domesticToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getDomesticFlow();
        float flushingToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getFlushingFlow();
        
        for (int i = 0; i < towersList.size(); i++) {
            TowerData towerData = towersList.get(i);
            List<TowerWaterDemandRowData> perTowerWaterDemandList = getWaterDemandForTower(towerData, standardValMap, domesticToSewerPercentage, flushingToSewerPercentage);
            TowerWaterDemandRowData totalWaterDemandForTower = getTotalWaterDemandForTower(perTowerWaterDemandList);
            perTowerWaterDemandList.add(totalWaterDemandForTower);
            towerDemandList.put(towerData.getName(), perTowerWaterDemandList);
        }
        return towerDemandList;
    }
    
    public static float calculateTotalLpdaForTowers(PdfData pdfData) {
       HashMap<String, List<TowerWaterDemandRowData>> towersDataRowMap = calculateWaterDemandForTowers(pdfData);
       float totalLpda=0;
       for(String key : towersDataRowMap.keySet()) {
           List<TowerWaterDemandRowData> towersFlatDataRows = towersDataRowMap.get(key);
           Optional<TowerWaterDemandRowData> totalRow = towersFlatDataRows.stream()
                   .filter(towerRow -> towerRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                   .findFirst();
           if(totalRow.isPresent()) {
               TowerWaterDemandRowData totalDataRow = totalRow.get();
               totalLpda += totalDataRow.getLpda();
           }
       }
       return totalLpda;
    }
    
    public static float calculateTotalLpdaByTowerName(PdfData pdfData, String towerName) {
        Optional<TowerData> tower = pdfData.getTowersList().stream().filter(towerData -> towerData.getName().equals(towerName)).findFirst();
        if(tower.isPresent()) {
            float domesticToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getDomesticFlow();
            float flushingToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getFlushingFlow();
            TowerData towerData = tower.get();
            List<TowerWaterDemandRowData> perTowerWaterDemandList = getWaterDemandForTower(towerData, pdfData.getStandardValMap(), domesticToSewerPercentage, flushingToSewerPercentage);
            TowerWaterDemandRowData totalWaterDemandForTower = getTotalWaterDemandForTower(perTowerWaterDemandList);
            return totalWaterDemandForTower.getLpda();
        } else {
            return 0;
        }
    }
    
    public static TowerWaterDemandRowData calculateTotalDataRowForTower(PdfData pdfData, String towerName) {
    Optional<TowerData> tower = pdfData.getTowersList().stream().filter(towerData -> towerData.getName().equals(towerName)).findFirst();
    if(tower.isPresent()) {
        float domesticToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getDomesticFlow();
        float flushingToSewerPercentage = pdfData.getCapacityDetailsResidential().getsTPCapacity().getFlushingFlow();
        TowerData towerData = tower.get();
        List<TowerWaterDemandRowData> perTowerWaterDemandList = getWaterDemandForTower(towerData, pdfData.getStandardValMap(), domesticToSewerPercentage, flushingToSewerPercentage);
        TowerWaterDemandRowData totalWaterDemandForTower = getTotalWaterDemandForTower(perTowerWaterDemandList);
        return totalWaterDemandForTower;
    } else {
        return null;
    }
}

    public static float calculateTotalLpdbForTowers(PdfData pdfData) {
       HashMap<String, List<TowerWaterDemandRowData>> towersDataRowMap = calculateWaterDemandForTowers(pdfData);
       float totalLpda=0;
       for(String key : towersDataRowMap.keySet()) {
           List<TowerWaterDemandRowData> towersFlatDataRows = towersDataRowMap.get(key);
           Optional<TowerWaterDemandRowData> totalRow = towersFlatDataRows.stream()
                   .filter(towerRow -> towerRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                   .findFirst();
           if(totalRow.isPresent()) {
               TowerWaterDemandRowData totalDataRow = totalRow.get();
               totalLpda += totalDataRow.getLpdb();
           }
       }
       return totalLpda;
    }
    
    public static float calculateTotalFlowToSewerForTowers(PdfData pdfData) {
       HashMap<String, List<TowerWaterDemandRowData>> towersDataRowMap = calculateWaterDemandForTowers(pdfData);
       float totalFlowToSewer=0;
       for(String key : towersDataRowMap.keySet()) {
           List<TowerWaterDemandRowData> towersFlatDataRows = towersDataRowMap.get(key);
           Optional<TowerWaterDemandRowData> totalRow = towersFlatDataRows.stream()
                   .filter(towerRow -> towerRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                   .findFirst();
           if(totalRow.isPresent()) {
               TowerWaterDemandRowData totalDataRow = totalRow.get();
               totalFlowToSewer += totalDataRow.getTotalFlowToSewer();
           }
       }
       return totalFlowToSewer;
    }
        
    public static List<CommertialWaterDemandRowData> calculateWaterDemandForCommertial(PdfData pdfData){
        List<CommertialWaterDemandRowData> commertialWaterDemand = new ArrayList<>();
        
        
        return commertialWaterDemand;
    }

    public static TowerWaterDemandRowData calculateWaterDemandForMiscTotalForTower(PdfData pdfData) {
        TowerWaterDemandRowData miscTowerWaterDemand = new TowerWaterDemandRowData();
        TowerWaterDemandRowData landscapeWaterDemand = calculateWaterDemandForLandscape(pdfData);
        TowerWaterDemandRowData swimmingPoolscapeWaterDemand = calculateWaterDemandForSwimmingPool(pdfData);
        TowerWaterDemandRowData clubHouseWaterDemand = calculateWaterDemandForClubHouse(pdfData);
        
        miscTowerWaterDemand.setDescription(DESCRIPTION_ROW_TOTAL);
        miscTowerWaterDemand.setLpda(landscapeWaterDemand.getLpda() + swimmingPoolscapeWaterDemand.getLpda() + clubHouseWaterDemand.getLpda());
        miscTowerWaterDemand.setLpdb(landscapeWaterDemand.getLpdb() + swimmingPoolscapeWaterDemand.getLpdb() + clubHouseWaterDemand.getLpdb());
        miscTowerWaterDemand.setGrossLPD(landscapeWaterDemand.getGrossLPD() + swimmingPoolscapeWaterDemand.getGrossLPD() + clubHouseWaterDemand.getGrossLPD());
        miscTowerWaterDemand.setTotalFlowToSewer(landscapeWaterDemand.getTotalFlowToSewer() + swimmingPoolscapeWaterDemand.getTotalFlowToSewer() + clubHouseWaterDemand.getTotalFlowToSewer());
        
        return miscTowerWaterDemand;
    }
    
    public static TowerWaterDemandRowData calculateWaterDemandForLandscape(PdfData pdfData) {
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        TowerWaterDemandRowData landscapeWaterDemandRowData = new TowerWaterDemandRowData();
        landscapeWaterDemandRowData.setDescription(StandardType.LANDSCAPE.getValue() + " (sq.m)");
        int landscapeArea = pdfData.getOutsideAreaMap().get(StandardType.LANDSCAPE);
        landscapeWaterDemandRowData.setQuantity(landscapeArea);
        
//        int peoplePerLandscape = (int)standardValMap.get(StandardType.LANDSCAPE).getValue();
//        
//        int totalPopulation = landscapeArea * peoplePerLandscape;
        landscapeWaterDemandRowData.setTotalPopulation(landscapeArea);
        
        landscapeWaterDemandRowData.setLpcdDom(0);
        landscapeWaterDemandRowData.setLpda(0);
        landscapeWaterDemandRowData.setFlowToSewerDom(0);


        int waterReqForLandscapeArea = (int)standardValMap.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE).getValue();
        landscapeWaterDemandRowData.setLpcdFlush(waterReqForLandscapeArea);

        int landscapeFlushWaterReq = landscapeArea * waterReqForLandscapeArea;
        landscapeWaterDemandRowData.setLpdb(landscapeFlushWaterReq);
        landscapeWaterDemandRowData.setFlowToSewerFlush(0);
        
        landscapeWaterDemandRowData.setGrossLPD(landscapeFlushWaterReq);
        return landscapeWaterDemandRowData;
    }

    public static TowerWaterDemandRowData calculateWaterDemandForSwimmingPool(PdfData pdfData) {
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        TowerWaterDemandRowData swimmingPoolWaterDemandRowData = new TowerWaterDemandRowData();
        swimmingPoolWaterDemandRowData.setDescription(StandardType.SWIMMING_POOL.getValue() + "Capacity (cum)");
        int swimmingPoolArea = pdfData.getOutsideAreaMap().get(StandardType.SWIMMING_POOL);
        swimmingPoolWaterDemandRowData.setQuantity(swimmingPoolArea);
        swimmingPoolWaterDemandRowData.setTotalPopulation(0);
        double wtrReqSwimmingPool = (double) standardValMap.get(StandardType.SWIMMING_POOL_AREA_PERCENT).getValue();
        swimmingPoolWaterDemandRowData.setLpcdDom((float)wtrReqSwimmingPool);
        
        double lpda = (int)(swimmingPoolArea * (wtrReqSwimmingPool/100) * 1000);
        swimmingPoolWaterDemandRowData.setLpda((float)lpda);
        swimmingPoolWaterDemandRowData.setFlowToSewerDom(0);
        swimmingPoolWaterDemandRowData.setLpcdFlush(0);
        swimmingPoolWaterDemandRowData.setLpdb(0);
        swimmingPoolWaterDemandRowData.setFlowToSewerFlush(0);
        swimmingPoolWaterDemandRowData.setGrossLPD((float)lpda);
        swimmingPoolWaterDemandRowData.setTotalFlowToSewer(0);

        return swimmingPoolWaterDemandRowData;
    }
    
    public static TowerWaterDemandRowData calculateWaterDemandForClubHouse(PdfData pdfData) {
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        TowerWaterDemandRowData clubHouseWaterDemandRowData = new TowerWaterDemandRowData();
        clubHouseWaterDemandRowData.setDescription(StandardType.CLUB_HOUSE.getValue());

        int clubHouseArea = pdfData.getOutsideAreaMap().get(StandardType.CLUB_HOUSE);
        clubHouseWaterDemandRowData.setQuantity(clubHouseArea);
        
        int clubHousePeople = (int) (clubHouseArea/standardValMap.get(StandardType.CLUB_HOUSE).getValue());
        clubHouseWaterDemandRowData.setTotalPopulation(clubHousePeople);

        //TODO hardcoded value
        int lpcdDom = 25;
        clubHouseWaterDemandRowData.setLpcdDom(lpcdDom);

        int lpda = clubHousePeople * lpcdDom;
        clubHouseWaterDemandRowData.setLpda(lpda);
        //
        int domPercentageToSewer = (int) pdfData.getCapacityDetailsResidential().getsTPCapacity().getDomesticFlow();
        clubHouseWaterDemandRowData.setFlowToSewerDom(domPercentageToSewer);
        
        //TODO hardcoded value
        int lpcdFlushing = 20;
        clubHouseWaterDemandRowData.setLpcdFlush(lpcdFlushing);
        
        int lpdb = clubHousePeople * lpcdFlushing;
        clubHouseWaterDemandRowData.setLpdb(lpdb);
        
        int flushPercentageToSewer = (int) pdfData.getCapacityDetailsResidential().getsTPCapacity().getFlushingFlow();
        clubHouseWaterDemandRowData.setFlowToSewerFlush(flushPercentageToSewer);

        int grossWaterDemand = lpda + lpdb;
        clubHouseWaterDemandRowData.setGrossLPD(grossWaterDemand);
        
        double totalWaterToSewer = (lpda * domPercentageToSewer/100) + (lpdb * flushPercentageToSewer/100);
        clubHouseWaterDemandRowData.setTotalFlowToSewer((float)totalWaterToSewer);
        
        return clubHouseWaterDemandRowData;
    }

    public static List<CommertialWaterDemandRowData> calculateWaterDemandForShowroom(PdfData pdfData){
        
        List<CommertialWaterDemandRowData> commertialWaterDemandForShowroom = new ArrayList<>();
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        //filter showroom data
        List<OfficeData> showrooms = pdfData.getOfficesList().stream()
                .filter(officeData -> officeData.getType().equals(StandardType.SHOWROOM))
                .collect(Collectors.toList());
        
        int retailFloatingPopulation = 0;
        int flowToSewerDomestic = (int) pdfData.getCapacityDetailsCommercial().getsTPCapacity().getDomesticFlow();
        int flowToSewerFlush = (int) pdfData.getCapacityDetailsCommercial().getsTPCapacity().getFlushingFlow();
        
        int totalShowroomPopulation = 0;
        float totalShowroomLpda = 0 ;
        float totalShowroomLpdb = 0 ;
        float totalShowroomGross = 0 ;
        float totalShowroomFlowToSewer = 0 ;
                
        if (!showrooms.isEmpty()) {
            int index = 0;
            for (; index < showrooms.size(); index++) {
                OfficeData showroom = showrooms.get(index);
                
                CommertialWaterDemandRowData showroomWaterDemandRowData = new CommertialWaterDemandRowData();
                showroomWaterDemandRowData.setDescription(showroom.getFloorNumer());
                showroomWaterDemandRowData.setTypeOfArea("Retails/ Showroom");
                showroomWaterDemandRowData.setArea(showroom.getArea());
                
                double population = showroom.getArea()/showroom.getSqMtrPerPerson();
                //TODO hardcoded value
                double totalPopulation = (int) population / 10;
                showroomWaterDemandRowData.setTotalPopulation((int)Math.ceil(totalPopulation));
                
                int totalWaterCommertial = (int) standardValMap.get(StandardType.WATER_DEMAND_COMMERCIAL).getValue();
                //TODO hardcoded value
                int lpcdDomestic = totalWaterCommertial - 20;
                showroomWaterDemandRowData.setLpcdDom(lpcdDomestic);
                
                int lpda = (int) totalPopulation * lpcdDomestic;
                showroomWaterDemandRowData.setLpda(lpda);
                showroomWaterDemandRowData.setFlowToSewerDom(flowToSewerDomestic);

                //TODO hardcoded value
                int lpcdFlushing = 20;
                showroomWaterDemandRowData.setLpcdFlush(lpcdFlushing);
                
                int lpdb = (int) totalPopulation * lpcdFlushing;
                showroomWaterDemandRowData.setLpdb(lpdb);
                
                showroomWaterDemandRowData.setFlowToSewerFlush(flowToSewerFlush);

                showroomWaterDemandRowData.setGrossLPD(lpda+lpdb);

                int totalFlowToSewer = (int) ((lpda * flowToSewerDomestic / 100) + (lpdb * flowToSewerFlush / 100));
                showroomWaterDemandRowData.setTotalFlowToSewer(totalFlowToSewer);

                commertialWaterDemandForShowroom.add(showroomWaterDemandRowData);
                retailFloatingPopulation += population;
                totalShowroomPopulation += totalPopulation;
                totalShowroomLpda += lpda;
                totalShowroomLpdb += lpdb;
                totalShowroomGross += lpda+lpdb;
                totalShowroomFlowToSewer += totalFlowToSewer;
            }
            
            CommertialWaterDemandRowData floatingRetailWaterDemandRowData = new CommertialWaterDemandRowData();
            floatingRetailWaterDemandRowData.setDescription("Retail Floating Population");
            floatingRetailWaterDemandRowData.setTypeOfArea("Retails/ Showroom");
            
            //TODO hardcode value
            int floatingPopulation = (int) (retailFloatingPopulation * 0.9);
            floatingRetailWaterDemandRowData.setArea(0);
            floatingRetailWaterDemandRowData.setTotalPopulation(floatingPopulation);
            
            int floatingLPCD = 5;
            floatingRetailWaterDemandRowData.setLpcdDom(floatingLPCD);

            int lpdaFloating = (int) floatingPopulation * 5;
            floatingRetailWaterDemandRowData.setLpda(lpdaFloating);
            
            int lpcdFloating = 10;
            floatingRetailWaterDemandRowData.setLpcdFlush(lpcdFloating);
                    
            int lpdbFloating = floatingPopulation * lpcdFloating;
            floatingRetailWaterDemandRowData.setLpdb(lpdbFloating);

            floatingRetailWaterDemandRowData.setGrossLPD(lpdaFloating + lpdbFloating);
            
            int totalFlowToSewerFloating = (int) ((lpdaFloating * flowToSewerDomestic / 100) + (lpdbFloating * flowToSewerFlush / 100));
            floatingRetailWaterDemandRowData.setTotalFlowToSewer(totalFlowToSewerFloating);
            
            commertialWaterDemandForShowroom.add(floatingRetailWaterDemandRowData);
            
            totalShowroomPopulation += floatingPopulation;
            totalShowroomLpda += lpdaFloating;
            totalShowroomLpdb += lpdbFloating;
            totalShowroomGross += lpdaFloating + lpdbFloating;
            totalShowroomFlowToSewer += totalFlowToSewerFloating;
            //add total demand row
            CommertialWaterDemandRowData totalShowroomWaterDemandRowData = new CommertialWaterDemandRowData();
            totalShowroomWaterDemandRowData.setDescription(DESCRIPTION_ROW_TOTAL);
            totalShowroomWaterDemandRowData.setTotalPopulation(totalShowroomPopulation);
            totalShowroomWaterDemandRowData.setLpda(totalShowroomLpda);
            totalShowroomWaterDemandRowData.setLpdb(totalShowroomLpdb);
            totalShowroomWaterDemandRowData.setGrossLPD(totalShowroomGross);
            totalShowroomWaterDemandRowData.setTotalFlowToSewer(totalShowroomFlowToSewer);
            commertialWaterDemandForShowroom.add(totalShowroomWaterDemandRowData);
        }

        return commertialWaterDemandForShowroom;
    }
    
    public static List<CommertialWaterDemandRowData> calculateWaterDemandForOffice(PdfData pdfData){
        List<CommertialWaterDemandRowData> commertialWaterDemand = new ArrayList<>();
        
        HashMap<StandardType, StandardValues> standardValMap = pdfData.getStandardValMap();
        //filter office data
        List<OfficeData> offices = pdfData.getOfficesList().stream()
                .filter(officeData -> officeData.getType().equals(StandardType.OFFICE))
                .collect(Collectors.toList());
        
        int totalRetailPopulation = 0;
        float flowToSewerDomestic = pdfData.getCapacityDetailsCommercial().getsTPCapacity().getDomesticFlow();
        float flowToSewerFlush = pdfData.getCapacityDetailsCommercial().getsTPCapacity().getFlushingFlow();
        
        int totalOfficePopulation = 0;
        int totalOfficeLpda = 0;
        int totalOfficeLpdb = 0;
        int totalOfficeGross = 0;
        int totalOfficeFlowToSewer = 0;

        if (!offices.isEmpty()) {
            int index = 0;
            for (; index < offices.size(); index++) {
                
                CommertialWaterDemandRowData commertialWaterDemandRowData = new CommertialWaterDemandRowData();
                
                OfficeData officeData = offices.get(index);
                
                commertialWaterDemandRowData.setDescription(officeData.getFloorNumer());
                commertialWaterDemandRowData.setTypeOfArea("Offices");
                commertialWaterDemandRowData.setArea(officeData.getArea());
                
                double population = officeData.getArea()/officeData.getSqMtrPerPerson();
                int totalPopulation = (int)Math.ceil(population);
                commertialWaterDemandRowData.setTotalPopulation(totalPopulation);
                
                int totalWaterCommertial = (int) standardValMap.get(StandardType.WATER_DEMAND_COMMERCIAL).getValue();
                
                //TODO hard coded value
                int lpcdDomestic = totalWaterCommertial - 20;
                commertialWaterDemandRowData.setLpcdDom((int)Math.ceil(lpcdDomestic));
                
                int lpda = (int) totalPopulation * lpcdDomestic;
                commertialWaterDemandRowData.setLpda(lpda);
                
                commertialWaterDemandRowData.setFlowToSewerDom(flowToSewerDomestic);
                
                //TODO hard coded value
                int lpcdFlushing = 20;
                commertialWaterDemandRowData.setLpcdFlush(lpcdFlushing);
                
                int lpdb = (int) totalPopulation * lpcdFlushing;
                commertialWaterDemandRowData.setLpdb(lpdb);
                
                commertialWaterDemandRowData.setFlowToSewerFlush(flowToSewerFlush);
                commertialWaterDemandRowData.setGrossLPD(lpda+lpdb);
                        
                int totalFlowToSewer = (int) ((lpda * flowToSewerDomestic / 100) + (lpdb * flowToSewerFlush / 100));
                commertialWaterDemandRowData.setTotalFlowToSewer(totalFlowToSewer);
                
                commertialWaterDemand.add(commertialWaterDemandRowData);
                
                totalRetailPopulation += totalPopulation;
                
                totalOfficePopulation += totalPopulation;
                totalOfficeLpda += lpda;
                totalOfficeLpdb += lpdb;
                totalOfficeGross += (lpda + lpdb);
                totalOfficeFlowToSewer += totalFlowToSewer;
            }

            CommertialWaterDemandRowData visitorWaterDemandRowData = new CommertialWaterDemandRowData();
            visitorWaterDemandRowData.setDescription("Visitors");
            visitorWaterDemandRowData.setTypeOfArea("Offices");
            //TODO hardcoded value
            int visitorArea = 10;
            visitorWaterDemandRowData.setArea(10);

            //TODO : visitorPopulation 
            int visitorPopulation = (totalRetailPopulation/visitorArea);
            visitorWaterDemandRowData.setTotalPopulation(visitorPopulation);
            
            //TODO : hardcoded value
            int domLPCDvisitor = 5;
            visitorWaterDemandRowData.setLpcdDom(domLPCDvisitor);
            
            int lpdaVisitor = (int) visitorPopulation * domLPCDvisitor;
            visitorWaterDemandRowData.setLpda(lpdaVisitor);
            
            visitorWaterDemandRowData.setFlowToSewerDom(flowToSewerDomestic);
            //TODO : hardcoded value
            int flushingLPCD = 10;
            visitorWaterDemandRowData.setLpcdFlush(flushingLPCD);
            
            int lpdbVisitor = visitorPopulation * flushingLPCD;
            visitorWaterDemandRowData.setLpdb(lpdbVisitor);
            
            int grossLPDVisitor = lpdaVisitor + lpdbVisitor;
            visitorWaterDemandRowData.setGrossLPD(grossLPDVisitor);
            
            int totalFlowToSewerVisitor = (int) ((lpdaVisitor * flowToSewerDomestic / 100) + (lpdbVisitor * flowToSewerFlush / 100));
            visitorWaterDemandRowData.setTotalFlowToSewer(totalFlowToSewerVisitor);
            
            commertialWaterDemand.add(visitorWaterDemandRowData);
            
            
            totalOfficePopulation += visitorPopulation;
            totalOfficeLpda += lpdaVisitor;
            totalOfficeLpdb += lpdbVisitor;
            totalOfficeGross += (lpdaVisitor + lpdbVisitor);
            totalOfficeFlowToSewer += totalFlowToSewerVisitor;
            
            CommertialWaterDemandRowData totalOfficeWaterDemandRowData = new CommertialWaterDemandRowData();
            totalOfficeWaterDemandRowData.setDescription(DESCRIPTION_ROW_TOTAL);
            totalOfficeWaterDemandRowData.setLpda(totalOfficeLpda);
            totalOfficeWaterDemandRowData.setLpdb(totalOfficeLpdb);
            totalOfficeWaterDemandRowData.setGrossLPD(totalOfficeGross);
            totalOfficeWaterDemandRowData.setTotalFlowToSewer(totalOfficeFlowToSewer);
            
            commertialWaterDemand.add(totalOfficeWaterDemandRowData);
        }
        return commertialWaterDemand;
    }

    public static List<CommertialWaterDemandRowData> calculateWaterDemandForOfficeMisc(PdfData pdfData) {
        List<CommertialWaterDemandRowData> officeMiscData = new ArrayList<>();
        
        float flowToSewerDomestic = pdfData.getCapacityDetailsCommercial().getsTPCapacity().getDomesticFlow();
        float flowToSewerFlush = pdfData.getCapacityDetailsCommercial().getsTPCapacity().getDomesticFlow();
        
        CommertialWaterDemandRowData commertialMiscDataRow = new CommertialWaterDemandRowData();
        commertialMiscDataRow.setDescription("Maintenance &\nSecurity Staff");
        commertialMiscDataRow.setTypeOfArea("");
        commertialMiscDataRow.setArea(0);
        //TODO hardcoded value
        int miscPopulation = 50;
        commertialMiscDataRow.setTotalPopulation(miscPopulation);
        //TODO hardcoded value
        int lpcdDomestic = 25;
        commertialMiscDataRow.setLpcdDom(lpcdDomestic);
        
        int lpdaMisc = miscPopulation * lpcdDomestic;
        commertialMiscDataRow.setLpda(lpdaMisc);
        commertialMiscDataRow.setFlowToSewerDom(flowToSewerDomestic);
        
        int lpcdFlushing = 20;
        commertialMiscDataRow.setLpcdFlush(lpcdFlushing);
        
        int lpdbMisc = miscPopulation * lpcdFlushing;
        commertialMiscDataRow.setLpdb(lpdbMisc);
        
        commertialMiscDataRow.setFlowToSewerFlush(flowToSewerFlush);
        int lpdGross = lpdaMisc + lpdbMisc;
        commertialMiscDataRow.setGrossLPD(lpdGross);
        
        int totalFlowToSewer = (int) ((lpdaMisc * flowToSewerDomestic / 100) + (lpdbMisc * flowToSewerFlush / 100));
        commertialMiscDataRow.setTotalFlowToSewer(totalFlowToSewer);
        
        officeMiscData.add(commertialMiscDataRow);
        
        //Total data row same as misc
        CommertialWaterDemandRowData commertialMiscTotalDataRow = new CommertialWaterDemandRowData();
        commertialMiscTotalDataRow.setDescription(DESCRIPTION_ROW_TOTAL);
        commertialMiscTotalDataRow.setTypeOfArea("");
        commertialMiscTotalDataRow.setArea(0);
        commertialMiscTotalDataRow.setTotalPopulation(miscPopulation);
        commertialMiscTotalDataRow.setLpda(lpdaMisc);
        commertialMiscTotalDataRow.setLpdb(lpdbMisc);
        commertialMiscTotalDataRow.setGrossLPD(lpdGross);
        commertialMiscTotalDataRow.setTotalFlowToSewer(totalFlowToSewer);
        officeMiscData.add(commertialMiscTotalDataRow);
        
        return officeMiscData;
    }
    
    private static TowerWaterDemandRowData getTotalWaterDemandForTower(List<TowerWaterDemandRowData> perTowerWaterDemandList) {
        TowerWaterDemandRowData totalWaterDemandForTower = new TowerWaterDemandRowData();
        totalWaterDemandForTower.setDescription(DESCRIPTION_ROW_TOTAL);
        int totalQty = 0;
        float totalLpda = 0;        
        float totalLpdb = 0;        
        float grossLpd = 0;        
        float totalFlowToSewer = 0;        
        for(TowerWaterDemandRowData dataRow : perTowerWaterDemandList) {
            totalQty += dataRow.getQuantity();
            totalLpda += dataRow.getLpda();
            totalLpdb += dataRow.getLpdb();
            grossLpd += dataRow.getGrossLPD();
            totalFlowToSewer += dataRow.getTotalFlowToSewer();
        }
        totalWaterDemandForTower.setQuantity(totalQty);
        totalWaterDemandForTower.setLpda(totalLpda);
        totalWaterDemandForTower.setLpdb(totalLpdb);
        totalWaterDemandForTower.setGrossLPD(grossLpd);
        totalWaterDemandForTower.setTotalFlowToSewer(totalFlowToSewer);
        
        return totalWaterDemandForTower;
    }

    private static List<TowerWaterDemandRowData> getWaterDemandForTower(TowerData towerData, HashMap<StandardType, StandardValues> standardValMap, float domesticToSewerPercentage, float flushingToSewerPercentage) {
        List<TowerWaterDemandRowData> perTowerWaterDemandList = new ArrayList<>();
        TreeMap<StandardType, Integer> flatsData = towerData.getFlatsData();
        for (StandardType type : flatsData.keySet()) {
            Integer numOfFlats = flatsData.get(type);
            if (numOfFlats > 0) {
                TowerWaterDemandRowData towerWaterDemandRowData = new TowerWaterDemandRowData();
                towerWaterDemandRowData.setDescription(type.getValue());
                //Total population of type
                StandardValues standardVal = standardValMap.get(type);
                towerWaterDemandRowData.setQuantity(numOfFlats);

                String standardValue = DataFormater.getStandardValue(standardVal);
                int pplForType = Integer.parseInt(standardValue);
                int totalPopulation = numOfFlats * pplForType;
                towerWaterDemandRowData.setTotalPopulation(totalPopulation);

                //domestic consumption per type
                int domesticConsumption = (int) standardValMap.get(StandardType.WATER_DEMAND).getValue() * 2 / 3;
                towerWaterDemandRowData.setLpcdDom(domesticConsumption);

                //total domestic water consumption
                int totalDomesticWaterConsumption = totalPopulation * domesticConsumption;
                towerWaterDemandRowData.setLpda(totalDomesticWaterConsumption);

                //flow to sewer for domestin in %
                towerWaterDemandRowData.setFlowToSewerDom(domesticToSewerPercentage);

                //flushing water consumption per person
                int flushWaterConsumptionPerPerson = (int) standardValMap.get(StandardType.WATER_DEMAND).getValue() * 1 / 3;
                towerWaterDemandRowData.setLpcdFlush(flushWaterConsumptionPerPerson);

                //Total flushing water consumption
                int totalFlushWaterConsumption = flushWaterConsumptionPerPerson * totalPopulation;
                towerWaterDemandRowData.setLpdb(totalFlushWaterConsumption);

                //flushing water flow to sewer in %
                towerWaterDemandRowData.setFlowToSewerFlush(flushingToSewerPercentage);

                //gross water consumption
                float grossWaterConsumption = totalDomesticWaterConsumption + totalFlushWaterConsumption;
                towerWaterDemandRowData.setGrossLPD(grossWaterConsumption);

                //flow to sewer
                float flowToSewer = totalDomesticWaterConsumption * (domesticToSewerPercentage / 100) + totalFlushWaterConsumption * (flushingToSewerPercentage / 100);
                towerWaterDemandRowData.setTotalFlowToSewer(flowToSewer);

                perTowerWaterDemandList.add(towerWaterDemandRowData);
            }
        }
        return perTowerWaterDemandList;
    }
    
    public static CommertialWaterDemandRowData getTotalCommertialData(PdfData pdfData){
        float totalLdpa=0;
        List<CommertialWaterDemandRowData> showroomDetails = calculateWaterDemandForShowroom(pdfData);
        List<CommertialWaterDemandRowData> officeDetails = calculateWaterDemandForOffice(pdfData);
        List<CommertialWaterDemandRowData> miscDetails = calculateWaterDemandForOfficeMisc(pdfData);
        
        CommertialWaterDemandRowData showroomTotalRowData = showroomDetails.stream()
                .filter(dataRow -> dataRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                .findFirst()
                .get();
        
        CommertialWaterDemandRowData officeTotalRowData = officeDetails.stream()
                .filter(dataRow -> dataRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                .findFirst()
                .get();
        
        CommertialWaterDemandRowData miscTotalRowData = miscDetails.stream()
                .filter(dataRow -> dataRow.getDescription().equals(DESCRIPTION_ROW_TOTAL))
                .findFirst()
                .get();
        
        CommertialWaterDemandRowData totalRowData = new CommertialWaterDemandRowData();
        totalRowData.setLpda(showroomTotalRowData.getLpda() + officeTotalRowData.getLpda() + miscTotalRowData.getLpda());
        totalRowData.setLpdb(showroomTotalRowData.getLpdb() + officeTotalRowData.getLpdb() + miscTotalRowData.getLpdb());
        totalRowData.setTotalFlowToSewer(showroomTotalRowData.getTotalFlowToSewer() + officeTotalRowData.getTotalFlowToSewer() + miscTotalRowData.getTotalFlowToSewer());
        
        return totalRowData;
    }
}
