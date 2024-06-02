/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.initializer;

import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.WaterConsumption;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Prashant
 */
public class DataInitializer {
    private static final String BASE_LOCATION = "./config/";
    private static XStream xstream;
    
    private DataInitializer() {
        initXstream();
    }
    
    private static void initXstream() {
        xstream = new XStream(new DomDriver());
        xstream.allowTypesByWildcard(new String[] {
            "com.pc.plumbit.model.*",
            "com.pc.plumbit.enums.*"                
        });
    }

    public static XStream getXstream() {
        if(xstream == null){
            initXstream();
        }
        return xstream;
    }
    
    public static Map<String,List> setupConfigData(JTable populationCriteriaTbl, JTable domWaterRequirement) {
       
        Map<StandardType,StandardValues> populationCriteriaList = getPopulationCriteria();
        List domWaterConsumptionList = getDomWaterConsumptionData();
        List flushWaterConsumptionList = getFlushWaterConsumptionData();
        
        setPopulationCriteria(populationCriteriaTbl, populationCriteriaList);
        setWaterConsumptionData(domWaterRequirement, domWaterConsumptionList, flushWaterConsumptionList);
        return null;
    }

    public static Map<StandardType,StandardValues> getPopulationCriteria() {
        if(xstream == null){
            initXstream();
        }
        File file = new File(BASE_LOCATION + "PopulationCriteria.xml");
        return (Map<StandardType,StandardValues>) xstream.fromXML(file);
    }
    
    public static HashMap<StandardType,StandardValues> getStandardValues() {
        if(xstream == null){
            initXstream();
        }
        File file = new File(BASE_LOCATION + "StandardValues.xml");
        return (HashMap<StandardType, StandardValues>) xstream.fromXML(file);
    }
    
    private static List setPopulationCriteria(JTable tbl, Map<StandardType,StandardValues> map) {
        String header1 = "<html><style='width:100%'><b>Type</b></html>";
        String header2 = "<html><b># of People</b></html>";
        String header3 = "<html><b>Description</b></html>";
        
        setTableStyling(tbl);
        /*DefaultTableModel dtm = new DefaultTableModel(new Object[]{header1, header2, header3},0);
        
//        dtm.addColumn("Type");
//        dtm.addColumn("# of People");
//        dtm.addColumn("Description");
        map.forEach(item -> {
            dtm.addRow(new Object[]{
                item.getType(),
                item.getValue()> 0 ? item.getValue() : "-",
                item.getDescription()}
            );
        });
        tbl.setModel(dtm);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tbl.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tbl.setShowGrid(false);
        */
        return null;
    }
    
    public static List<WaterConsumption> getDomWaterConsumptionData() {
        File file = new File(BASE_LOCATION + "DomesticWaterConsumptionPerType.xml");
        return (List<WaterConsumption>) xstream.fromXML(file);
    }
    
    public static List getFlushWaterConsumptionData() {
        File file = new File(BASE_LOCATION + "FlushWaterConsumptionPerType.xml");
        return (List<WaterConsumption>) xstream.fromXML(file);
    }
    
    private static void setWaterConsumptionData(JTable tbl, List<WaterConsumption> domWaterConsumptionList, List<WaterConsumption> flushWaterConsumptionList) {
        String header1 = "<html><b>Type</b></html>";
        String header2 = "<html><b>Water requirement</b></html>";
        String header3 = "<html><style='width:100%'><b>Unit</b></html>";
        
        setTableStyling(tbl);
        DefaultTableModel dtm = new DefaultTableModel(new Object[]{header1, header2, header3},0);
        
        if(domWaterConsumptionList.size() != flushWaterConsumptionList.size()){
            dtm.addRow(new Object[]{"<html><style='color:red'>Invalid confid data for water consumption</html>"});
        } else {
            for(int i=0;i<domWaterConsumptionList.size();i++) {
                WaterConsumption domWater = domWaterConsumptionList.get(i);
                WaterConsumption flushWater = flushWaterConsumptionList.get(i);
                if(domWater.getType().equals(flushWater.getType())) {
                    int totalQty = domWater.getQty() + flushWater.getQty();
                    dtm.addRow(new Object[]{domWater.getType(), totalQty, domWater.getMessage()});
                }
            }
            tbl.setModel(dtm);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            tbl.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            tbl.setShowGrid(false);
        }
    }
    
    public static void setTableStyling(JTable tbl) {
        tbl.getTableHeader().setOpaque(false);
        tbl.getTableHeader().setForeground(Color.BLUE);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.setDefaultEditor(Object.class, null);
    }
}
