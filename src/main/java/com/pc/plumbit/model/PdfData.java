/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

import com.pc.plumbit.enums.StandardType;
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
    private List<TreeMap<StandardType, Integer>> officesList;
    private TreeMap<StandardType, Integer> outsideAreaMap;
    private List<List<String>> groupedTowerNamesList;
    private String projectName;
    private String pdfLocation;
    
    public PdfData(PdfDataBuilder builder) {
        this.standardValMap = builder.standardValMap;
        this.towersList = builder.towersList;
        this.officesList = builder.officesList;
        this.outsideAreaMap = builder.outsideAreaMap;
        this.groupedTowerNamesList = builder.groupedTowerNamesList;
        this.projectName = builder.projectName;
        this.pdfLocation = builder.pdfLocation;
    }

    public HashMap<StandardType, StandardValues> getStandardValMap() {
        return standardValMap;
    }

    public List<TowerData> getTowersList() {
        return towersList;
    }

    public List<TreeMap<StandardType, Integer>> getOfficesList() {
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
    
    
	
    public static class PdfDataBuilder {
        private HashMap<StandardType, StandardValues> standardValMap = new HashMap<>();
        private List<TowerData> towersList;
        private List<TreeMap<StandardType, Integer>> officesList = new ArrayList<>();
        private TreeMap<StandardType, Integer> outsideAreaMap = new TreeMap<>();;
        private List<List<String>> groupedTowerNamesList = new ArrayList<>();;
        private String projectName;
        private String pdfLocation;

        public PdfDataBuilder(HashMap<StandardType, StandardValues> standardValMap, List<TowerData> towersList) {
            this.standardValMap = standardValMap;
            this.towersList = towersList;
        }
        
        public PdfDataBuilder setOfficesList(List<TreeMap<StandardType, Integer>> officesList){
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
        
        public PdfData build(){
            return new PdfData(this);
        }
    }
}
