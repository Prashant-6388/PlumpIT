/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.domestic;

import com.pc.plumbit.generator.WaterDemandCalculator;
import com.pc.plumbit.model.PdfData;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Prashant
 */
public class DomesticWaterDemandData {
    HashMap<String, List<TowerWaterDemandRowData>> towerRowData = new HashMap<>();
    TowerWaterDemandRowData landscapeDataRow = new TowerWaterDemandRowData();
    TowerWaterDemandRowData swimmingPoolDataRow = new TowerWaterDemandRowData();
    TowerWaterDemandRowData clubHouseDataRow = new TowerWaterDemandRowData();
    TowerWaterDemandRowData miscDataRow =  new TowerWaterDemandRowData();

    public DomesticWaterDemandData(PdfData pdfData) {
        towerRowData = WaterDemandCalculator.calculateWaterDemandForTowers(pdfData);
        landscapeDataRow = WaterDemandCalculator.calculateWaterDemandForLandscape(pdfData);
        swimmingPoolDataRow = WaterDemandCalculator.calculateWaterDemandForSwimmingPool(pdfData);
        clubHouseDataRow = WaterDemandCalculator.calculateWaterDemandForClubHouse(pdfData);
        miscDataRow =  WaterDemandCalculator.calculateWaterDemandForMiscTotalForTower(pdfData);
    }

    public HashMap<String, List<TowerWaterDemandRowData>> getTowerRowData() {
        return towerRowData;
    }

    public TowerWaterDemandRowData getLandscapeDataRow() {
        return landscapeDataRow;
    }

    public TowerWaterDemandRowData getSwimmingPoolDataRow() {
        return swimmingPoolDataRow;
    }

    public TowerWaterDemandRowData getClubHouseDataRow() {
        return clubHouseDataRow;
    }

    public TowerWaterDemandRowData getMiscDataRow() {
        return miscDataRow;
    }
}
