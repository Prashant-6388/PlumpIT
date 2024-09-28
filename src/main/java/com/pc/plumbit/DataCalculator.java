/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pc.plumbit;

import com.pc.plumbit.enums.StandardType;
import com.pc.exceptions.InvalidInputException;
import com.pc.initializer.DataInitializer;
import com.pc.plumbit.generator.PDFGenerator;
import com.pc.plumbit.model.input.OfficeData;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.input.CapacityDetails;
import com.pc.plumbit.model.input.FireFightingDetails;
import com.pc.plumbit.model.input.PlotArea;
import com.pc.plumbit.model.input.TowerData;
import com.pc.plumbit.model.input.capacity.OHTCapacity;
import com.pc.plumbit.model.input.capacity.STPCapacity;
import com.pc.plumbit.model.input.capacity.UGTCapacity;
import com.pc.utils.DataFormater;
import com.pc.utils.DataUtils;
import com.pc.utils.StyleFormatter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prashant
 */
public class DataCalculator extends javax.swing.JFrame implements SaveListener {

    private static final Logger log = LoggerFactory.getLogger(DataCalculator.class);
    private List<TowerData> towersList = new ArrayList<>();
    private List<OfficeData> officesList = new ArrayList<>();
    private TreeMap<StandardType,Integer> outsideAreaMap = new TreeMap<>();
    private HashMap<StandardType, StandardValues> standardValMap;
    private DefaultTableModel dtm;
    private DefaultTableModel dtmOffice;
    private DefaultTableModel dtmOfficeOverview = new DefaultTableModel(0, 0);
    private DefaultTableModel dtmTowerDataOverview = new DefaultTableModel(0, 0);
    private DefaultTableModel dtmGroupedTowerTable = new DefaultTableModel(0, 0);    
    public static final String TYPE_RESIDENTIAL="Residential";
    public static final String TYPE_COMMERTIAL="Commertial";
    private List<String> towerNameList = new ArrayList<>();
    private List<List<String>> groupedTowerNamesList = new ArrayList<>();
    public static Font baseFont = new Font("Segoe UI", Font.PLAIN, 12);
    private SaveListener saveListener;
    private CapacityDetails capacityDetailsResidential;
    private CapacityDetails capacityDetailsCommertial;
    private FireFightingDetails fireFightingDetails;
    private PlotArea plotArea;

    /**
     * Creates new form DataCalculator
     */
    public DataCalculator() {
        initApp();
    }
    
    private void initApp() {
        initComponents();
        initConfigs();
        initTowerTable();
        initOfficeTable();
        officeShowroomPanel.setVisible(false);
        residentialPanel.setVisible(true);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        
        officeDataOverviewTable.setModel(dtmOfficeOverview);
        towerDataOveriewTable.setModel(dtmTowerDataOverview);
        DataInitializer.setTableStyling(towerDataOveriewTable);
        DataInitializer.setTableStyling(officeDataOverviewTable);
        
        initTowerDataOverview();
        initOfficeDataOverview();
        initPanelSelector();
        initButtons();
        
        towerGroupPanel.setLayout(new GridLayout(20,1));
        DataInitializer.setTableStyling(towerGroupTable);
        
        StyleFormatter.setTableColumnAlignment(towerDataTable, JLabel.CENTER, true);
        StyleFormatter.setTableColumnAlignment(officeDataTable, SwingConstants.LEFT, true);
        StyleFormatter.setTableColumnAlignment(towerGroupTable, JLabel.CENTER, true);
    }
    
    public DataCalculator(PdfData projectData) {
        initApp();
        modifyStdVals.setEnabled(false);
        setStandardValues(projectData.getStandardValMap());
        
        this.towersList = projectData.getTowersList();
        for(TowerData towerData : towersList) {
            addRowToTowerTable(towerData.getName(), towerData.getFlatsData() ,false);
            addTowerDataOverviewRow(towerData.getName(), towerData.getFlatsData());
        }

        this.officesList = projectData.getOfficesList();
        for(OfficeData officeData : officesList){
            addOfficeDataRow(officeData);
        }
        
        this.outsideAreaMap = projectData.getOutsideAreaMap();

        landscapeAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.LANDSCAPE)));
        clubHouseAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.CLUB_HOUSE)));
        swimmingAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.SWIMMING_POOL)));

        List<List<String>> groupedTowerNames = projectData.getGroupedTowerNamesList();
        for(List<String> groupedTowers : groupedTowerNames) {
            addGroupedTowerRow(groupedTowers);
        }
        
        this.capacityDetailsResidential = projectData.getCapacityDetailsResidential();
        this.capacityDetailsCommertial = projectData.getCapacityDetailsCommertial();
        setOtherInputs(projectData);
                
        createOverview();
        projectNameInput.setText(projectData.getProjectName());
        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setEnabledAt(2, true);
    }
    
    private void initTowerTable() {
        //String[] tableHeaders= {"2 BHK, 3 BHK, 3.5 BHK, 4 BHK"};
        dtm = new DefaultTableModel(0, 0);
        dtm.addColumn("<html><b>Name</b></html>");
        dtm.addColumn("<html><b>1 BHK</b></html>");
        dtm.addColumn("<html><b>2 BHK</b></html>");
        dtm.addColumn("<html><b>2.5 BHK</b></html>");
        dtm.addColumn("<html><b>3 BHK</b></html>");
        dtm.addColumn("<html><b>3.5 BHK</b></html>");
        dtm.addColumn("<html><b>4 BHK</b></html>");
        dtm.addColumn("<html><b>4.5 BHK</b></html>");
        dtm.addColumn("<html><b>Studio</b></html>");
        dtm.addColumn("<html><b>  </b></html>");
        towerDataTable.setModel(dtm);
        towerDataTable.setDefaultEditor(Object.class, null);
        DataInitializer.setTableStyling(towerDataTable);
    }
    
    private void initOfficeTable() {
        //String[] tableHeaders= {"2 BHK, 3 BHK, 3.5 BHK, 4 BHK"};
        dtmOffice = new DefaultTableModel(0, 0);
        dtmOffice.addColumn("<html><div style='height:25px'><b>Sr. No</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>Floor Nr</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>Type of Area</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>Area</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>No of floors</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>Sq. Mtr. Per Person </b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b>No. of Shifts</b></div></html>");
        dtmOffice.addColumn("<html><div style='height:25px'><b></b></html>");
        officeDataTable.setModel(dtmOffice);
        officeDataTable.setDefaultEditor(Object.class, null);
        DataInitializer.setTableStyling(towerDataTable);
        
    }
    
    private void initConfigs() {
        updateStandardFieldEditiability(false);
        readAndSetStandardValues();
    }
    
    private void readAndSetStandardValues() {
        try {
            standardValMap = DataInitializer.getStandardValues();
            setStandardValues(standardValMap);
        } catch (StreamException ex) {
            log.error("unable to read standard value config : ", ex);
            int continueWithoutStandardVals = JOptionPane.showConfirmDialog(this, "Unable to read Standard values \n Do you want to continue?", 
                    "Error", JOptionPane.YES_NO_OPTION);
            if (continueWithoutStandardVals == 0) {
                updateStandardFieldEditiability(true);
            } else {
                JOptionPane.showMessageDialog(this, "Application will close now, please contact support team");
                this.dispose();
                System.exit(0);
            }
        }
    }
    
    private void setStandardValues(HashMap<StandardType, StandardValues> standardValues){
        peoplePer1BHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.ONE_BHK)));
        peoplePer2BHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.TWO_BHK)));
        peoplePer2NHalfBHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.TWO_N_HALF_BHK)));
        peoplePer3BHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.THREE_BHK)));
        peoplePer3AndHalfBHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.THREE_N_HALF_BHK)));
        peoplePer4BHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.FOUR_BHK)));
        peoplePer4AndHalfBHKInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.FOUR_AND_HALF_BHK)));
        peoplePerStudioInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.STUDIO)));
        peopleServantInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SERVANT)));
        peopleDriverInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.DRIVER)));
        peoplePODsInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.PODS)));
        peoplePerLandscapeInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.LANDSCAPE)));
        peoplePerSwimmingPoolInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SWIMMING_POOL)));
        peoplePerClubHouseInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.CLUB_HOUSE)));

        landscapeAreaWater.setSelectedItem(DataFormater.getStandardValue(standardValues.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE)));
        swimmingPoolCapacity.setSelectedItem(DataFormater.getStandardValue(standardValues.get(StandardType.SWIMMING_POOL_AREA_PERCENT)));

        sqMtrPerPersonOfficeInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SQ_MTR_PER_PERSON_OFFICE)));
        sqMtrPerPersonAtGroundInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND)));
        sqMtrPerPersonAboveGroundInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND)));
        
        stdwaterDemand.setText(DataFormater.getStandardValue(standardValues.get(StandardType.WATER_DEMAND)));
        stdSolidWasteKgPPD.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_KG_PERSON_DAY)));
        stdGasBank.setText(DataFormater.getStandardValue(standardValues.get(StandardType.GAS_BANK)));
        stdSolar.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLAR)));
        dryGarbageInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_DRY_GARBAGE)));
        wetGarbageInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_WET_GARBAGE)));
        
        stdwaterDemandCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.WATER_DEMAND_COMMERTIAL)));
        stdSolidWasteKgPPCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_KG_PERSON_DAY_COMMERTIAL)));
        stdGasBankCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.GAS_BANK_COMMERTIAL)));
        stdSolarCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLAR_COMMERTIAL)));
        dryGarbageInputCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_DRY_GARBAGE_COMMERTIAL)));
        wetGarbageInputCommertial.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_WET_GARBAGE_COMMERTIAL)));
    }
    
    private void updateStandardFieldEditiability(boolean allowEdit){
        peoplePer1BHKInput.setEditable(allowEdit);
        peoplePer2BHKInput.setEditable(allowEdit);
        peoplePer2NHalfBHKInput.setEditable(allowEdit);
        peoplePer3BHKInput.setEditable(allowEdit);
        peoplePer3AndHalfBHKInput.setEditable(allowEdit);
        peoplePer4BHKInput.setEditable(allowEdit);
        peoplePer4AndHalfBHKInput.setEditable(allowEdit);
        peoplePerStudioInput.setEditable(allowEdit);
        peopleServantInput.setEditable(allowEdit);
        peopleDriverInput.setEditable(allowEdit);
        peoplePODsInput.setEditable(allowEdit);
        peoplePerLandscapeInput.setEditable(allowEdit);
        peoplePerSwimmingPoolInput.setEditable(allowEdit);
        peoplePerClubHouseInput.setEditable(allowEdit);
        
        sqMtrPerPersonOfficeInput.setEditable(allowEdit);
        sqMtrPerPersonAtGroundInput.setEditable(allowEdit);
        sqMtrPerPersonAboveGroundInput.setEditable(allowEdit);
        
        stdwaterDemand.setEditable(allowEdit);
        stdSolidWasteKgPPD.setEditable(allowEdit);
        stdGasBank.setEditable(allowEdit);
        stdSolar.setEditable(allowEdit);
        dryGarbageInput.setEditable(allowEdit);
        wetGarbageInput.setEditable(allowEdit);
        
        stdwaterDemandCommertial.setEditable(allowEdit);
        stdSolidWasteKgPPCommertial.setEditable(allowEdit);
        stdGasBankCommertial.setEditable(allowEdit);
        stdSolarCommertial.setEditable(allowEdit);
        dryGarbageInputCommertial.setEditable(allowEdit);
        wetGarbageInputCommertial.setEditable(allowEdit);
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        inputCriteriaForm = new javax.swing.JPanel();
        peoplePerTypeInputPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        peoplePer1BHKInput = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        peoplePer2BHKInput = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        peoplePer2NHalfBHKInput = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        peoplePer3BHKInput = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        peoplePer3AndHalfBHKInput = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        peoplePer4BHKInput = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        peoplePer4AndHalfBHKInput = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        peoplePODsInput = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        peoplePerStudioInput = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        peopleServantInput = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        peopleDriverInput = new javax.swing.JTextField();
        peoplePerLandscapeInput = new javax.swing.JTextField();
        peoplePerSwimmingPoolInput = new javax.swing.JTextField();
        peoplePerClubHouseInput = new javax.swing.JTextField();
        jLabel140 = new javax.swing.JLabel();
        consumptionParamPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        stdwaterDemand = new javax.swing.JTextField();
        stdSolidWasteKgPPD = new javax.swing.JTextField();
        stdSolar = new javax.swing.JTextField();
        stdGasBank = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        dryGarbageInput = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        wetGarbageInput = new javax.swing.JTextField();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jLabel146 = new javax.swing.JLabel();
        jLabel148 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        inputCriteriaTabNextBtn = new javax.swing.JButton();
        modifyStdVals = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        sqMtrPerPersonOfficeInput = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        sqMtrPerPersonAtGroundInput = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        sqMtrPerPersonAboveGroundInput = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        jLabel143 = new javax.swing.JLabel();
        consumptionParamPanel1 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        stdwaterDemandCommertial = new javax.swing.JTextField();
        stdSolidWasteKgPPCommertial = new javax.swing.JTextField();
        stdSolarCommertial = new javax.swing.JTextField();
        stdGasBankCommertial = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        dryGarbageInputCommertial = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        wetGarbageInputCommertial = new javax.swing.JTextField();
        jLabel147 = new javax.swing.JLabel();
        jLabel149 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel152 = new javax.swing.JLabel();
        jLabel154 = new javax.swing.JLabel();
        inputDataPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        peopertTypeSelector = new javax.swing.JComboBox<>();
        outsideAreaPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        swimmingAreaInput = new javax.swing.JTextField();
        landscapeAreaInput = new javax.swing.JTextField();
        outsideAreaBtn = new javax.swing.JButton();
        clubHouseAreaInput = new javax.swing.JTextField();
        swimmingPoolCapacity = new javax.swing.JComboBox<>();
        landscapeAreaWater = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        towerDataTable = new javax.swing.JTable();
        typePanel = new javax.swing.JPanel();
        residentialPanel = new javax.swing.JPanel();
        addTowerBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        towerNameInput = new javax.swing.JTextField();
        nrFlats1BHKInput = new javax.swing.JTextField();
        nrFlats2BHKInput = new javax.swing.JTextField();
        nrFlats2NHalfBHKInput = new javax.swing.JTextField();
        nrFlats3BHKInput = new javax.swing.JTextField();
        nrFlats3AndHalfBHKInput = new javax.swing.JTextField();
        nrFlats4BHKInput = new javax.swing.JTextField();
        nrFlats4AndHalfBHKInput = new javax.swing.JTextField();
        nrStudioInput = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        officeShowroomPanel = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        officeFloorNrInput = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        officeAreaInput = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        nrOfShiftsInput = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        officeType = new javax.swing.JComboBox<>();
        nrFloorInput = new javax.swing.JComboBox<>();
        inputDataNextBtn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        officeDataTable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        towerGroupTable = new javax.swing.JTable();
        groupTowers = new javax.swing.JButton();
        towerGroupScrollPane = new javax.swing.JScrollPane();
        towerGroupPanel = new javax.swing.JPanel();
        clearGroupedTowerBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        terraceArea = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        greenArea = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        pavedArea = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        totalPlotArea = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        extraAreaCatered = new javax.swing.JTextField();
        jLabel132 = new javax.swing.JLabel();
        jLabel136 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        jLabel139 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        heightOfTallestBld = new javax.swing.JTextField();
        residualHeadPlumbing = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        basementArea = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        frictionLossInput = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        residualHeadFire = new javax.swing.JTextField();
        jLableStdFrictionLoss = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jLabel135 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        ugtDomesticWaterTankPercent = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        ugtRawWaterTankPercent = new javax.swing.JTextField();
        jLabel104 = new javax.swing.JLabel();
        ugtFireFightingTank = new javax.swing.JTextField();
        jLabel103 = new javax.swing.JLabel();
        ugtFlushingTank = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        ohtFlushingTank = new javax.swing.JTextField();
        ohtDomesticTankPercent = new javax.swing.JTextField();
        ohtFireFightingTank = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        domesticSewerPercent = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        flushingSewerPercent = new javax.swing.JTextField();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        ugtDomesticWaterTankPercentCommertial = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        ugtRawWaterTankPercentCommertial = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        ugtFireFightingTankCommertial = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        ugtFlushingTankCommertial = new javax.swing.JTextField();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        ohtFlushingTankCommertial = new javax.swing.JTextField();
        ohtDomesticTankPercentCommertial = new javax.swing.JTextField();
        ohtFireFightingTankCommertial = new javax.swing.JTextField();
        jLabel125 = new javax.swing.JLabel();
        jLabel126 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        domesticSewerPercentCommertial = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        flushingSewerPercentCommertial = new javax.swing.JTextField();
        jLabel128 = new javax.swing.JLabel();
        jLabel129 = new javax.swing.JLabel();
        overviewPanel = new javax.swing.JPanel();
        populationCriteriaPanel = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        waterDemandOverviewPanel = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        outdoorOverviewPanel = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        officeDataOverviewTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        towerDataOveriewTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        projectNameInput = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        pdfLocationInput = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        waterDemandPDFBtn = new javax.swing.JButton();
        saveProjectBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(203, 203, 255));
        setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        setResizable(false);

        tabbedPane.setName(""); // NOI18N

        peoplePerTypeInputPanel.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerTypeInputPanel.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerTypeInputPanel.setPreferredSize(new java.awt.Dimension(20, 40));

        jLabel15.setBackground(new java.awt.Color(204, 255, 255));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 255));
        jLabel15.setText("Residential Population Criteria");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("1 BHK");
        jLabel13.setAlignmentX(0.5F);
        jLabel13.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel13.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel13.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer1BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer1BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer1BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("2 BHK");
        jLabel11.setAlignmentX(0.5F);
        jLabel11.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel11.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel11.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer2BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer2BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer2BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("2.5 BHK");
        jLabel12.setAlignmentX(0.5F);
        jLabel12.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel12.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel12.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer2NHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer2NHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer2NHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("3 BHK");
        jLabel14.setAlignmentX(0.5F);
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel14.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel14.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel14.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer3BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer3BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer3BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel10.setText("3.5 BHK");
        jLabel10.setAlignmentX(0.5F);
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel10.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel10.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel10.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer3AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer3AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer3AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel16.setText("4 BHK");
        jLabel16.setAlignmentX(0.5F);
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel16.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel16.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel16.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer4BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer4BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer4BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel17.setText("4.5 BHK");
        jLabel17.setAlignmentX(0.5F);
        jLabel17.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel17.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel17.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePer4AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePer4AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePer4AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Studio");
        jLabel18.setAlignmentX(0.5F);
        jLabel18.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel18.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel18.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePODsInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePODsInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePODsInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel19.setText("PODS");
        jLabel19.setAlignmentX(0.5F);
        jLabel19.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel19.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel19.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePerStudioInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerStudioInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerStudioInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel20.setText("SERVANT");
        jLabel20.setAlignmentX(0.5F);
        jLabel20.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel20.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel20.setPreferredSize(new java.awt.Dimension(20, 80));

        peopleServantInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peopleServantInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peopleServantInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel21.setText("DRIVER");
        jLabel21.setAlignmentX(0.5F);
        jLabel21.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel21.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel21.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel22.setText("Landscape");
        jLabel22.setAlignmentX(0.5F);
        jLabel22.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel22.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel22.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel23.setText("Swimming Pool");
        jLabel23.setAlignmentX(0.5F);
        jLabel23.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel23.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel23.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel24.setText("Club House");
        jLabel24.setAlignmentX(0.5F);
        jLabel24.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel24.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel24.setPreferredSize(new java.awt.Dimension(20, 80));

        peopleDriverInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peopleDriverInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peopleDriverInput.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePerLandscapeInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerLandscapeInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerLandscapeInput.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePerSwimmingPoolInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerSwimmingPoolInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerSwimmingPoolInput.setPreferredSize(new java.awt.Dimension(20, 80));

        peoplePerClubHouseInput.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerClubHouseInput.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerClubHouseInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel140.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel140.setForeground(new java.awt.Color(0, 0, 255));
        jLabel140.setText("(Person/flat)");

        javax.swing.GroupLayout peoplePerTypeInputPanelLayout = new javax.swing.GroupLayout(peoplePerTypeInputPanel);
        peoplePerTypeInputPanel.setLayout(peoplePerTypeInputPanelLayout);
        peoplePerTypeInputPanelLayout.setHorizontalGroup(
            peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peoplePer1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(peoplePer2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(peoplePer2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peoplePODsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(peopleServantInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peopleDriverInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(peoplePerLandscapeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peoplePerSwimmingPoolInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(peoplePer3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(peoplePer3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(peoplePer4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peoplePer4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                                .addComponent(peoplePerClubHouseInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peoplePerStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(23, Short.MAX_VALUE))))
        );
        peoplePerTypeInputPanelLayout.setVerticalGroup(
            peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel140))
                .addGap(8, 8, 8)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(peoplePer3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePODsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peopleServantInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peopleDriverInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerLandscapeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerSwimmingPoolInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerClubHouseInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jLabel3.setText("Water Demand");

        jLabel48.setText("Gas Bank");

        jLabel49.setText("Solar ");

        jLabel50.setBackground(new java.awt.Color(204, 255, 255));
        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(0, 0, 255));
        jLabel50.setText("Residential Consumption Parameter");

        jLabel64.setText("Solid Waste");

        jLabel91.setText("<html>Dry Garbage</html>");

        jLabel92.setText("<html>Wet Garbage</html>");

        jLabel144.setText("(liters/day)");

        jLabel145.setText("<html>% of total waste generation</html>");

        jLabel146.setText("(kg/p/d)");

        jLabel148.setText("(kg/flat/day)");

        jLabel151.setText("%");

        jLabel153.setText("lpcd");

        javax.swing.GroupLayout consumptionParamPanelLayout = new javax.swing.GroupLayout(consumptionParamPanel);
        consumptionParamPanel.setLayout(consumptionParamPanelLayout);
        consumptionParamPanelLayout.setHorizontalGroup(
            consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50)
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53)
                        .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                                .addComponent(dryGarbageInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel151))
                            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                                .addComponent(stdGasBank, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel148))
                            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                                .addComponent(stdwaterDemand, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel144, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(117, 117, 117)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, consumptionParamPanelLayout.createSequentialGroup()
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(wetGarbageInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, consumptionParamPanelLayout.createSequentialGroup()
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(stdSolidWasteKgPPD, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(stdSolar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel146)
                    .addComponent(jLabel145, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel153))
                .addGap(0, 88, Short.MAX_VALUE))
        );
        consumptionParamPanelLayout.setVerticalGroup(
            consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stdwaterDemand, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel144)
                    .addComponent(stdSolidWasteKgPPD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel146))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(stdGasBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148)
                    .addComponent(stdSolar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel153))
                .addGap(18, 18, 18)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(wetGarbageInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel145, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dryGarbageInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel151)))
                .addGap(18, 18, 18))
        );

        inputCriteriaTabNextBtn.setText("Next");
        inputCriteriaTabNextBtn.setAlignmentX(0.5F);
        inputCriteriaTabNextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputCriteriaTabNextBtnActionPerformed(evt);
            }
        });

        modifyStdVals.setText("Modify Standard Values");
        modifyStdVals.setActionCommand("");
        modifyStdVals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyStdValsActionPerformed(evt);
            }
        });

        jLabel87.setText("<html>Office:</html>");

        jLabel86.setText("<html>Merchantial Ground floor</html>");

        jLabel89.setText("<html>Sq. Mtr. Per Person</html>");

        jLabel81.setBackground(new java.awt.Color(204, 255, 255));
        jLabel81.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(0, 0, 255));
        jLabel81.setText("Commertial Population Criteria");

        jLabel141.setText("<html>Sq. Mtr. Per Person</html>");

        jLabel142.setText("<html>Sq. Mtr. Per Person</html>");

        jLabel143.setText("<html>Merchantial Above Ground floor</html>");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sqMtrPerPersonOfficeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel141, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(sqMtrPerPersonAboveGroundInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(sqMtrPerPersonAtGroundInput, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sqMtrPerPersonOfficeInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87)
                    .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sqMtrPerPersonAtGroundInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel141, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sqMtrPerPersonAboveGroundInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel27.setText("Water Demand");

        jLabel94.setText("Gas Bank");

        jLabel95.setText("Solar ");
        jLabel95.setEnabled(false);

        stdSolarCommertial.setEnabled(false);

        jLabel96.setBackground(new java.awt.Color(204, 255, 255));
        jLabel96.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel96.setForeground(new java.awt.Color(0, 0, 255));
        jLabel96.setText("Commertial Consumption Parameter");

        jLabel97.setText("Solid Waste");

        jLabel98.setText("<html>Dry Garbage</html>");

        jLabel99.setText("<html>Wet Garbage</html>");

        jLabel147.setText("<html>% of total waste generation</html>");

        jLabel149.setText("(kg/day)");

        jLabel150.setText("%");

        jLabel152.setText("(liters/day)");

        jLabel154.setText("(kg/p/d)");

        javax.swing.GroupLayout consumptionParamPanel1Layout = new javax.swing.GroupLayout(consumptionParamPanel1);
        consumptionParamPanel1.setLayout(consumptionParamPanel1Layout);
        consumptionParamPanel1Layout.setHorizontalGroup(
            consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel96)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                        .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stdwaterDemandCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dryGarbageInputCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel149)
                                    .addComponent(jLabel150)
                                    .addComponent(jLabel152, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(stdGasBankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                        .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel95)
                                .addGap(127, 127, 127)
                                .addComponent(stdSolarCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(stdSolidWasteKgPPCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel154))
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addGap(157, 157, 157)
                                .addComponent(wetGarbageInputCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel147, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(125, 125, 125))))
        );
        consumptionParamPanel1Layout.setVerticalGroup(
            consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                .addComponent(jLabel96)
                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel27)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel94)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, consumptionParamPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(stdSolidWasteKgPPCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel154))
                                    .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 22, 22)
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stdSolarCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel95))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(wetGarbageInputCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel147, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel99, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(consumptionParamPanel1Layout.createSequentialGroup()
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stdwaterDemandCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel152))
                                .addGap(22, 22, 22)
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stdGasBankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel149))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(consumptionParamPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(dryGarbageInputCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel150))))
                        .addGap(32, 32, 32))))
        );

        javax.swing.GroupLayout inputCriteriaFormLayout = new javax.swing.GroupLayout(inputCriteriaForm);
        inputCriteriaForm.setLayout(inputCriteriaFormLayout);
        inputCriteriaFormLayout.setHorizontalGroup(
            inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addGap(507, 507, 507)
                        .addComponent(inputCriteriaTabNextBtn))
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(modifyStdVals, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(consumptionParamPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerTypeInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 983, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(consumptionParamPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(163, Short.MAX_VALUE))
        );
        inputCriteriaFormLayout.setVerticalGroup(
            inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(modifyStdVals)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(peoplePerTypeInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(consumptionParamPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(consumptionParamPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inputCriteriaTabNextBtn)
                .addGap(43, 43, 43))
        );

        tabbedPane.addTab("Design Criteria", inputCriteriaForm);

        peopertTypeSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Residential", "Commertial" }));
        peopertTypeSelector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                peopertTypeSelectorItemStateChanged(evt);
            }
        });
        peopertTypeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peopertTypeSelectorActionPerformed(evt);
            }
        });

        jLabel7.setText("Landscape area");

        jLabel8.setText("<html>Swimming Pool <br>Capacity</html>");

        jLabel9.setText("Club House Area");

        swimmingAreaInput.setMinimumSize(new java.awt.Dimension(80, 30));

        landscapeAreaInput.setMinimumSize(new java.awt.Dimension(80, 30));

        outsideAreaBtn.setText("Add Other area");
        outsideAreaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outsideAreaBtnActionPerformed(evt);
            }
        });

        clubHouseAreaInput.setMinimumSize(new java.awt.Dimension(80, 30));

        swimmingPoolCapacity.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4", "5", "6", "7", "8" }));
        swimmingPoolCapacity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swimmingPoolCapacityActionPerformed(evt);
            }
        });

        landscapeAreaWater.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "6", "8" }));

        jLabel53.setText("<html>ltr per  sq.m <br> of green area</html>");

        jLabel70.setText("<html>% of <br> Pool Capacity </html>");

        jLabel39.setText("Sq. mtr");

        jLabel71.setText("Liters");

        jLabel72.setText("Sq. mtr");

        javax.swing.GroupLayout outsideAreaPanelLayout = new javax.swing.GroupLayout(outsideAreaPanel);
        outsideAreaPanel.setLayout(outsideAreaPanelLayout);
        outsideAreaPanelLayout.setHorizontalGroup(
            outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outsideAreaPanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(outsideAreaBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(outsideAreaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(swimmingAreaInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(landscapeAreaInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clubHouseAreaInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(outsideAreaPanelLayout.createSequentialGroup()
                        .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outsideAreaPanelLayout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(landscapeAreaWater, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outsideAreaPanelLayout.createSequentialGroup()
                                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(swimmingPoolCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                            .addComponent(jLabel53)))
                    .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        outsideAreaPanelLayout.setVerticalGroup(
            outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outsideAreaPanelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(landscapeAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(landscapeAreaWater, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel39)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(swimmingAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(swimmingPoolCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel71)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(clubHouseAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(outsideAreaBtn)
                .addContainerGap())
        );

        towerDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        towerDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                towerDataTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(towerDataTable);

        typePanel.setLayout(new java.awt.CardLayout());

        residentialPanel.setForeground(new java.awt.Color(255, 255, 0));

        addTowerBtn.setText("Add Tower");
        addTowerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTowerBtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Type");

        jLabel62.setText("Tower Name");

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("1 BHK");
        jLabel40.setAlignmentX(0.5F);
        jLabel40.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel40.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel40.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("2 BHK");
        jLabel41.setAlignmentX(0.5F);
        jLabel41.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel41.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel41.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("2.5 BHK");
        jLabel42.setAlignmentX(0.5F);
        jLabel42.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel42.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel42.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("3 BHK");
        jLabel43.setAlignmentX(0.5F);
        jLabel43.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel43.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel43.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel43.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel44.setText("3.5 BHK");
        jLabel44.setAlignmentX(0.5F);
        jLabel44.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel44.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel44.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel44.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel45.setText("4 BHK");
        jLabel45.setAlignmentX(0.5F);
        jLabel45.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel45.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel45.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel45.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel46.setText("4.5 BHK");
        jLabel46.setAlignmentX(0.5F);
        jLabel46.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel46.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel46.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("Studio");
        jLabel47.setAlignmentX(0.5F);
        jLabel47.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel47.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel47.setPreferredSize(new java.awt.Dimension(20, 80));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel40, jLabel62});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel40, jLabel62});

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("# of Flats");

        nrFlats1BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats1BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats1BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats2BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2NHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2NHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats2NHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats3BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats3AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats4BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats4AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrStudioInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrStudioInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrStudioInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel38.setText("Nos");

        jLabel33.setText("Nos");

        jLabel32.setText("Nos");

        jLabel37.setText("Nos");

        jLabel36.setText("Nos");

        jLabel34.setText("Nos");

        jLabel31.setText("Nos");

        jLabel35.setText("Nos");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(towerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nrStudioInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel19Layout.createSequentialGroup()
                                            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(1, 1, 1)))
                                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel19Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nrFlats1BHKInput, nrFlats2BHKInput, nrFlats2NHalfBHKInput, nrFlats3AndHalfBHKInput, nrFlats3BHKInput, nrFlats4AndHalfBHKInput, nrFlats4BHKInput, nrStudioInput, towerNameInput});

        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(towerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel19Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {nrFlats1BHKInput, nrFlats2BHKInput, nrFlats2NHalfBHKInput, nrFlats3AndHalfBHKInput, nrFlats3BHKInput, nrFlats4AndHalfBHKInput, nrFlats4BHKInput, nrStudioInput, towerNameInput});

        javax.swing.GroupLayout residentialPanelLayout = new javax.swing.GroupLayout(residentialPanel);
        residentialPanel.setLayout(residentialPanelLayout);
        residentialPanelLayout.setHorizontalGroup(
            residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(addTowerBtn)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        residentialPanelLayout.setVerticalGroup(
            residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTowerBtn)
                .addGap(10, 10, 10))
        );

        typePanel.add(residentialPanel, "card2");

        jLabel82.setText("Floor Number");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setText("Type");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("# of Flats");

        jLabel83.setText("Type Of Area");

        jLabel84.setText("Area (m sq)");

        jLabel85.setText("No. of floors");

        jLabel88.setText("No. of Shifts");

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        officeType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Office", "Showroom" }));

        nrFloorInput.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4" }));

        javax.swing.GroupLayout officeShowroomPanelLayout = new javax.swing.GroupLayout(officeShowroomPanel);
        officeShowroomPanel.setLayout(officeShowroomPanelLayout);
        officeShowroomPanelLayout.setHorizontalGroup(
            officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(officeShowroomPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(officeShowroomPanelLayout.createSequentialGroup()
                        .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(officeShowroomPanelLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24)
                        .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nrOfShiftsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(officeAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(officeFloorNrInput, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(officeType, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nrFloorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 21, Short.MAX_VALUE))
        );
        officeShowroomPanelLayout.setVerticalGroup(
            officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(officeShowroomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(officeFloorNrInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(officeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(officeAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFloorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(officeShowroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88)
                    .addComponent(nrOfShiftsInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        typePanel.add(officeShowroomPanel, "card4");

        inputDataNextBtn.setText("Next");
        inputDataNextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputDataNextBtnActionPerformed(evt);
            }
        });

        officeDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        officeDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                officeDataTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(officeDataTable);

        towerGroupTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "<html><b>Sr.</b></html>", "<html><b>Grouped Towers</b></html>"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        towerGroupTable.getTableHeader().setResizingAllowed(false);
        towerGroupTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(towerGroupTable);

        groupTowers.setText("Group");
        groupTowers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupTowersActionPerformed(evt);
            }
        });

        towerGroupScrollPane.setMaximumSize(new java.awt.Dimension(100, 400));

        towerGroupPanel.setMaximumSize(new java.awt.Dimension(100, 400));
        towerGroupPanel.setPreferredSize(new java.awt.Dimension(183, 400));

        javax.swing.GroupLayout towerGroupPanelLayout = new javax.swing.GroupLayout(towerGroupPanel);
        towerGroupPanel.setLayout(towerGroupPanelLayout);
        towerGroupPanelLayout.setHorizontalGroup(
            towerGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 219, Short.MAX_VALUE)
        );
        towerGroupPanelLayout.setVerticalGroup(
            towerGroupPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        towerGroupScrollPane.setViewportView(towerGroupPanel);

        clearGroupedTowerBtn.setText("Clear");
        clearGroupedTowerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearGroupedTowerBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(groupTowers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearGroupedTowerBtn)
                        .addGap(97, 97, 97))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(towerGroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(towerGroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(groupTowers)
                    .addComponent(clearGroupedTowerBtn))
                .addContainerGap())
        );

        jLabel51.setBackground(new java.awt.Color(204, 255, 255));
        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 0, 255));
        jLabel51.setText("PLOT AREA( SQ.MTRS )");

        jLabel52.setText("TERRACE");

        jLabel54.setText("GREEN AREA");

        jLabel55.setText("PAVED AREA");

        jLabel56.setText("TOTAL PLOT AREA");

        totalPlotArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalPlotAreaActionPerformed(evt);
            }
        });

        jLabel57.setText("<html>EXTRA AREA TO BE CATERED</html>");

        jLabel132.setText("mtrs");

        jLabel136.setText("mtrs");

        jLabel137.setText("mtrs");

        jLabel138.setText("mtrs");

        jLabel139.setText("mtrs");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel51)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel56)
                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(totalPlotArea, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel137))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(terraceArea, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel132)))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(greenArea, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel136)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(pavedArea, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel139))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(extraAreaCatered, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel138)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(greenArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(terraceArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55)
                    .addComponent(pavedArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel132)
                    .addComponent(jLabel136)
                    .addComponent(jLabel139))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel138)
                            .addComponent(extraAreaCatered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel56)
                        .addComponent(totalPlotArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel137)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel59.setText("<html>HEIGHT OF <br>TALLEST BUILDING <br>FROM GR</html>");
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 110, 50));
        jPanel3.add(heightOfTallestBld, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 50, 20));
        jPanel3.add(residualHeadPlumbing, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 40, 50, -1));

        jLabel60.setText("<html>BASEMENT AREA</html>");
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 100, 20));
        jPanel3.add(basementArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 50, -1));

        jLabel61.setText("<html>RESIDUAL HEAD <br>(PLUMBING)</html>");
        jPanel3.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 30, 120, 40));
        jPanel3.add(frictionLossInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 50, -1));

        jLabel63.setText("<html>RESIDUAL HEAD (FIRE)</html>");
        jPanel3.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 130, 22));
        jPanel3.add(residualHeadFire, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 50, -1));

        jLableStdFrictionLoss.setText("FRICTION LOSS");
        jPanel3.add(jLableStdFrictionLoss, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 90, 22));

        jLabel58.setBackground(new java.awt.Color(204, 255, 255));
        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(0, 0, 255));
        jLabel58.setText("Design Criteria for Fire Fighting");
        jPanel3.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel130.setText("Sq. mtrs");
        jPanel3.add(jLabel130, new org.netbeans.lib.awtextra.AbsoluteConstraints(472, 40, 50, 20));

        jLabel131.setText("mtrs");
        jPanel3.add(jLabel131, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 40, -1, -1));

        jLabel133.setText("mtrs");
        jPanel3.add(jLabel133, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, -1, -1));

        jLabel134.setText("mtrs");
        jPanel3.add(jLabel134, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 90, -1, -1));

        jLabel135.setText("mtrs");
        jPanel3.add(jLabel135, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, -1, -1));

        jLabel90.setBackground(new java.awt.Color(204, 255, 255));
        jLabel90.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(0, 0, 255));
        jLabel90.setText("Residential");

        jLabel1.setText("UGT Capacity");

        jLabel106.setText("Domestic Tank");

        jLabel101.setText("Raw Water Tank");

        jLabel104.setText(" Fire Fighting Tank");

        jLabel103.setText("FlushingTank (days)");

        jLabel73.setText("1 day");

        jLabel74.setText("0.5 day");

        jLabel79.setText("Liters");

        jLabel80.setText("1 day");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel73)
                        .addGap(32, 32, 32)
                        .addComponent(ugtDomesticWaterTankPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel74)
                        .addGap(18, 18, 18)
                        .addComponent(ugtRawWaterTankPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel104)
                        .addGap(39, 39, 39)
                        .addComponent(ugtFireFightingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel79)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel103)
                        .addGap(18, 18, 18)
                        .addComponent(ugtFlushingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel80)))
                .addContainerGap(107, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ugtDomesticWaterTankPercent)
                    .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ugtRawWaterTankPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ugtFireFightingTank)
                    .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ugtFlushingTank)
                    .addComponent(jLabel73)
                    .addComponent(jLabel74)
                    .addComponent(jLabel79)
                    .addComponent(jLabel80))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLabel2.setText("OHT Capacity");

        jLabel107.setText("Domestic Tank");

        jLabel109.setText(" Fire Fighting Tank");

        jLabel110.setText("FlushingTank (days)");

        ohtDomesticTankPercent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ohtDomesticTankPercentActionPerformed(evt);
            }
        });

        jLabel75.setText("0.5 day");

        jLabel76.setText("Liters");

        jLabel118.setText("day");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel75)
                        .addGap(22, 22, 22)
                        .addComponent(ohtDomesticTankPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel109)
                        .addGap(61, 61, 61)
                        .addComponent(ohtFireFightingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel110)
                        .addGap(32, 32, 32)
                        .addComponent(ohtFlushingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(362, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ohtFlushingTank)
                    .addComponent(ohtDomesticTankPercent)
                    .addComponent(ohtFireFightingTank)
                    .addComponent(jLabel75)
                    .addComponent(jLabel76)
                    .addComponent(jLabel118))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jLabel4.setText("STP Capacity");

        jLabel93.setText("Domestic Flow to STP");

        jLabel100.setText("Flushing Flow to STP");

        jLabel119.setText("%");

        jLabel120.setText("% of Total water generated");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(domesticSewerPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel119)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel100)
                        .addGap(47, 47, 47)
                        .addComponent(flushingSewerPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel120)))
                .addContainerGap(476, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(domesticSewerPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flushingSewerPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119)
                    .addComponent(jLabel120))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel90)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel102.setBackground(new java.awt.Color(204, 255, 255));
        jLabel102.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(0, 0, 255));
        jLabel102.setText("Commertial");

        jLabel28.setText("UGT Capacity");

        jLabel108.setText("Domestic Tank");

        jLabel105.setText("Raw Water Tank");

        jLabel111.setText("<html> Fire Fighting Tank<br>(optional)</html>");

        jLabel112.setText("FlushingTank (days)");

        jLabel121.setText("1 day");

        jLabel122.setText("0.5 day");

        jLabel123.setText("Liters");

        jLabel124.setText("1 day");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel28)
                .addContainerGap(995, Short.MAX_VALUE))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel121)
                .addGap(27, 27, 27)
                .addComponent(ugtDomesticWaterTankPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel122)
                .addGap(18, 18, 18)
                .addComponent(ugtRawWaterTankPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(ugtFireFightingTankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel123)
                .addGap(18, 18, 18)
                .addComponent(jLabel112)
                .addGap(43, 43, 43)
                .addComponent(ugtFlushingTankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel124)
                .addGap(85, 85, 85))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ugtDomesticWaterTankPercentCommertial)
                            .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ugtRawWaterTankPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ugtFireFightingTankCommertial)
                            .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ugtFlushingTankCommertial)
                            .addComponent(jLabel121)
                            .addComponent(jLabel122)
                            .addComponent(jLabel123)
                            .addComponent(jLabel124))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(12, Short.MAX_VALUE))))
        );

        jLabel29.setText("OHT Capacity");

        jLabel113.setText("Domestic Tank");

        jLabel114.setText(" Fire Fighting Tank");

        jLabel115.setText("FlushingTank (days)");

        ohtDomesticTankPercentCommertial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ohtDomesticTankPercentCommertialActionPerformed(evt);
            }
        });

        ohtFireFightingTankCommertial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ohtFireFightingTankCommertialActionPerformed(evt);
            }
        });

        jLabel125.setText("1 day");

        jLabel126.setText("Liters");

        jLabel127.setText("day");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel125)
                        .addGap(27, 27, 27)
                        .addComponent(ohtDomesticTankPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jLabel114)
                        .addGap(60, 60, 60)
                        .addComponent(ohtFireFightingTankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel126, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel115)
                        .addGap(21, 21, 21)
                        .addComponent(ohtFlushingTankCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(347, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ohtFlushingTankCommertial)
                    .addComponent(ohtDomesticTankPercentCommertial)
                    .addComponent(ohtFireFightingTankCommertial)
                    .addComponent(jLabel125)
                    .addComponent(jLabel126)
                    .addComponent(jLabel127))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setText("STP Capacity");

        jLabel116.setText("Domestic Flow to STP");

        jLabel117.setText("Flushing Flow to STP");

        jLabel128.setText("%");

        jLabel129.setText("% of Total water generated");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(domesticSewerPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel128)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel117)
                        .addGap(46, 46, 46)
                        .addComponent(flushingSewerPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel129)))
                .addContainerGap(472, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(domesticSewerPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flushingSewerPercentCommertial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel128)
                    .addComponent(jLabel129))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16))))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel102)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(peopertTypeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(typePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(outsideAreaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 83, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(486, 486, 486)
                .addComponent(inputDataNextBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(peopertTypeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(typePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)))
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outsideAreaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(235, 235, 235)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inputDataNextBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(563, 563, 563)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(546, Short.MAX_VALUE)))
        );

        jScrollPane2.setViewportView(mainPanel);

        javax.swing.GroupLayout inputDataPanelLayout = new javax.swing.GroupLayout(inputDataPanel);
        inputDataPanel.setLayout(inputDataPanelLayout);
        inputDataPanelLayout.setHorizontalGroup(
            inputDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1070, Short.MAX_VALUE)
        );
        inputDataPanelLayout.setVerticalGroup(
            inputDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
        );

        tabbedPane.addTab("Project Details", inputDataPanel);

        populationCriteriaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout populationCriteriaPanelLayout = new javax.swing.GroupLayout(populationCriteriaPanel);
        populationCriteriaPanel.setLayout(populationCriteriaPanelLayout);
        populationCriteriaPanelLayout.setHorizontalGroup(
            populationCriteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        populationCriteriaPanelLayout.setVerticalGroup(
            populationCriteriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 105, Short.MAX_VALUE)
        );

        jLabel66.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 102, 255));
        jLabel66.setText("Population Criteria");

        jLabel65.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(0, 102, 255));
        jLabel65.setText("Water Demand");

        javax.swing.GroupLayout waterDemandOverviewPanelLayout = new javax.swing.GroupLayout(waterDemandOverviewPanel);
        waterDemandOverviewPanel.setLayout(waterDemandOverviewPanelLayout);
        waterDemandOverviewPanelLayout.setHorizontalGroup(
            waterDemandOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );
        waterDemandOverviewPanelLayout.setVerticalGroup(
            waterDemandOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );

        jLabel67.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(0, 102, 255));
        jLabel67.setText("Tower Data");

        jLabel68.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 102, 255));
        jLabel68.setText("Office Data");

        javax.swing.GroupLayout outdoorOverviewPanelLayout = new javax.swing.GroupLayout(outdoorOverviewPanel);
        outdoorOverviewPanel.setLayout(outdoorOverviewPanelLayout);
        outdoorOverviewPanelLayout.setHorizontalGroup(
            outdoorOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 367, Short.MAX_VALUE)
        );
        outdoorOverviewPanelLayout.setVerticalGroup(
            outdoorOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 115, Short.MAX_VALUE)
        );

        jLabel69.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 102, 255));
        jLabel69.setText("Outdoor Area");

        officeDataOverviewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(officeDataOverviewTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        towerDataOveriewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(towerDataOveriewTable);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        projectNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectNameInputActionPerformed(evt);
            }
        });

        jLabel77.setText("Project Name");

        jLabel78.setText("PDF Location");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(projectNameInput)
                    .addComponent(pdfLocationInput, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(pdfLocationInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        waterDemandPDFBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/export-pdf.png"))); // NOI18N
        waterDemandPDFBtn.setText("<html> Export <br>PDF </html>");
        waterDemandPDFBtn.setToolTipText("");
        waterDemandPDFBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterDemandPDFBtnActionPerformed(evt);
            }
        });

        saveProjectBtn.setBackground(new java.awt.Color(242, 242, 242));
        saveProjectBtn.setText("Save");
        saveProjectBtn.setMaximumSize(new java.awt.Dimension(50, 50));
        saveProjectBtn.setMinimumSize(new java.awt.Dimension(50, 50));
        saveProjectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(427, 427, 427)
                .addComponent(waterDemandPDFBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveProjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(waterDemandPDFBtn, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveProjectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout overviewPanelLayout = new javax.swing.GroupLayout(overviewPanel);
        overviewPanel.setLayout(overviewPanelLayout);
        overviewPanelLayout.setHorizontalGroup(
            overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(populationCriteriaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(overviewPanelLayout.createSequentialGroup()
                        .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69)
                            .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65)
                            .addComponent(waterDemandOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outdoorOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(overviewPanelLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(overviewPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel68)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(overviewPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        overviewPanelLayout.setVerticalGroup(
            overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewPanelLayout.createSequentialGroup()
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(populationCriteriaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(waterDemandOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(jLabel68))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outdoorOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        tabbedPane.addTab("Overview", overviewPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1070, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void waterDemandPDFBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterDemandPDFBtnActionPerformed
        try {
            if(projectNameInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empty project name", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(pdfLocationInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empty PDF location", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String pdfFolderLocation = pdfLocationInput.getText();
                PdfData.PdfDataBuilder builder = getPdfDataBuilder();
                builder.setPdfLocation(pdfFolderLocation);
                PdfData pdfData = builder.build();
                String pdfFileLocation = pdfData.getPdfLocation()+ "/" + pdfData.getProjectName()+".pdf";
                File outputFile = new File(pdfFileLocation);
                if(!outputFile.exists()) {
                    PDFGenerator.generatePDF(pdfData, this);
                } else {
                    JOptionPane.showMessageDialog(this, "PDF with same project name exist", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (HeadlessException ex) {
            log.error("Error file reading", ex);
        }
    }//GEN-LAST:event_waterDemandPDFBtnActionPerformed

    private PdfData.PdfDataBuilder getPdfDataBuilder() {
        PdfData.PdfDataBuilder builder = new PdfData.PdfDataBuilder(standardValMap, towersList);
        builder.setOfficesList(officesList)
                .setOutsideAreaMap(outsideAreaMap)
                .setGroupedTowerNamesList(groupedTowerNamesList)
                .setCapacityDetailsResidential(capacityDetailsResidential)
                .setCapacityDetailsCommertial(capacityDetailsCommertial)
                .setFireFightingDetails(fireFightingDetails)
                .setPlotArea(plotArea)
                .setProjectName(projectNameInput.getText());
        return builder;
    }

    private void outsideAreaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outsideAreaBtnActionPerformed
        boolean valid = true;
        TreeMap<StandardType,Integer> outsideArea = new TreeMap<>();
        try {
            Integer landscapeArea = DataUtils.getIntegerVal(landscapeAreaInput.getText());
            outsideArea.put(StandardType.LANDSCAPE, landscapeArea);
        } catch (InvalidInputException ex) {
            valid = false;
        }
        try {
            Integer swimmingPoolArea = DataUtils.getIntegerVal(swimmingAreaInput.getText());
            outsideArea.put(StandardType.SWIMMING_POOL, swimmingPoolArea);
        } catch (InvalidInputException ex) {
            valid = false;
        }
        try {
            Integer clubHouseArea = DataUtils.getIntegerVal(clubHouseAreaInput.getText());
            outsideArea.put(StandardType.CLUB_HOUSE, clubHouseArea);
        } catch (InvalidInputException ex) {
            valid = false;
        }
        if(valid) {
            outsideAreaMap.clear();
            outsideAreaMap.putAll(outsideArea);
        } else {
            outsideArea.clear();
        }
        
        StandardValues swimmingPoolPercent = standardValMap.get(StandardType.SWIMMING_POOL_AREA_PERCENT);
        swimmingPoolPercent.setValue(Double.parseDouble(swimmingPoolCapacity.getSelectedItem().toString()));
        standardValMap.put(StandardType.SWIMMING_POOL_AREA_PERCENT,swimmingPoolPercent);

        StandardValues landscapeAreaPercent = standardValMap.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE);
        landscapeAreaPercent.setValue(Double.parseDouble(landscapeAreaWater.getSelectedItem().toString()));
        standardValMap.put(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE,landscapeAreaPercent);
    }//GEN-LAST:event_outsideAreaBtnActionPerformed

    private void inputDataNextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputDataNextBtnActionPerformed
        if (!towersList.isEmpty() || !officesList.isEmpty()) {
            if(validateAndSetInputs()) {
                createOverview();
                tabbedPane.setEnabledAt(2, true);
                tabbedPane.setSelectedIndex(2);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No Data provided?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_inputDataNextBtnActionPerformed

    private void createOverview() {
        addPopulationCriteriaOverview();
        addWaterDemandOverview();
        addTowerDataOverview();
        addOfficeDataOverview();
        addOutdoorDataOverview();
    }
    
    private void addPopulationCriteriaOverview() {
        populationCriteriaPanel.removeAll();
        populationCriteriaPanel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        populationCriteriaPanel.setLayout(new GridLayout(4, 4, 10, 10));
        
        populationCriteriaPanel.add(getOverViewLabel(StandardType.ONE_BHK.getValue(), standardValMap.get(StandardType.ONE_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.TWO_BHK.getValue(), standardValMap.get(StandardType.TWO_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.TWO_N_HALF_BHK.getValue(), standardValMap.get(StandardType.TWO_N_HALF_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.THREE_BHK.getValue(), standardValMap.get(StandardType.THREE_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.THREE_N_HALF_BHK.getValue(), standardValMap.get(StandardType.THREE_N_HALF_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.FOUR_BHK.getValue(), standardValMap.get(StandardType.FOUR_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.FOUR_AND_HALF_BHK.getValue(), standardValMap.get(StandardType.FOUR_AND_HALF_BHK)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.STUDIO.getValue(), standardValMap.get(StandardType.STUDIO)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.PODS.getValue(), standardValMap.get(StandardType.PODS)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.SERVANT.getValue(), standardValMap.get(StandardType.SERVANT)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.DRIVER.getValue(), standardValMap.get(StandardType.DRIVER)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.LANDSCAPE.getValue(), standardValMap.get(StandardType.LANDSCAPE)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.SWIMMING_POOL.getValue(), standardValMap.get(StandardType.SWIMMING_POOL)));
        populationCriteriaPanel.add(getOverViewLabel(StandardType.CLUB_HOUSE.getValue(), standardValMap.get(StandardType.CLUB_HOUSE)));
    }
    
    private JLabel getOverViewLabel(String labelText, StandardValues val) {
        JLabel jLabel = new JLabel("<html><b>" + labelText + ":</b> " + val);
        jLabel.setFont(baseFont);
        return jLabel;
    }
    
    private JLabel getOverViewLabel(String labelText, int val) {
        JLabel jLabel = new JLabel("<html><b>" + labelText + ":</b> " + val);
        jLabel.setFont(baseFont);
        return jLabel;
    }

    private void addWaterDemandOverview(){
        waterDemandOverviewPanel.removeAll();
        waterDemandOverviewPanel.setLayout(new GridLayout(1,1));
        waterDemandOverviewPanel.add(getOverViewLabel(StandardType.WATER_DEMAND.getValue(),standardValMap.get(StandardType.WATER_DEMAND)));
    }
    
    private void addTowerDataOverview(){
        if (!towersList.isEmpty()) {
            while(dtmTowerDataOverview.getRowCount()>0){
                dtmTowerDataOverview.removeRow(dtmTowerDataOverview.getRowCount()-1);
            }
            towersList.forEach(towerData -> {
                TreeMap<StandardType, Integer> flatsData = towerData.getFlatsData();
                addTowerDataOverviewRow(towerData.getName(), flatsData);
                towerDataOveriewTable.setVisible(true);
            });
        } else {
            towerDataOveriewTable.setVisible(false);
        }
    }
    
    private void addTowerDataOverviewRow(String towerName, TreeMap<StandardType, Integer> flatsData) {
        dtmTowerDataOverview.addRow(new Object[] { 
            towerName,
            flatsData.get(StandardType.ONE_BHK).toString(),
            flatsData.get(StandardType.TWO_BHK).toString(),
            flatsData.get(StandardType.TWO_N_HALF_BHK).toString(),
            flatsData.get(StandardType.THREE_BHK).toString(),
            flatsData.get(StandardType.THREE_N_HALF_BHK).toString(),
            flatsData.get(StandardType.FOUR_BHK).toString(),
            flatsData.get(StandardType.FOUR_AND_HALF_BHK).toString(),
            flatsData.get(StandardType.STUDIO).toString()
        });
    }
    
    private void addOfficeDataOverview() {
        while (dtmOfficeOverview.getRowCount() > 0) {
            dtmOfficeOverview.removeRow(dtmOfficeOverview.getRowCount() - 1);
        }
        if (!officesList.isEmpty()) {
            DataInitializer.setTableStyling(officeDataOverviewTable);
            addOfficeTableDataOverview();
            officeDataOverviewTable.setVisible(true);
        } else {
            officeDataOverviewTable.setVisible(false);
        }
    }
    
    private void addOfficeTableDataOverview() {
        officeDataOverviewTable.removeAll();
        for (int index = 0; index < officesList.size(); index++) {
            OfficeData officeData = officesList.get(index);
            double sqMtrPerPerson = officeData.getSqMtrPerPerson();
            
            dtmOfficeOverview.addRow(new Object[]{
                index + 1,
                officeData.getFloorNumer(),
                officeData.getType().getValue(),
                officeData.getArea(),
                officeData.getNumberOfFloors(),
                officeData.getSqMtrPerPerson(),
                officeData.getArea() / sqMtrPerPerson,
                officeData.getNrOfShifts(),
                officeData.getArea() * officeData.getNrOfShifts() / sqMtrPerPerson
            });
        }
    }
    
    private void addOutdoorDataOverview() {
        outdoorOverviewPanel.removeAll();
        outdoorOverviewPanel.setLayout(new GridLayout(3, 1, 10, 10));

        if(outsideAreaMap.size() > 0) {
            outdoorOverviewPanel.add(getOverViewLabel(StandardType.LANDSCAPE.getValue(), outsideAreaMap.get(StandardType.LANDSCAPE)));
            outdoorOverviewPanel.add(getOverViewLabel(StandardType.SWIMMING_POOL.getValue(), outsideAreaMap.get(StandardType.SWIMMING_POOL)));
            outdoorOverviewPanel.add(getOverViewLabel(StandardType.CLUB_HOUSE.getValue(), outsideAreaMap.get(StandardType.CLUB_HOUSE)));
        }
    }
    
    private void addTowerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTowerBtnActionPerformed
        String towerName = towerNameInput.getText();
        String nrOneBHKs = nrFlats1BHKInput.getText();
        String nrTwoBHKs = nrFlats2BHKInput.getText();
        String nrTwoAndHalfBHKs = nrFlats2NHalfBHKInput.getText();
        String nrThreeBHKs = nrFlats3BHKInput.getText();
        String nrThreeAndHalfBHKs = nrFlats3AndHalfBHKInput.getText();
        String nrFourBHKs = nrFlats4BHKInput.getText();
        String nrFourAndHalfBHKs = nrFlats4AndHalfBHKInput.getText();
        String nrStudios = nrStudioInput.getText();

        boolean valid = true;
        StringBuilder errorMsg = new StringBuilder("Please correct input : \n");
        TreeMap<StandardType,Integer> flatPerTower = new TreeMap<>();
        
        if(towerName.isEmpty()) {
            valid = false;
            errorMsg.append("Empty Name\n");
        }
        try {
            Integer oneBHKQty = DataUtils.getIntegerVal(nrOneBHKs);
            flatPerTower.put(StandardType.ONE_BHK, oneBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("1 BHK\n");
        }
        
        try {
            Integer twoBHKQty = DataUtils.getIntegerVal(nrTwoBHKs);
            flatPerTower.put(StandardType.TWO_BHK, twoBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("2 BHK\n");
        }
        
        try {
            Integer twoAndHalfBHKQty = DataUtils.getIntegerVal(nrTwoAndHalfBHKs);
            flatPerTower.put(StandardType.TWO_N_HALF_BHK, twoAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("2.5 BHK\n");
        }
        
        try {
            Integer threeBHKQty = DataUtils.getIntegerVal(nrThreeBHKs);
            flatPerTower.put(StandardType.THREE_BHK, threeBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("3 BHK\n");
        }
        try {
            Integer threeAndHalfBHKQty = DataUtils.getIntegerVal(nrThreeAndHalfBHKs);
            flatPerTower.put(StandardType.THREE_N_HALF_BHK, threeAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("3.5 BHK\n");
        }
        try {
            Integer fourBHKQty = DataUtils.getIntegerVal(nrFourBHKs);
            flatPerTower.put(StandardType.FOUR_BHK, fourBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("4 BHK\n");
        }
        try {
            Integer fourAndHalfBHKQty = DataUtils.getIntegerVal(nrFourAndHalfBHKs);
            flatPerTower.put(StandardType.FOUR_AND_HALF_BHK, fourAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("4.5 BHK\n");
        }
        try {
            Integer studioQty = DataUtils.getIntegerVal(nrStudios);
            flatPerTower.put(StandardType.STUDIO, studioQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("Studio\n");
        }
        if(towerNameList.contains(towerName)) {
            valid = false;
            errorMsg.append("Tower Name already exists\n");
        }
                
        if(valid) {
            towersList.add(new TowerData(towerName, flatPerTower));
            addRowToTowerTable(towerName, flatPerTower);
        } else {
            flatPerTower.clear();
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addTowerBtnActionPerformed

    private void towerDataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_towerDataTableMouseClicked
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            int row = towerDataTable.rowAtPoint(evt.getPoint());
            int col = towerDataTable.columnAtPoint(evt.getPoint());
            if (row >= 0 && col == 9) {
                int dialogResult = JOptionPane.showConfirmDialog (this, "Are you sure, you want to delete Tower Data?","Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    towersList.remove(row);
                    dtm.removeRow(row);
                    String towerName = towerNameList.get(row);
                    removeCheckBox(towerName);
                    towerNameList.remove(row);
                }
            } else {
                TowerDataEditor towerDataEditor = new TowerDataEditor(towersList, row, this);
                towerDataEditor.setVisible(true);
            }
        }
    }//GEN-LAST:event_towerDataTableMouseClicked

    private void addOfficeDataRow(OfficeData officeData) {
        dtmOffice.addRow(new Object[]{
            dtmOffice.getRowCount()+1,
            officeData.getFloorNumer(),
            officeData.getType().getValue(),
            officeData.getArea(),
            officeData.getNumberOfFloors(),
            officeData.getSqMtrPerPerson(),
            officeData.getNrOfShifts(),
            "<html><div style='text-align:center; color:red'><b> X </b></div></html>"
        });
    }

    private void officeDataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officeDataTableMouseClicked
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            int row = officeDataTable.rowAtPoint(evt.getPoint());
            int col = officeDataTable.columnAtPoint(evt.getPoint());
            if (row >= 0 && col == 7) {
                int dialogResult = JOptionPane.showConfirmDialog (this, "Are you sure, you want to delete Office Data?","Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    officesList.remove(row);
                    dtmOffice.removeRow(row);
                }
            } else {
                OfficeDataEditor officeDataEditor = new OfficeDataEditor(officesList, row, standardValMap, this);
                officeDataEditor.setVisible(true);
            }
        }
    }//GEN-LAST:event_officeDataTableMouseClicked

    private void inputCriteriaTabNextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputCriteriaTabNextBtnActionPerformed
        updateStandardValMap();
    }//GEN-LAST:event_inputCriteriaTabNextBtnActionPerformed

    private StandardValues getUpdatedStandardVal(StandardType stdType,String val) {
        double newVal = Double.parseDouble(val);
        return new StandardValues(standardValMap.get(stdType), newVal);
    }
   
    private void updateStandardValMap() {
        try {
            standardValMap.put(StandardType.ONE_BHK, getUpdatedStandardVal(StandardType.ONE_BHK, peoplePer1BHKInput.getText()));
            standardValMap.put(StandardType.TWO_BHK, getUpdatedStandardVal(StandardType.TWO_BHK, peoplePer2BHKInput.getText()));
            standardValMap.put(StandardType.TWO_N_HALF_BHK, getUpdatedStandardVal(StandardType.TWO_N_HALF_BHK, peoplePer2NHalfBHKInput.getText()));
            standardValMap.put(StandardType.THREE_BHK, getUpdatedStandardVal(StandardType.THREE_BHK, peoplePer3BHKInput.getText()));
            standardValMap.put(StandardType.THREE_N_HALF_BHK, getUpdatedStandardVal(StandardType.THREE_N_HALF_BHK, peoplePer3AndHalfBHKInput.getText()));
            standardValMap.put(StandardType.FOUR_BHK, getUpdatedStandardVal(StandardType.FOUR_BHK, peoplePer4BHKInput.getText()));
            standardValMap.put(StandardType.FOUR_AND_HALF_BHK, getUpdatedStandardVal(StandardType.FOUR_AND_HALF_BHK, peoplePer4AndHalfBHKInput.getText()));
            standardValMap.put(StandardType.STUDIO, getUpdatedStandardVal(StandardType.STUDIO, peoplePerStudioInput.getText()));
            standardValMap.put(StandardType.SERVANT, getUpdatedStandardVal(StandardType.SERVANT, peopleServantInput.getText()));
            standardValMap.put(StandardType.DRIVER, getUpdatedStandardVal(StandardType.DRIVER, peopleDriverInput.getText()));
            standardValMap.put(StandardType.PODS, getUpdatedStandardVal(StandardType.PODS, peoplePODsInput.getText()));
            standardValMap.put(StandardType.LANDSCAPE, getUpdatedStandardVal(StandardType.LANDSCAPE, peoplePerLandscapeInput.getText()));
            standardValMap.put(StandardType.SWIMMING_POOL, getUpdatedStandardVal(StandardType.SWIMMING_POOL, peoplePerSwimmingPoolInput.getText()));
            standardValMap.put(StandardType.CLUB_HOUSE, getUpdatedStandardVal(StandardType.CLUB_HOUSE, peoplePerClubHouseInput.getText()));

            standardValMap.put(StandardType.SQ_MTR_PER_PERSON_AT_GROUND, getUpdatedStandardVal(StandardType.SQ_MTR_PER_PERSON_AT_GROUND, sqMtrPerPersonAtGroundInput.getText()));
            standardValMap.put(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND, getUpdatedStandardVal(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND, sqMtrPerPersonAboveGroundInput.getText()));
            standardValMap.put(StandardType.SQ_MTR_PER_PERSON_OFFICE, getUpdatedStandardVal(StandardType.SQ_MTR_PER_PERSON_OFFICE, sqMtrPerPersonOfficeInput.getText()));
            
            standardValMap.put(StandardType.WATER_DEMAND, getUpdatedStandardVal(StandardType.WATER_DEMAND, stdwaterDemand.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_KG_PERSON_DAY, getUpdatedStandardVal(StandardType.SOLID_WASTE_KG_PERSON_DAY, stdSolidWasteKgPPD.getText()));
            standardValMap.put(StandardType.GAS_BANK, getUpdatedStandardVal(StandardType.GAS_BANK, stdGasBank.getText()));
            standardValMap.put(StandardType.SOLAR, getUpdatedStandardVal(StandardType.SOLAR, stdSolar.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_DRY_GARBAGE, getUpdatedStandardVal(StandardType.SOLID_WASTE_DRY_GARBAGE, dryGarbageInput.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_WET_GARBAGE, getUpdatedStandardVal(StandardType.SOLID_WASTE_WET_GARBAGE, wetGarbageInput.getText()));
            

            standardValMap.put(StandardType.WATER_DEMAND_COMMERTIAL, getUpdatedStandardVal(StandardType.WATER_DEMAND_COMMERTIAL, stdwaterDemandCommertial.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_KG_PERSON_DAY_COMMERTIAL, getUpdatedStandardVal(StandardType.SOLID_WASTE_KG_PERSON_DAY_COMMERTIAL, stdSolidWasteKgPPCommertial.getText()));
            standardValMap.put(StandardType.GAS_BANK_COMMERTIAL, getUpdatedStandardVal(StandardType.GAS_BANK_COMMERTIAL, stdGasBankCommertial.getText()));
            standardValMap.put(StandardType.SOLAR_COMMERTIAL, getUpdatedStandardVal(StandardType.SOLAR_COMMERTIAL, stdSolarCommertial.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_DRY_GARBAGE_COMMERTIAL, getUpdatedStandardVal(StandardType.SOLID_WASTE_DRY_GARBAGE_COMMERTIAL, dryGarbageInputCommertial.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_WET_GARBAGE_COMMERTIAL, getUpdatedStandardVal(StandardType.SOLID_WASTE_WET_GARBAGE_COMMERTIAL, wetGarbageInputCommertial.getText()));

            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifyStdValsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyStdValsActionPerformed
        JCheckBox chkBox = (JCheckBox) evt.getSource();
        boolean allowEdit = chkBox.isSelected();
        if(allowEdit) {
            updateStandardFieldEditiability(true);
        } else {
            updateStandardFieldEditiability(false);
        }
        
    }//GEN-LAST:event_modifyStdValsActionPerformed

    private void peopertTypeSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peopertTypeSelectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_peopertTypeSelectorActionPerformed

    private void swimmingPoolCapacityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swimmingPoolCapacityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swimmingPoolCapacityActionPerformed

    private void groupTowersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupTowersActionPerformed
        Component[] components = towerGroupPanel.getComponents();
        List<String> groupedTowers = new ArrayList<>();
        for(int i=0; i<components.length; i++) {
            if(components[i] instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox)components[i];
                if(checkbox.isSelected() && checkbox.isEnabled()) {
                    groupedTowers.add(checkbox.getName());
                    checkbox.setEnabled(false);
                }
            }
        }
        addGroupedTowerRow(groupedTowers);
    }//GEN-LAST:event_groupTowersActionPerformed

    private void addGroupedTowerRow(List<String> groupedTowers) {
        if (!groupedTowers.isEmpty()) {
            
//            groupedTowers.forEach(data -> addCheckBoxForTower(data, true));
            
            groupedTowerNamesList.add(groupedTowers);
            DefaultTableModel model = (DefaultTableModel) towerGroupTable.getModel();

            model.addRow(new Object[]{
                groupedTowerNamesList.size(),
                String.join(",", groupedTowers)
            });
        }
    }

    private void clearGroupedTowerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearGroupedTowerBtnActionPerformed
        unCheckGroupedTowers();
        groupedTowerNamesList.clear();
        DefaultTableModel dtm = (DefaultTableModel)towerGroupTable.getModel();
        int rowCnt = dtm.getRowCount();
        for (int i = 0; i < rowCnt; i++) {
            dtm.removeRow(0);
        }
        towerGroupTable.revalidate();
        towerGroupTable.repaint();
    }//GEN-LAST:event_clearGroupedTowerBtnActionPerformed

    private void projectNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectNameInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_projectNameInputActionPerformed

    private void saveProjectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectBtnActionPerformed
        try {
            PdfData projectData = getPdfDataBuilder().build();
            String appDataFolder = System.getenv("APPDATA");
            String location = "";
//            if (appDataFolder.isEmpty() || appDataFolder.isBlank()) {
                location = JOptionPane.showInputDialog(this, "Location:", "Save Project", JOptionPane.QUESTION_MESSAGE);
//            } else {
//                location = appDataFolder + "/plumbIT/projects";
//            }
            XStream xstream = DataInitializer.getXstream();
            File outputFile = new File(location + "/" + projectData.getProjectName() + ".pscs");
            if (!outputFile.exists()) {
                xstream.toXML(projectData, new FileOutputStream(outputFile));
                JOptionPane.showMessageDialog(this, "Project saved successfully");
            } else {
                JOptionPane.showMessageDialog(this, "PDF with same project name exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (FileNotFoundException ex) {
            log.error("Unable to save project", ex);
            JOptionPane.showMessageDialog(this, "Save project failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveProjectBtnActionPerformed

    private void peopertTypeSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_peopertTypeSelectorItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_peopertTypeSelectorItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String floorNr = officeFloorNrInput.getText();
        String type = (String)officeType.getSelectedItem();
        String officeAreaTxt = officeAreaInput.getText();
        int nrOfFloors = Integer.parseInt((String)nrFloorInput.getSelectedItem());
        String nrOfShiftsTxt = nrOfShiftsInput.getText();

        boolean valid = true;
        OfficeData officeData; 
        StringBuilder errorMsg = new StringBuilder("Please correct input : ");

        if(type.equals("Office")) {
            officeData = new OfficeData(getSqMtrPerPerson(floorNr, StandardType.OFFICE));
            officeData.setType(StandardType.OFFICE);
        } else {
            officeData = new OfficeData(getSqMtrPerPerson(floorNr, StandardType.SHOWROOM));
            officeData.setType(StandardType.SHOWROOM);
        }
        
        officeData.setFloorNumer(floorNr);
        
        try {
            int officeArea = DataUtils.getIntegerVal(officeAreaTxt);
            officeData.setArea(officeArea);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("Area\n");
        }

        officeData.setNumberOfFloors(nrOfFloors);

        try {
            float nrOfShifts = Float.parseFloat(nrOfShiftsTxt);
            officeData.setNrOfShifts(nrOfShifts);
        } catch(NumberFormatException ex) {
            valid = false;
            errorMsg.append("No. of shifts\n");
        }

        if(valid) {
            officesList.add(officeData);
            addOfficeDataRow(officeData);
        } else {
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ohtDomesticTankPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ohtDomesticTankPercentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ohtDomesticTankPercentActionPerformed

    private void ohtDomesticTankPercentCommertialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ohtDomesticTankPercentCommertialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ohtDomesticTankPercentCommertialActionPerformed

    private void ohtFireFightingTankCommertialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ohtFireFightingTankCommertialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ohtFireFightingTankCommertialActionPerformed

    private void totalPlotAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalPlotAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalPlotAreaActionPerformed

    private void generateWaterDemandPDF() {
        
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
       
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataCalculator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTowerBtn;
    private javax.swing.JTextField basementArea;
    private javax.swing.JButton clearGroupedTowerBtn;
    private javax.swing.JTextField clubHouseAreaInput;
    private javax.swing.JPanel consumptionParamPanel;
    private javax.swing.JPanel consumptionParamPanel1;
    private javax.swing.JTextField domesticSewerPercent;
    private javax.swing.JTextField domesticSewerPercentCommertial;
    private javax.swing.JTextField dryGarbageInput;
    private javax.swing.JTextField dryGarbageInputCommertial;
    private javax.swing.JTextField extraAreaCatered;
    private javax.swing.JTextField flushingSewerPercent;
    private javax.swing.JTextField flushingSewerPercentCommertial;
    private javax.swing.JTextField frictionLossInput;
    private javax.swing.JTextField greenArea;
    private javax.swing.JButton groupTowers;
    private javax.swing.JTextField heightOfTallestBld;
    private javax.swing.JPanel inputCriteriaForm;
    private javax.swing.JButton inputCriteriaTabNextBtn;
    private javax.swing.JButton inputDataNextBtn;
    private javax.swing.JPanel inputDataPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JLabel jLableStdFrictionLoss;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextField landscapeAreaInput;
    private javax.swing.JComboBox<String> landscapeAreaWater;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox modifyStdVals;
    private javax.swing.JTextField nrFlats1BHKInput;
    private javax.swing.JTextField nrFlats2BHKInput;
    private javax.swing.JTextField nrFlats2NHalfBHKInput;
    private javax.swing.JTextField nrFlats3AndHalfBHKInput;
    private javax.swing.JTextField nrFlats3BHKInput;
    private javax.swing.JTextField nrFlats4AndHalfBHKInput;
    private javax.swing.JTextField nrFlats4BHKInput;
    private javax.swing.JComboBox<String> nrFloorInput;
    private javax.swing.JTextField nrOfShiftsInput;
    private javax.swing.JTextField nrStudioInput;
    private javax.swing.JTextField officeAreaInput;
    private javax.swing.JTable officeDataOverviewTable;
    private javax.swing.JTable officeDataTable;
    private javax.swing.JTextField officeFloorNrInput;
    private javax.swing.JPanel officeShowroomPanel;
    private javax.swing.JComboBox<String> officeType;
    private javax.swing.JTextField ohtDomesticTankPercent;
    private javax.swing.JTextField ohtDomesticTankPercentCommertial;
    private javax.swing.JTextField ohtFireFightingTank;
    private javax.swing.JTextField ohtFireFightingTankCommertial;
    private javax.swing.JTextField ohtFlushingTank;
    private javax.swing.JTextField ohtFlushingTankCommertial;
    private javax.swing.JPanel outdoorOverviewPanel;
    private javax.swing.JButton outsideAreaBtn;
    private javax.swing.JPanel outsideAreaPanel;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JTextField pavedArea;
    private javax.swing.JTextField pdfLocationInput;
    private javax.swing.JComboBox<String> peopertTypeSelector;
    private javax.swing.JTextField peopleDriverInput;
    private javax.swing.JTextField peoplePODsInput;
    private javax.swing.JTextField peoplePer1BHKInput;
    private javax.swing.JTextField peoplePer2BHKInput;
    private javax.swing.JTextField peoplePer2NHalfBHKInput;
    private javax.swing.JTextField peoplePer3AndHalfBHKInput;
    private javax.swing.JTextField peoplePer3BHKInput;
    private javax.swing.JTextField peoplePer4AndHalfBHKInput;
    private javax.swing.JTextField peoplePer4BHKInput;
    private javax.swing.JTextField peoplePerClubHouseInput;
    private javax.swing.JTextField peoplePerLandscapeInput;
    private javax.swing.JTextField peoplePerStudioInput;
    private javax.swing.JTextField peoplePerSwimmingPoolInput;
    private javax.swing.JPanel peoplePerTypeInputPanel;
    private javax.swing.JTextField peopleServantInput;
    private javax.swing.JPanel populationCriteriaPanel;
    private javax.swing.JTextField projectNameInput;
    private javax.swing.JPanel residentialPanel;
    private javax.swing.JTextField residualHeadFire;
    private javax.swing.JTextField residualHeadPlumbing;
    private javax.swing.JButton saveProjectBtn;
    private javax.swing.JTextField sqMtrPerPersonAboveGroundInput;
    private javax.swing.JTextField sqMtrPerPersonAtGroundInput;
    private javax.swing.JTextField sqMtrPerPersonOfficeInput;
    private javax.swing.JTextField stdGasBank;
    private javax.swing.JTextField stdGasBankCommertial;
    private javax.swing.JTextField stdSolar;
    private javax.swing.JTextField stdSolarCommertial;
    private javax.swing.JTextField stdSolidWasteKgPPCommertial;
    private javax.swing.JTextField stdSolidWasteKgPPD;
    private javax.swing.JTextField stdwaterDemand;
    private javax.swing.JTextField stdwaterDemandCommertial;
    private javax.swing.JTextField swimmingAreaInput;
    private javax.swing.JComboBox<String> swimmingPoolCapacity;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField terraceArea;
    private javax.swing.JTextField totalPlotArea;
    private javax.swing.JTable towerDataOveriewTable;
    private javax.swing.JTable towerDataTable;
    private javax.swing.JPanel towerGroupPanel;
    private javax.swing.JScrollPane towerGroupScrollPane;
    private javax.swing.JTable towerGroupTable;
    private javax.swing.JTextField towerNameInput;
    private javax.swing.JPanel typePanel;
    private javax.swing.JTextField ugtDomesticWaterTankPercent;
    private javax.swing.JTextField ugtDomesticWaterTankPercentCommertial;
    private javax.swing.JTextField ugtFireFightingTank;
    private javax.swing.JTextField ugtFireFightingTankCommertial;
    private javax.swing.JTextField ugtFlushingTank;
    private javax.swing.JTextField ugtFlushingTankCommertial;
    private javax.swing.JTextField ugtRawWaterTankPercent;
    private javax.swing.JTextField ugtRawWaterTankPercentCommertial;
    private javax.swing.JPanel waterDemandOverviewPanel;
    private javax.swing.JButton waterDemandPDFBtn;
    private javax.swing.JTextField wetGarbageInput;
    private javax.swing.JTextField wetGarbageInputCommertial;
    // End of variables declaration//GEN-END:variables

    private void addCheckBoxForTower(String towerName) {
        addCheckBoxForTower(towerName, true);
    }
  
    private void addCheckBoxForTower(String towerName, boolean enabled){
        JCheckBox checkbox = new JCheckBox(towerName, enabled);
        checkbox.setName(towerName);
        checkbox.setMargin(new Insets(5,5,5,5));
        checkbox.setEnabled(enabled);
        towerGroupPanel.add(checkbox);
        towerGroupPanel.revalidate();
        towerGroupPanel.repaint();
    }
    private void removeCheckBox(String towerName) {
        Component componentByName = getComponentByName("checkBox"+towerName, towerGroupPanel);
        if(componentByName != null) {
            towerGroupPanel.remove(componentByName);
            towerGroupPanel.revalidate();
            towerGroupPanel.repaint();
        }
    }
    
    private void unCheckGroupedTowers() {
        Component[] components = towerGroupPanel.getComponents();
        for (Component component : components) {
            JCheckBox checkBox = (JCheckBox) component;
            checkBox.setSelected(false);
            checkBox.setEnabled(true);
        }
    }

    public Component getComponentByName(String towerName, JComponent panel) {
        HashMap componentMap = new HashMap<String,Component>();
        Component[] components = panel.getComponents();
        for (int i=0; i < components.length; i++) {
            componentMap.put(components[i].getName(), components[i]);
        }
        if (componentMap.containsKey(towerName)) {
            return (Component) componentMap.get(towerName);
        }
        else 
            return null;
    }

    private void addRowToTowerTable(String towerName, TreeMap<StandardType,Integer> flatPerTower) {
        addRowToTowerTable(towerName, flatPerTower, true);
    }
    
    private void addRowToTowerTable(String towerName, TreeMap<StandardType,Integer> flatPerTower, boolean addToGroupList) {
        addRowToTowerTable(towerName, flatPerTower, true, addToGroupList);
    }
    
    private void addRowToTowerTable(String towerName, TreeMap<StandardType,Integer> flatPerTower, boolean checkboxEnabled, boolean addToGroupList) {
        dtm.addRow(new Object[] { 
            towerName,
            flatPerTower.get(StandardType.ONE_BHK),
            flatPerTower.get(StandardType.TWO_BHK),
            flatPerTower.get(StandardType.TWO_N_HALF_BHK),
            flatPerTower.get(StandardType.THREE_BHK),
            flatPerTower.get(StandardType.THREE_N_HALF_BHK),
            flatPerTower.get(StandardType.FOUR_BHK),
            flatPerTower.get(StandardType.FOUR_AND_HALF_BHK),
            flatPerTower.get(StandardType.STUDIO),
            "<html><div style='text-align:center; color:red'><b> X </b></div></html>"
        });
        
        towerNameList.add(towerName);
        if(addToGroupList) {
            addCheckBoxForTower(towerName, checkboxEnabled);
        }
    }

    private void initTowerDataOverview() {
        dtmTowerDataOverview.addColumn("<html><b>Name</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>1 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>2 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>2.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>3 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>3.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>4 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>4.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>Studio</b></html>");
    }

    private void initOfficeDataOverview() {
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Sr.</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Floor Nr</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Type of Area</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Area</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>No of floors</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Sq. Mtr. Per Person </b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Occupancy</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>No. of Shifts</b></div></html>");
        dtmOfficeOverview.addColumn("<html><div style='height:25px'><b>Total Occupancy </b></div></html>");
    }

    private void initPanelSelector() {
        peopertTypeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String type = (String) cb.getSelectedItem();
                if(type.equals(TYPE_RESIDENTIAL)) {
                    officeShowroomPanel.setVisible(false);
                    residentialPanel.setVisible(true);
                } else {
                    residentialPanel.setVisible(false);
                    officeShowroomPanel.setVisible(true);
                }
            }
        });
    }

    private void initButtons() {
        try {
            URL imageUrl = DataCalculator.class.getResource("/images/saveBtnIcon2.png");
            ImageIcon icon = new ImageIcon(imageUrl);
            saveProjectBtn.setIcon(icon);
            saveProjectBtn.setText("");
        } catch (Exception e) {
            log.error("save button icon failed "+e.getMessage());
        }
    }

    @Override
    public void onSave(TowerData towerData, int index) {

        towersList.remove(index);
        towersList.add(index, towerData);

        towerDataTable.removeAll();
        while (dtm.getRowCount() > 0) {
            dtm.removeRow(dtm.getRowCount() - 1);
        }
        
        for (TowerData data : towersList) {
            addRowToTowerTable(data.getName(), data.getFlatsData(), false);
        }
    }

    @Override
    public void onSaveOfficeData(OfficeData officeData, int index) {
        officesList.remove(index);
        officesList.add(index, officeData);

        officeDataTable.removeAll();
        while (dtmOffice.getRowCount() > 0) {
            dtmOffice.removeRow(dtmOffice.getRowCount() - 1);
        }
        
        for (OfficeData data : officesList) {
            addOfficeDataRow(data);
        }
    }

    public int getSqMtrPerPerson(String floorNr, StandardType type) {
        if(type.equals(StandardType.OFFICE)){
            return (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_OFFICE).getValue();
        } else {
            if(floorNr.toLowerCase().contains("ground")){
                return (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND).getValue();
            } else {
                try {
                    int flrNr = Integer.parseInt(floorNr);
                    if(flrNr > 0) {
                        return (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND).getValue();
                    } else {
                        return (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_AT_GROUND).getValue();
                    }
                } catch(NumberFormatException ex) {
                    log.error("error per sqr calculation : ",ex);
                }
                return (int) standardValMap.get(StandardType.SQ_MTR_PER_PERSON_ABOVE_GROUND).getValue();
            }
        }
    }

    private boolean validateAndSetInputs() {
        StringBuilder errorMsg = new StringBuilder("Please fix input :\n");
        //ugr
        float ugtDomesticWaterTankPercentVal;
        float ugtRawWaterTankPercentVal;
        float ugtFireFightingTankVal;
        int ugtFlushingTankVal;
        //OHT
        float ohtDomesticTankPercentVal;
        float ohtFireFightingTankVal;
        float ohtFlushingTankVal;
        //STP
        float domesticSewerPercentVal;
        float flushSewerPercentVal;
        
        //ugr
        float ugtDomesticWaterTankPercentCommertialVal;
        float ugtRawWaterTankPercentCommertialVal;
        float ugtFireFightingTankCommertialVal;
        int ugtFlushingTankCommertialVal;
        //OHT
        float ohtDomesticTankPercentCommertialVal;
        float ohtFireFightingTankCommertialVal;
        float ohtFlushingTankCommertialVal;
        //STP
        float domesticSewerPercentCommertialVal;
        float flushSewerPercentCommertialVal;
        
        //Fire fighting
        float heightOfTallestBldVal;
        float basementAreaVal;
        float residualHeadPlumbingVal;
        float residualHeadFireVal;
        float frictionLossInputVal;
        //plot area
        float terraceAreaVal;
        float greenAreaVal;
        float pavedAreaVal;
        float totalPlotAreaVal;
        float extraAreaCateredVal;
        
        try {
            //UGTCapacity
            ugtDomesticWaterTankPercentVal = getFloatValue(ugtDomesticWaterTankPercent.getText(), errorMsg, "UGR : Domestic Water Tank %");
            ugtRawWaterTankPercentVal = getFloatValue(ugtRawWaterTankPercent.getText(), errorMsg, "UGR :Raw water tank %");
            ugtFireFightingTankVal = getFloatValue(ugtFireFightingTank.getText(), errorMsg, "UGT : Fire fighing tank %");
            ugtFlushingTankVal = (int)getFloatValue(ugtFlushingTank.getText(), errorMsg, "UGT : Flusing tank val");
            //OHT
            ohtDomesticTankPercentVal = getFloatValue(ohtDomesticTankPercent.getText(), errorMsg, "OHT : Domestic tank %");
            ohtFireFightingTankVal = getFloatValue(ohtFireFightingTank.getText(), errorMsg, "OHT : Fire Fighting tank ");
            ohtFlushingTankVal = getFloatValue(ohtFlushingTank.getText(), errorMsg, "OHT : Flushing tank ");
            //STP
            domesticSewerPercentVal = getFloatValue(domesticSewerPercent.getText(), errorMsg, "STP : Domestic sewer ");
            flushSewerPercentVal = getFloatValue(flushingSewerPercent.getText(), errorMsg, "STP : Flushing sewer ");

            //UGTCapacity commertial
            ugtDomesticWaterTankPercentCommertialVal = getFloatValue(ugtRawWaterTankPercentCommertial.getText(), errorMsg, "UGR Commertial: Domestic Water Tank %");
            ugtRawWaterTankPercentCommertialVal = getFloatValue(ugtRawWaterTankPercent.getText(), errorMsg, "UGR Commertial:Raw water tank %");
            ugtFireFightingTankCommertialVal = getFloatValue(ugtFireFightingTankCommertial.getText(), errorMsg, "UGT Commertial: Fire fighing tank %");
            ugtFlushingTankCommertialVal = (int)getFloatValue(ugtFlushingTankCommertial.getText(), errorMsg, "UGT Commertial: Flusing tank val");
            //OHT
            ohtDomesticTankPercentCommertialVal = getFloatValue(ohtDomesticTankPercentCommertial.getText(), errorMsg, "OHT Commertial: Domestic tank %");
            ohtFireFightingTankCommertialVal = getFloatValue(ohtFireFightingTankCommertial.getText(), errorMsg, "OHT Commertial: Fire Fighting tank ");
            ohtFlushingTankCommertialVal = getFloatValue(ohtFlushingTankCommertial.getText(), errorMsg, "OHT Commertial: Flushing tank ");
            //STP
            domesticSewerPercentCommertialVal = getFloatValue(domesticSewerPercentCommertial.getText(), errorMsg, "STP Commertial: Domestic sewer ");
            flushSewerPercentCommertialVal = getFloatValue(flushingSewerPercentCommertial.getText(), errorMsg, "STP Commertial: Flushing sewer ");

            //Fire fighting
            heightOfTallestBldVal = getFloatValue(heightOfTallestBld.getText(), errorMsg, "Height of tallest building");
            basementAreaVal = getFloatValue(basementArea.getText(), errorMsg, "Basement Area");
            residualHeadPlumbingVal = getFloatValue(residualHeadPlumbing.getText(), errorMsg, "Residual head plumbing");
            residualHeadFireVal = getFloatValue(residualHeadFire.getText(), errorMsg, "Residual head fire");
            frictionLossInputVal = getFloatValue(frictionLossInput.getText(), errorMsg, "Friction loss");

            //plot area
            terraceAreaVal = getFloatValue(terraceArea.getText(), errorMsg, "Terrace Area");
            greenAreaVal = getFloatValue(greenArea.getText(), errorMsg, "Green Area");
            pavedAreaVal = getFloatValue(pavedArea.getText(), errorMsg, "Paved Area");
            totalPlotAreaVal = getFloatValue(totalPlotArea.getText(), errorMsg, "Total Plot Area");
            extraAreaCateredVal = getFloatValue(extraAreaCatered.getText(), errorMsg, "Extra Area Catered");
            
            UGTCapacity ugtCapacityResidential = new UGTCapacity(ugtDomesticWaterTankPercentVal, ugtRawWaterTankPercentVal, ugtFireFightingTankVal, ugtFlushingTankVal);
            OHTCapacity ohtCapacityResidential = new OHTCapacity(ohtDomesticTankPercentVal, ohtFireFightingTankVal, ohtFlushingTankVal);
            STPCapacity stpCapacityResidential = new STPCapacity(domesticSewerPercentVal, flushSewerPercentVal);
            
            UGTCapacity ugtCapacityCommertial = new UGTCapacity(ugtDomesticWaterTankPercentCommertialVal, ugtRawWaterTankPercentCommertialVal, ugtFireFightingTankCommertialVal, ugtFlushingTankCommertialVal);
            OHTCapacity ohtCapacityCommertial = new OHTCapacity(ohtDomesticTankPercentCommertialVal, ohtFireFightingTankCommertialVal, ohtFlushingTankCommertialVal);
            STPCapacity stpCapacityCommertial = new STPCapacity(domesticSewerPercentCommertialVal, flushSewerPercentCommertialVal);
            
            capacityDetailsResidential = new CapacityDetails(ugtCapacityResidential, ohtCapacityResidential, stpCapacityResidential);
            capacityDetailsCommertial = new CapacityDetails(ugtCapacityCommertial, ohtCapacityCommertial, stpCapacityCommertial);

            fireFightingDetails = new FireFightingDetails(heightOfTallestBldVal, basementAreaVal, residualHeadPlumbingVal, residualHeadFireVal, frictionLossInputVal);
            plotArea = new PlotArea(terraceAreaVal, greenAreaVal, pavedAreaVal, totalPlotAreaVal, extraAreaCateredVal);
            
            return true;
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private float getFloatValue(String input, StringBuilder sb, String errorMsg) {
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException ex) {
            sb.append(errorMsg).append("\n");
            throw new NumberFormatException(input);
        }
    }

    private void setOtherInputs(PdfData projectData) {
        ugtDomesticWaterTankPercent.setText(String.valueOf(projectData.getCapacityDetailsResidential().getuGTCapacity().getDomesticTank()));
        ugtFlushingTank.setText(String.valueOf(projectData.getCapacityDetailsResidential().getuGTCapacity().getFlushingTank()));
        ugtRawWaterTankPercent.setText(String.valueOf(projectData.getCapacityDetailsResidential().getuGTCapacity().getRawWaterTank()));
        ugtFireFightingTank.setText(String.valueOf(projectData.getCapacityDetailsResidential().getuGTCapacity().getFireFightingTank()));
        
        ohtDomesticTankPercent.setText(String.valueOf(projectData.getCapacityDetailsResidential().getoHTCapacity().getDomesticTank()));
        ohtFlushingTank.setText(String.valueOf(projectData.getCapacityDetailsResidential().getoHTCapacity().getFlushingTank()));
        ohtFireFightingTank.setText(String.valueOf(projectData.getCapacityDetailsResidential().getoHTCapacity().getFireFightingTank()));
        
        domesticSewerPercent.setText(String.valueOf(projectData.getCapacityDetailsResidential().getsTPCapacity().getDomesticFlow()));
        flushingSewerPercent.setText(String.valueOf(projectData.getCapacityDetailsResidential().getsTPCapacity().getFlushingFlow()));
        //commertial
        ugtDomesticWaterTankPercentCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getuGTCapacity().getDomesticTank()));
        ugtFlushingTankCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getuGTCapacity().getFlushingTank()));
        ugtRawWaterTankPercentCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getuGTCapacity().getRawWaterTank()));
        ugtFireFightingTankCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getuGTCapacity().getFireFightingTank()));
        
        ohtDomesticTankPercentCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getoHTCapacity().getDomesticTank()));
        ohtFlushingTankCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getoHTCapacity().getFlushingTank()));
        ohtFireFightingTankCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getoHTCapacity().getFireFightingTank()));
        
        domesticSewerPercentCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getsTPCapacity().getDomesticFlow()));
        flushingSewerPercentCommertial.setText(String.valueOf(projectData.getCapacityDetailsCommertial().getsTPCapacity().getFlushingFlow()));
    
        //fireFighting
        heightOfTallestBld.setText(String.valueOf(projectData.getFireFightingDetails().getHeightOfTallestBuilding()));
        residualHeadPlumbing.setText(String.valueOf(projectData.getFireFightingDetails().getResidualHeadPlumbing()));
        residualHeadFire.setText(String.valueOf(projectData.getFireFightingDetails().getResidualHeadFire()));
        frictionLossInput.setText(String.valueOf(projectData.getFireFightingDetails().getFrictionLoss()));
        basementArea.setText(String.valueOf(projectData.getFireFightingDetails().getBasementArea()));
        
        //plot area
        terraceArea.setText(String.valueOf(projectData.getPlotArea().getTerrace()));
        greenArea.setText(String.valueOf(projectData.getPlotArea().getGreenArea()));
        pavedArea.setText(String.valueOf(projectData.getPlotArea().getPavedArea()));
        totalPlotArea.setText(String.valueOf(projectData.getPlotArea().getTotalAreaPlot()));
        extraAreaCatered.setText(String.valueOf(projectData.getPlotArea().getExtraAreaCatered()));
    }

}
