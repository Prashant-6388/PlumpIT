/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

import com.pc.plumbit.model.input.OfficeData;
import com.pc.plumbit.model.input.TowerData;
import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.input.FireFightingDetails;
import com.pc.plumbit.model.input.PlotArea;
import com.pc.plumbit.model.input.CapacityDetails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Prashant
 */
public class PdfData {
    private HashMap<StandardType, StandardValues> standardValMap;
    private List<TowerData> towersList;
    private List<OfficeData> officesList;
    private TreeMap<StandardType, Integer> outsideAreaMap;
    private List<List<String>> groupedTowerNamesList;
    private String projectName;
    private String pdfLocation;
    private CapacityDetails capacityDetailsResidential;
    private CapacityDetails capacityDetailsCommertial;
    private PlotArea plotArea;
    private FireFightingDetails fireFightingDetails;
    
    public PdfData(PdfDataBuilder builder) {
        this.standardValMap = builder.standardValMap;
        this.towersList = builder.towersList;
        this.officesList = builder.officesList;
        this.outsideAreaMap = builder.outsideAreaMap;
        this.groupedTowerNamesList = builder.groupedTowerNamesList;
        this.capacityDetailsResidential = builder.capacityDetailsResidential;
        this.capacityDetailsCommertial = builder.capacityDetailsCommertial;
        this.plotArea = builder.plotArea;
        this.fireFightingDetails = builder.fireFightingDetails;
        this.projectName = builder.projectName;
        this.pdfLocation = builder.pdfLocation;
    }

    public HashMap<StandardType, StandardValues> getStandardValMap() {
        return standardValMap;
    }

    public List<TowerData> getTowersList() {
        return towersList;
    }

    public List<OfficeData> getOfficesList() {
        return officesList;
    }

    public TreeMap<StandardType, Integer> getOutsideAreaMap() {
        return outsideAreaMap;
    }

    public List<List<String>> getGroupedTowerNamesList() {
        return groupedTowerNamesList;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPdfLocation() {
        return pdfLocation;
    }

    public CapacityDetails getCapacityDetailsResidential() {
        return capacityDetailsResidential;
    }

    public void setCapacityDetails(CapacityDetails capacityDetailsResidential) {
        this.capacityDetailsResidential = capacityDetailsResidential;
    }

    public CapacityDetails getCapacityDetailsCommercial() {
        return capacityDetailsCommertial;
    }

    public void setCapacityDetailsCommertial(CapacityDetails capacityDetailsCommertial) {
        this.capacityDetailsCommertial = capacityDetailsCommertial;
    }

    public PlotArea getPlotArea() {
        return plotArea;
    }

    public void setPlotArea(PlotArea plotArea) {
        this.plotArea = plotArea;
    }

    public FireFightingDetails getFireFightingDetails() {
        return fireFightingDetails;
    }

    public void setFireFightingDetails(FireFightingDetails fireFightingDetails) {
        this.fireFightingDetails = fireFightingDetails;
    }
    
    public static class PdfDataBuilder {
        private HashMap<StandardType, StandardValues> standardValMap = new HashMap<>();
        private List<TowerData> towersList;
        private List<OfficeData> officesList = new ArrayList<>();
        private TreeMap<StandardType, Integer> outsideAreaMap = new TreeMap<>();
        private List<List<String>> groupedTowerNamesList = new ArrayList<>();
        
        private CapacityDetails capacityDetailsResidential;
        private CapacityDetails capacityDetailsCommertial;
        private PlotArea plotArea;
        private FireFightingDetails fireFightingDetails;
        
        private String projectName;
        private String pdfLocation;

        public PdfDataBuilder(HashMap<StandardType, StandardValues> standardValMap, List<TowerData> towersList) {
            this.standardValMap = standardValMap;
            this.towersList = towersList;
        }
        
        public PdfDataBuilder setOfficesList(List<OfficeData> officesList){
            this.officesList = officesList;
            return this;
        }
        
        public PdfDataBuilder setOutsideAreaMap(TreeMap<StandardType, Integer> outsideAreaMap){
            this.outsideAreaMap = outsideAreaMap;
            return this;
        }
        
        public PdfDataBuilder setGroupedTowerNamesList(List<List<String>> groupedTowerNamesList) {
            this.groupedTowerNamesList = groupedTowerNamesList;
            return this;
        }

        public PdfDataBuilder setProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public PdfDataBuilder setPdfLocation(String pdfLocation) {
            this.pdfLocation = pdfLocation;
            return this;
        }
        
        public PdfDataBuilder setCapacityDetailsResidential(CapacityDetails capacityDetailsResidential) {
            this.capacityDetailsResidential = capacityDetailsResidential;
            return this;
        }
        
        public PdfDataBuilder setCapacityDetailsCommertial(CapacityDetails capacityDetailsCommertial) {
            this.capacityDetailsCommertial = capacityDetailsCommertial;
            return this;
        }
        
        public PdfDataBuilder setPlotArea(PlotArea plotArea) {
            this.plotArea = plotArea;
            return this;
        }
        
        public PdfDataBuilder setFireFightingDetails(FireFightingDetails fireFightingDetails) {
            this.fireFightingDetails = fireFightingDetails;
            return this;
        }                
        
        public PdfData build(){
            return new PdfData(this);
        }
    }
}
