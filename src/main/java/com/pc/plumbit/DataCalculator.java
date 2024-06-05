/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pc.plumbit;

import com.pc.plumbit.enums.StandardType;
import com.pc.exceptions.InvalidInputException;
import com.pc.initializer.DataInitializer;
import com.pc.plumbit.generator.PDFGenerator;
import com.pc.plumbit.model.PdfData;
import com.pc.plumbit.model.StandardValues;
import com.pc.plumbit.model.TowerData;
import com.pc.utils.DataFormater;
import com.pc.utils.StyleFormatter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prashant
 */
public class DataCalculator extends javax.swing.JFrame {

    private static final Logger log = LoggerFactory.getLogger(DataCalculator.class);
    private List<TowerData> towersList = new ArrayList<>();
    private List<TreeMap<StandardType,Integer>> officesList = new ArrayList<>();
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
    /**
     * Creates new form DataCalculator
     */
    public DataCalculator() {
        initApp();
    }
    
    private void initApp(){
        initComponents();
        initConfigs();
        initTowerTable();
        initOfficeTable();
        officePanel.setVisible(false);
        residentialPanel.setVisible(true);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        
        dtmOfficeOverview.addColumn("<html><b>Offices</b></html>");
        dtmOfficeOverview.addColumn("<html><b>Showroom</b></html>");
        officeDataOverviewTable.setModel(dtmOfficeOverview);
        
        DataInitializer.setTableStyling(towerDataOveriewTable);
        dtmTowerDataOverview.addColumn("<html><b>Name</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>1 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>2 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>2.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>3 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>3.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>4 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>4.5 BHK</b></html>");
        dtmTowerDataOverview.addColumn("<html><b>Studio</b></html>");
        towerDataOveriewTable.setModel(dtmTowerDataOverview);
        
        peopertTypeSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String type = (String) cb.getSelectedItem();
                System.out.println("type : " + type);
                if(type.equals(TYPE_RESIDENTIAL)) {
                    officePanel.setVisible(false);
                    residentialPanel.setVisible(true);
                } else {
                    residentialPanel.setVisible(false);
                    officePanel.setVisible(true);
                }
            }
        });
        try {
            URL imageUrl = DataCalculator.class.getResource("/images/saveBtnIcon2.png");
            ImageIcon icon = new ImageIcon(imageUrl);
            saveProjectBtn.setIcon(icon);
            saveProjectBtn.setText("");
        } catch (Exception e) {
            log.error("save button icon failed "+e.getMessage());
        }
        towerGroupPanel.setLayout(new GridLayout(20,1));
        DataInitializer.setTableStyling(towerGroupTable);
        
        StyleFormatter.setTableColumnAlignment(towerDataTable, JLabel.CENTER, true);
        StyleFormatter.setTableColumnAlignment(officeDataTable, JLabel.CENTER, true);
        StyleFormatter.setTableColumnAlignment(towerGroupTable, JLabel.CENTER, true);
    }
    
    public DataCalculator(PdfData projectData) {
        initApp();
        setStandardValues(projectData.getStandardValMap());
        
        this.towersList = projectData.getTowersList();
        for(TowerData towerData : towersList) {
            addRowToTowerTable(towerData.getName(), towerData.getFlatsData() ,false);
            addTowerDataOverviewRow(towerData.getName(), towerData.getFlatsData());
        }

        this.officesList = projectData.getOfficesList();
        for(TreeMap<StandardType,Integer> officeMap : officesList){
            addOfficeDataRow(officeMap);
        }
        
        this.outsideAreaMap = projectData.getOutsideAreaMap();

        landscapeAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.LANDSCAPE)));
        clubHouseAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.CLUB_HOUSE)));
        swimmingAreaInput.setText(String.valueOf(outsideAreaMap.get(StandardType.SWIMMING_POOL)));

        List<List<String>> groupedTowerNames = projectData.getGroupedTowerNamesList();
        for(List<String> groupedTowers : groupedTowerNames) {
            addGroupedTowerRow(groupedTowers);
        }
                
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
        dtmOffice.addColumn("<html><b>Office</b></html>");
        dtmOffice.addColumn("<html><b>Showroom</b></html>");
        dtmOffice.addColumn("<html><b>  </b></html>");
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
            System.out.println("continueWithoutStandardVals : "+continueWithoutStandardVals);
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

        stdwaterDemand.setText(DataFormater.getStandardValue(standardValues.get(StandardType.WATER_DEMAND)));
        stdSolidWasteKgPPD.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_KG_PERSON_DAY)));
        stdSolidWasteLtrPerFamily.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLID_WASTE_LITER_20_FAMILY)));
        stdGasBank.setText(DataFormater.getStandardValue(standardValues.get(StandardType.GAS_BANK)));
        stdSolar.setText(DataFormater.getStandardValue(standardValues.get(StandardType.SOLAR)));

        stdTerrace.setText(DataFormater.getStandardValue(standardValues.get(StandardType.TERRACE)));
        stdGreenArea.setText(DataFormater.getStandardValue(standardValues.get(StandardType.GREEN_AREA)));
        stdPavedArea.setText(DataFormater.getStandardValue(standardValues.get(StandardType.PAVED_AREA)));
        stdTotalPlotArea.setText(DataFormater.getStandardValue(standardValues.get(StandardType.TOTAL_PLOT_AREA)));
        stdExtraAreaCatered.setText(DataFormater.getStandardValue(standardValues.get(StandardType.EXTRA_AREA_TO_BE_CATERED)));

        stdHeightOfTallestBld.setText(DataFormater.getStandardValue(standardValues.get(StandardType.HEIGHT_OF_TALLEST_BUILDING_FROM_GR)));
        stdBasementArea.setText(DataFormater.getStandardValue(standardValues.get(StandardType.BASEMENT_AREA)));
        stdResidualHeadPlumbing.setText(DataFormater.getStandardValue(standardValues.get(StandardType.RESIDUAL_HEAD_PLUMBING)));
        stdResidualHeadFire.setText(DataFormater.getStandardValue(standardValues.get(StandardType.RESIDUAL_HEAD_FIRE)));
        stdFrictionLossInput.setText(DataFormater.getStandardValue(standardValues.get(StandardType.FRICTION_LOSS)));
        
        domesticSewerPercent.setText(DataFormater.getStandardValue(standardValues.get(StandardType.FLOW_TO_SEWER_DOM)));
        flushSewerPercent.setText(DataFormater.getStandardValue(standardValues.get(StandardType.FLOW_TO_SEWER_FLS)));
        rawWaterTankPercent.setText(DataFormater.getStandardValue(standardValues.get(StandardType.RAW_WATER_TANK)));
        domesticWaterTankPercent.setText(DataFormater.getStandardValue(standardValues.get(StandardType.DOMESTIC_TANK)));
        stpFlushingTank.setText(DataFormater.getStandardValue(standardValues.get(StandardType.STP_FLUSHING_TANK)));
        
        ohtFireFightingTank.setText(DataFormater.getStandardValue(standardValues.get(StandardType.OHT_FIRE_FIGHTING_TANK)));
        ugrFireFightingTank.setText(DataFormater.getStandardValue(standardValues.get(StandardType.UGR_FIRE_FIGHTING_TANK)));
        
        landscapeAreaWater.setSelectedItem(DataFormater.getStandardValue(standardValues.get(StandardType.LANDSCAPE_AREA_WATER_PERCENTAGE)));
        swimmingPoolCapacity.setSelectedItem(DataFormater.getStandardValue(standardValues.get(StandardType.SWIMMING_POOL_AREA_PERCENT)));

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
        
        stdwaterDemand.setEditable(allowEdit);
        stdSolidWasteKgPPD.setEditable(allowEdit);
        stdSolidWasteLtrPerFamily.setEditable(allowEdit);
        stdGasBank.setEditable(allowEdit);
        stdSolar.setEditable(allowEdit);
        
        stdTerrace.setEditable(allowEdit);
        stdGreenArea.setEditable(allowEdit);
        stdPavedArea.setEditable(allowEdit);
        stdTotalPlotArea.setEditable(allowEdit);
        stdExtraAreaCatered.setEditable(allowEdit);
        
        stdHeightOfTallestBld.setEditable(allowEdit);
        stdBasementArea.setEditable(allowEdit);
        stdResidualHeadPlumbing.setEditable(allowEdit);
        stdResidualHeadFire.setEditable(allowEdit);
        stdFrictionLossInput.setEditable(allowEdit);
        
        domesticSewerPercent.setEditable(allowEdit);
        flushSewerPercent.setEditable(allowEdit);
        rawWaterTankPercent.setEditable(allowEdit);
        domesticWaterTankPercent.setEditable(allowEdit);
        stpFlushingTank.setEditable(allowEdit);
        
        ugrFireFightingTank.setEditable(allowEdit);
        ohtFireFightingTank.setEditable(allowEdit);
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
        consumptionParamPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        stdwaterDemand = new javax.swing.JTextField();
        stdSolidWasteKgPPD = new javax.swing.JTextField();
        stdSolar = new javax.swing.JTextField();
        stdGasBank = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        stdSolidWasteLtrPerFamily = new javax.swing.JTextField();
        inputCriteriaTabNextBtn = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        stdTerrace = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        stdGreenArea = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        stdPavedArea = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        stdTotalPlotArea = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        stdExtraAreaCatered = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        stdHeightOfTallestBld = new javax.swing.JTextField();
        stdResidualHeadPlumbing = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        stdBasementArea = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        stdFrictionLossInput = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        stdResidualHeadFire = new javax.swing.JTextField();
        jLableStdFrictionLoss = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        domesticSewerPercent = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        flushSewerPercent = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        rawWaterTankPercent = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        domesticWaterTankPercent = new javax.swing.JTextField();
        jLabel76 = new javax.swing.JLabel();
        stpFlushingTank = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        ugrFireFightingTank = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        ohtFireFightingTank = new javax.swing.JTextField();
        inputDataPanel = new javax.swing.JPanel();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        towerDataTable = new javax.swing.JTable();
        typePanel = new javax.swing.JPanel();
        officePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nrOffice = new javax.swing.JTextField();
        addOfficeDataBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        nrShowroom = new javax.swing.JTextField();
        residentialPanel = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        nrFlats1BHKInput = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        nrFlats2BHKInput = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        nrStudioInput = new javax.swing.JTextField();
        nrFlats4AndHalfBHKInput = new javax.swing.JTextField();
        nrFlats4BHKInput = new javax.swing.JTextField();
        nrFlats3AndHalfBHKInput = new javax.swing.JTextField();
        nrFlats3BHKInput = new javax.swing.JTextField();
        nrFlats2NHalfBHKInput = new javax.swing.JTextField();
        addTowerBtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        towerNameInput = new javax.swing.JTextField();
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
        setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        setResizable(false);

        peoplePerTypeInputPanel.setMaximumSize(new java.awt.Dimension(20, 40));
        peoplePerTypeInputPanel.setMinimumSize(new java.awt.Dimension(20, 40));
        peoplePerTypeInputPanel.setPreferredSize(new java.awt.Dimension(20, 40));

        jLabel15.setBackground(new java.awt.Color(204, 255, 255));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 255));
        jLabel15.setText("Population Criteria");

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

        javax.swing.GroupLayout peoplePerTypeInputPanelLayout = new javax.swing.GroupLayout(peoplePerTypeInputPanel);
        peoplePerTypeInputPanel.setLayout(peoplePerTypeInputPanelLayout);
        peoplePerTypeInputPanelLayout.setHorizontalGroup(
            peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel15))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peopleServantInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peopleDriverInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePerLandscapeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePerSwimmingPoolInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePerClubHouseInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(peoplePer4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePer4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePerStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(peoplePODsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        peoplePerTypeInputPanelLayout.setVerticalGroup(
            peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peoplePerTypeInputPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel15)
                .addGap(8, 8, 8)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePer4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePODsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peopleServantInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peopleDriverInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerLandscapeInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerSwimmingPoolInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(peoplePerTypeInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(peoplePerClubHouseInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(185, 185, 185))
        );

        jLabel3.setText("WATER DEMAND");

        jLabel4.setText("<html>SOLID WASTE (Liter/20 families)</html>");

        jLabel48.setText("GAS BANK");

        jLabel49.setText("SOLAR ");

        jLabel50.setBackground(new java.awt.Color(204, 255, 255));
        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(0, 0, 255));
        jLabel50.setText("CONSUMPTION PARAMETERS");

        jLabel64.setText("SOLID WASTE(kg/p/d)");

        javax.swing.GroupLayout consumptionParamPanelLayout = new javax.swing.GroupLayout(consumptionParamPanel);
        consumptionParamPanel.setLayout(consumptionParamPanelLayout);
        consumptionParamPanelLayout.setHorizontalGroup(
            consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel50))
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(stdwaterDemand, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(stdSolidWasteKgPPD, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(stdSolidWasteLtrPerFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stdGasBank, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(stdSolar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        consumptionParamPanelLayout.setVerticalGroup(
            consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel50)
                .addGap(8, 8, 8)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(stdwaterDemand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(consumptionParamPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(stdSolidWasteKgPPD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consumptionParamPanelLayout.createSequentialGroup()
                        .addComponent(stdSolidWasteLtrPerFamily, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)))
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stdGasBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(consumptionParamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stdSolar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        inputCriteriaTabNextBtn.setText("Next");
        inputCriteriaTabNextBtn.setAlignmentX(0.5F);
        inputCriteriaTabNextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputCriteriaTabNextBtnActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Modify Standard Values");
        jCheckBox1.setActionCommand("");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel51.setBackground(new java.awt.Color(204, 255, 255));
        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 0, 255));
        jLabel51.setText("PLOT AREA( SQ.MTRS )");

        jLabel52.setText("TERRACE");

        jLabel54.setText("GREEN AREA");

        jLabel55.setText("PAVED AREA");

        jLabel56.setText("TOTAL PLOT AREA");

        jLabel57.setText("<html>EXTRA AREA TO BE CATERED</html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(stdExtraAreaCatered, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stdTerrace, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stdGreenArea, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stdPavedArea, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stdTotalPlotArea, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel51)
                .addGap(45, 45, 45))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(stdTerrace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(stdGreenArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(stdPavedArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(stdTotalPlotArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stdExtraAreaCatered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel59.setText("<html>HEIGHT OF TALLEST BUILDING FROM GR</html>");
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 170, 40));
        jPanel3.add(stdHeightOfTallestBld, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, 87, 33));
        jPanel3.add(stdResidualHeadPlumbing, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 87, -1));

        jLabel60.setText("<html>BASEMENT AREA</html>");
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 170, 33));
        jPanel3.add(stdBasementArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, 87, -1));

        jLabel61.setText("<html>RESIDUAL HEAD (PLUMBING)</html>");
        jPanel3.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 170, 30));
        jPanel3.add(stdFrictionLossInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 160, 87, -1));

        jLabel63.setText("<html>RESIDUAL HEAD (FIRE)</html>");
        jPanel3.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 170, 22));
        jPanel3.add(stdResidualHeadFire, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 87, -1));

        jLableStdFrictionLoss.setText("FRICTION LOSS");
        jPanel3.add(jLableStdFrictionLoss, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 170, 22));

        jLabel58.setBackground(new java.awt.Color(204, 255, 255));
        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(0, 0, 255));
        jLabel58.setText("DESIGN CRITERIA FOR FIRE FIGHTING");
        jPanel3.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 6, -1, -1));

        jLabel71.setBackground(new java.awt.Color(204, 255, 255));
        jLabel71.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(0, 0, 255));
        jLabel71.setText("Others");

        jLabel72.setText("Domestic Sewer %");

        jLabel73.setText("Flush Sewer %");

        jLabel74.setText("RAW WATER TANK %");

        jLabel75.setText("OHT DOMESTIC TANK %");

        jLabel76.setText("FLUSHING TANK (days)");

        jLabel79.setText("UGR FIRE FIGHTING TANK");

        jLabel80.setText("OHT FIRE FIGHTING TANK");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ohtFireFightingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                                    .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(domesticSewerPercent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(flushSewerPercent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rawWaterTankPercent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(domesticWaterTankPercent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(stpFlushingTank, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel79, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ugrFireFightingTank, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(19, 19, 19))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(domesticSewerPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(flushSewerPercent)
                    .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rawWaterTankPercent)
                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(domesticWaterTankPercent)
                    .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stpFlushingTank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ugrFireFightingTank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ohtFireFightingTank))
                .addContainerGap())
        );

        javax.swing.GroupLayout inputCriteriaFormLayout = new javax.swing.GroupLayout(inputCriteriaForm);
        inputCriteriaForm.setLayout(inputCriteriaFormLayout);
        inputCriteriaFormLayout.setHorizontalGroup(
            inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addComponent(peoplePerTypeInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(consumptionParamPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addGap(369, 369, 369)
                        .addComponent(inputCriteriaTabNextBtn)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        inputCriteriaFormLayout.setVerticalGroup(
            inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(peoplePerTypeInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inputCriteriaFormLayout.createSequentialGroup()
                        .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(consumptionParamPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inputCriteriaFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addComponent(inputCriteriaTabNextBtn)
                .addGap(10, 10, 10))
        );

        tabbedPane.addTab("Input Criteria ", inputCriteriaForm);

        peopertTypeSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Residential", "Commertial" }));
        peopertTypeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peopertTypeSelectorActionPerformed(evt);
            }
        });

        jLabel7.setText("Landscape aread");

        jLabel8.setText("Swimming Pool Area");

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

        jLabel53.setText("<html>lit per  sq.m of green area</html>");

        jLabel70.setText("<html>% of Pool Capacity </html>");

        javax.swing.GroupLayout outsideAreaPanelLayout = new javax.swing.GroupLayout(outsideAreaPanel);
        outsideAreaPanel.setLayout(outsideAreaPanelLayout);
        outsideAreaPanelLayout.setHorizontalGroup(
            outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outsideAreaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(swimmingAreaInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(landscapeAreaInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clubHouseAreaInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(landscapeAreaWater, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swimmingPoolCapacity, 0, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                    .addComponent(jLabel70)))
            .addGroup(outsideAreaPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(outsideAreaBtn))
        );
        outsideAreaPanelLayout.setVerticalGroup(
            outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outsideAreaPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(landscapeAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(landscapeAreaWater, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(swimmingAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swimmingPoolCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outsideAreaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(clubHouseAreaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(outsideAreaBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel1.setText("Office");

        nrOffice.setPreferredSize(new java.awt.Dimension(80, 20));

        addOfficeDataBtn.setText("Add Office");
        addOfficeDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOfficeDataBtnActionPerformed(evt);
            }
        });

        jLabel2.setText("Showroom");
        jLabel2.setToolTipText("");

        javax.swing.GroupLayout officePanelLayout = new javax.swing.GroupLayout(officePanel);
        officePanel.setLayout(officePanelLayout);
        officePanelLayout.setHorizontalGroup(
            officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(officePanelLayout.createSequentialGroup()
                .addGroup(officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(officePanelLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(addOfficeDataBtn))
                    .addGroup(officePanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                        .addGap(31, 31, 31)
                        .addGroup(officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nrOffice, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(nrShowroom, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        officePanelLayout.setVerticalGroup(
            officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(officePanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nrOffice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(officePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nrShowroom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                .addComponent(addOfficeDataBtn)
                .addContainerGap())
        );

        officePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, nrOffice});

        typePanel.add(officePanel, "card3");

        residentialPanel.setForeground(new java.awt.Color(255, 255, 0));

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("1 BHK");
        jLabel40.setAlignmentX(0.5F);
        jLabel40.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel40.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel40.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats1BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats1BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats1BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("2 BHK");
        jLabel41.setAlignmentX(0.5F);
        jLabel41.setMaximumSize(new java.awt.Dimension(20, 40));
        jLabel41.setMinimumSize(new java.awt.Dimension(20, 40));
        jLabel41.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats2BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

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

        nrStudioInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrStudioInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrStudioInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats4AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats4BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3AndHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats3AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3BHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats3BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2NHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2NHalfBHKInput.setMinimumSize(new java.awt.Dimension(20, 40));
        nrFlats2NHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        addTowerBtn.setText("Add Tower");
        addTowerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTowerBtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Type");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("# of Flats");

        jLabel62.setText("Tower Name");

        javax.swing.GroupLayout residentialPanelLayout = new javax.swing.GroupLayout(residentialPanel);
        residentialPanel.setLayout(residentialPanelLayout);
        residentialPanelLayout.setHorizontalGroup(
            residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(addTowerBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(residentialPanelLayout.createSequentialGroup()
                                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nrStudioInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(residentialPanelLayout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(residentialPanelLayout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(towerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        residentialPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel40, jLabel62});

        residentialPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nrFlats1BHKInput, towerNameInput});

        residentialPanelLayout.setVerticalGroup(
            residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentialPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(towerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(residentialPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(residentialPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTowerBtn)
                .addGap(14, 14, 14))
        );

        residentialPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel40, jLabel62});

        residentialPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {nrFlats1BHKInput, towerNameInput});

        typePanel.add(residentialPanel, "card2");

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
            .addGap(0, 183, Short.MAX_VALUE)
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
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(groupTowers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearGroupedTowerBtn))
                    .addComponent(towerGroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(towerGroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(groupTowers)
                    .addComponent(clearGroupedTowerBtn))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(outsideAreaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(peopertTypeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(typePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                                    .addComponent(jScrollPane4))
                                .addGap(12, 12, 12))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(369, 369, 369)
                        .addComponent(inputDataNextBtn)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(peopertTypeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(typePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outsideAreaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputDataNextBtn)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout inputDataPanelLayout = new javax.swing.GroupLayout(inputDataPanel);
        inputDataPanel.setLayout(inputDataPanelLayout);
        inputDataPanelLayout.setHorizontalGroup(
            inputDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inputDataPanelLayout.setVerticalGroup(
            inputDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabbedPane.addTab("Input form", inputDataPanel);

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
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
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
                .addGap(19, 19, 19)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(projectNameInput)
                    .addComponent(pdfLocationInput, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(pdfLocationInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(waterDemandPDFBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(322, 322, 322)
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
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65)
                            .addComponent(waterDemandOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outdoorOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(overviewPanelLayout.createSequentialGroup()
                                .addComponent(jLabel68)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Overview", overviewPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                JOptionPane.showMessageDialog(this, "Empty pdf location", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String pdfFolderLocation = pdfLocationInput.getText();
                PdfData.PdfDataBuilder builder = getPdfDataBuilder();
                builder.setPdfLocation(pdfFolderLocation);
                PdfData pdfData = builder.build();
                String pdfFileLocation = pdfData.getPdfLocation()+ "/" + pdfData.getProjectName()+".pdf";
                File outputFile = new File(pdfFileLocation);
                if(!outputFile.exists()) {
                    PDFGenerator.generateWaterDemandPDF(pdfData, this);
                } else {
                    JOptionPane.showMessageDialog(this, "PDF with same project name exist", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (FileNotFoundException ex) {
            log.error("Error file reading", ex);
        }
    }//GEN-LAST:event_waterDemandPDFBtnActionPerformed

    private PdfData.PdfDataBuilder getPdfDataBuilder() {
        PdfData.PdfDataBuilder builder = new PdfData.PdfDataBuilder(standardValMap, towersList);
        builder.setOfficesList(officesList)
                .setOutsideAreaMap(outsideAreaMap)
                .setGroupedTowerNamesList(groupedTowerNamesList)
                .setProjectName(projectNameInput.getText());
        return builder;
    }

    private void outsideAreaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outsideAreaBtnActionPerformed
        boolean valid = true;
        TreeMap<StandardType,Integer> outsideArea = new TreeMap<>();
        try {
            Integer landscapeArea = getIntegerVal(landscapeAreaInput.getText());
            outsideArea.put(StandardType.LANDSCAPE, landscapeArea);
        } catch (InvalidInputException ex) {
            valid = false;
        }
        try {
            Integer swimmingPoolArea = getIntegerVal(swimmingAreaInput.getText());
            outsideArea.put(StandardType.SWIMMING_POOL, swimmingPoolArea);
        } catch (InvalidInputException ex) {
            valid = false;
        }
        try {
            Integer clubHouseArea = getIntegerVal(clubHouseAreaInput.getText());
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
        if (towersList.size() > 0 || officesList.size() > 0) {
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);
        } else {
            JOptionPane.showMessageDialog(this, "No Data provided?", "Error", JOptionPane.ERROR_MESSAGE);
        }
        createOverview();
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
        officesList.forEach(officeData -> {
            dtmOfficeOverview.addRow(new Object[] { 
                officeData.get(StandardType.OFFICE),
                officeData.get(StandardType.SHOWROOM),
            });
        });
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
            Integer oneBHKQty = getIntegerVal(nrOneBHKs);
            flatPerTower.put(StandardType.ONE_BHK, oneBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("1 BHK\n");
        }
        
        try {
            Integer twoBHKQty = getIntegerVal(nrTwoBHKs);
            flatPerTower.put(StandardType.TWO_BHK, twoBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("2 BHK\n");
        }
        
        try {
            Integer twoAndHalfBHKQty = getIntegerVal(nrTwoAndHalfBHKs);
            flatPerTower.put(StandardType.TWO_N_HALF_BHK, twoAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("2.5 BHK\n");
        }
        
        try {
            Integer threeBHKQty = getIntegerVal(nrThreeBHKs);
            flatPerTower.put(StandardType.THREE_BHK, threeBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("3 BHK\n");
        }
        try {
            Integer threeAndHalfBHKQty = getIntegerVal(nrThreeAndHalfBHKs);
            flatPerTower.put(StandardType.THREE_N_HALF_BHK, threeAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("3.5 BHK\n");
        }
        try {
            Integer fourBHKQty = getIntegerVal(nrFourBHKs);
            flatPerTower.put(StandardType.FOUR_BHK, fourBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("4 BHK\n");
        }
        try {
            Integer fourAndHalfBHKQty = getIntegerVal(nrFourAndHalfBHKs);
            flatPerTower.put(StandardType.FOUR_AND_HALF_BHK, fourAndHalfBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("4.5 BHK\n");
        }
        try {
            Integer studioQty = getIntegerVal(nrStudios);
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
        }
    }//GEN-LAST:event_towerDataTableMouseClicked

    private void addOfficeDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOfficeDataBtnActionPerformed
        String officeQty = nrOffice.getText();
        String showroomQty = nrShowroom.getText();

        boolean valid = true;
        StringBuilder errorMsg = new StringBuilder("Please correct input : ");
        TreeMap<StandardType,Integer> commertials = new TreeMap<>();
        try {
            Integer oneBHKQty = getIntegerVal(officeQty);
            commertials.put(StandardType.OFFICE, oneBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("Office\n");
        }
        
        try {
            Integer twoBHKQty = getIntegerVal(showroomQty);
            commertials.put(StandardType.SHOWROOM, twoBHKQty);
        } catch(InvalidInputException ex) {
            valid = false;
            errorMsg.append("Showroom\n");
        }
        
        if(valid) {
            officesList.add(commertials);
            addOfficeDataRow(commertials);
        } else {
            commertials.clear();
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addOfficeDataBtnActionPerformed

    private void addOfficeDataRow(TreeMap<StandardType, Integer> commertials) {
        dtmOffice.addRow(new Object[]{
            commertials.get(StandardType.OFFICE),
            commertials.get(StandardType.SHOWROOM),
            "<html><div style='text-align:center; color:red'><b> X </b></div></html>"
        });
    }

    private void officeDataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_officeDataTableMouseClicked
        int row = officeDataTable.rowAtPoint(evt.getPoint());
        int col = officeDataTable.columnAtPoint(evt.getPoint());
        if (row >= 0 && col == 2) {
            int dialogResult = JOptionPane.showConfirmDialog (this, "Are you sure, you want to delete Office Data?","Warning", JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                officesList.remove(row);
                dtmOffice.removeRow(row);
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

            standardValMap.put(StandardType.WATER_DEMAND, getUpdatedStandardVal(StandardType.WATER_DEMAND, stdwaterDemand.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_KG_PERSON_DAY, getUpdatedStandardVal(StandardType.SOLID_WASTE_KG_PERSON_DAY, stdSolidWasteKgPPD.getText()));
            standardValMap.put(StandardType.SOLID_WASTE_LITER_20_FAMILY, getUpdatedStandardVal(StandardType.SOLID_WASTE_LITER_20_FAMILY, stdSolidWasteLtrPerFamily.getText()));
            standardValMap.put(StandardType.GAS_BANK, getUpdatedStandardVal(StandardType.GAS_BANK, stdGasBank.getText()));
            standardValMap.put(StandardType.SOLAR, getUpdatedStandardVal(StandardType.SOLAR, stdSolar.getText()));

            standardValMap.put(StandardType.TERRACE, getUpdatedStandardVal(StandardType.TERRACE, stdTerrace.getText()));
            standardValMap.put(StandardType.GREEN_AREA, getUpdatedStandardVal(StandardType.GREEN_AREA, stdGreenArea.getText()));
            standardValMap.put(StandardType.PAVED_AREA, getUpdatedStandardVal(StandardType.PAVED_AREA, stdPavedArea.getText()));
            standardValMap.put(StandardType.TOTAL_PLOT_AREA, getUpdatedStandardVal(StandardType.TOTAL_PLOT_AREA, stdTotalPlotArea.getText()));
            standardValMap.put(StandardType.EXTRA_AREA_TO_BE_CATERED, getUpdatedStandardVal(StandardType.EXTRA_AREA_TO_BE_CATERED, stdExtraAreaCatered.getText()));

            standardValMap.put(StandardType.HEIGHT_OF_TALLEST_BUILDING_FROM_GR, getUpdatedStandardVal(StandardType.HEIGHT_OF_TALLEST_BUILDING_FROM_GR, stdHeightOfTallestBld.getText()));
            standardValMap.put(StandardType.BASEMENT_AREA, getUpdatedStandardVal(StandardType.BASEMENT_AREA, stdBasementArea.getText()));
            standardValMap.put(StandardType.RESIDUAL_HEAD_PLUMBING, getUpdatedStandardVal(StandardType.RESIDUAL_HEAD_PLUMBING, stdResidualHeadPlumbing.getText()));
            standardValMap.put(StandardType.RESIDUAL_HEAD_FIRE, getUpdatedStandardVal(StandardType.RESIDUAL_HEAD_FIRE, stdResidualHeadFire.getText()));
            standardValMap.put(StandardType.FRICTION_LOSS, getUpdatedStandardVal(StandardType.FRICTION_LOSS, stdFrictionLossInput.getText()));
            
            standardValMap.put(StandardType.FLOW_TO_SEWER_DOM, getUpdatedStandardVal(StandardType.FLOW_TO_SEWER_DOM, domesticSewerPercent.getText()));
            standardValMap.put(StandardType.FLOW_TO_SEWER_FLS, getUpdatedStandardVal(StandardType.FLOW_TO_SEWER_FLS, flushSewerPercent.getText()));
            standardValMap.put(StandardType.RAW_WATER_TANK, getUpdatedStandardVal(StandardType.RAW_WATER_TANK, rawWaterTankPercent.getText()));
            standardValMap.put(StandardType.DOMESTIC_TANK, getUpdatedStandardVal(StandardType.DOMESTIC_TANK, domesticWaterTankPercent.getText()));
            standardValMap.put(StandardType.STP_FLUSHING_TANK, getUpdatedStandardVal(StandardType.STP_FLUSHING_TANK, stpFlushingTank.getText()));
            
            standardValMap.keySet().forEach(key -> {
                StandardValues std = standardValMap.get(key);
                System.out.println(key + ":" +std.getValue());
            });
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        JCheckBox chkBox = (JCheckBox) evt.getSource();
        boolean allowEdit = chkBox.isSelected();
        if(allowEdit) {
            updateStandardFieldEditiability(true);
        } else {
            updateStandardFieldEditiability(false);
        }
        
    }//GEN-LAST:event_jCheckBox1ActionPerformed

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
            dtm.removeRow(i);
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
            String location = JOptionPane.showInputDialog(this, "Location:", "Save Project", JOptionPane.QUESTION_MESSAGE);
            XStream xstream = DataInitializer.getXstream();
            File outputFile = new File(location+"/"+projectData.getProjectName()+".pscs");
            if(!outputFile.exists()) {
                xstream.toXML(projectData, new FileOutputStream(outputFile));
                JOptionPane.showMessageDialog(this, "Project saved successfully");
            } else {
                JOptionPane.showMessageDialog(this, "PDF with same project name exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (FileNotFoundException ex) {
            log.error("Unable to save project",ex);
            JOptionPane.showMessageDialog(this, "Save project failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveProjectBtnActionPerformed

    private void generateWaterDemandPDF() {
        
    }
    
    private Integer getIntegerVal(String text) throws InvalidInputException {
        if(text.isEmpty()){
            return 0;
        }
        try {
            return Integer.valueOf(text);
        } catch(NumberFormatException ex){
            throw new InvalidInputException("unable to convert "+text);
        }
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
    private javax.swing.JButton addOfficeDataBtn;
    private javax.swing.JButton addTowerBtn;
    private javax.swing.JButton clearGroupedTowerBtn;
    private javax.swing.JTextField clubHouseAreaInput;
    private javax.swing.JPanel consumptionParamPanel;
    private javax.swing.JTextField domesticSewerPercent;
    private javax.swing.JTextField domesticWaterTankPercent;
    private javax.swing.JTextField flushSewerPercent;
    private javax.swing.JButton groupTowers;
    private javax.swing.JPanel inputCriteriaForm;
    private javax.swing.JButton inputCriteriaTabNextBtn;
    private javax.swing.JButton inputDataNextBtn;
    private javax.swing.JPanel inputDataPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLableStdFrictionLoss;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextField landscapeAreaInput;
    private javax.swing.JComboBox<String> landscapeAreaWater;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField nrFlats1BHKInput;
    private javax.swing.JTextField nrFlats2BHKInput;
    private javax.swing.JTextField nrFlats2NHalfBHKInput;
    private javax.swing.JTextField nrFlats3AndHalfBHKInput;
    private javax.swing.JTextField nrFlats3BHKInput;
    private javax.swing.JTextField nrFlats4AndHalfBHKInput;
    private javax.swing.JTextField nrFlats4BHKInput;
    private javax.swing.JTextField nrOffice;
    private javax.swing.JTextField nrShowroom;
    private javax.swing.JTextField nrStudioInput;
    private javax.swing.JTable officeDataOverviewTable;
    private javax.swing.JTable officeDataTable;
    private javax.swing.JPanel officePanel;
    private javax.swing.JTextField ohtFireFightingTank;
    private javax.swing.JPanel outdoorOverviewPanel;
    private javax.swing.JButton outsideAreaBtn;
    private javax.swing.JPanel outsideAreaPanel;
    private javax.swing.JPanel overviewPanel;
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
    private javax.swing.JTextField rawWaterTankPercent;
    private javax.swing.JPanel residentialPanel;
    private javax.swing.JButton saveProjectBtn;
    private javax.swing.JTextField stdBasementArea;
    private javax.swing.JTextField stdExtraAreaCatered;
    private javax.swing.JTextField stdFrictionLossInput;
    private javax.swing.JTextField stdGasBank;
    private javax.swing.JTextField stdGreenArea;
    private javax.swing.JTextField stdHeightOfTallestBld;
    private javax.swing.JTextField stdPavedArea;
    private javax.swing.JTextField stdResidualHeadFire;
    private javax.swing.JTextField stdResidualHeadPlumbing;
    private javax.swing.JTextField stdSolar;
    private javax.swing.JTextField stdSolidWasteKgPPD;
    private javax.swing.JTextField stdSolidWasteLtrPerFamily;
    private javax.swing.JTextField stdTerrace;
    private javax.swing.JTextField stdTotalPlotArea;
    private javax.swing.JTextField stdwaterDemand;
    private javax.swing.JTextField stpFlushingTank;
    private javax.swing.JTextField swimmingAreaInput;
    private javax.swing.JComboBox<String> swimmingPoolCapacity;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable towerDataOveriewTable;
    private javax.swing.JTable towerDataTable;
    private javax.swing.JPanel towerGroupPanel;
    private javax.swing.JScrollPane towerGroupScrollPane;
    private javax.swing.JTable towerGroupTable;
    private javax.swing.JTextField towerNameInput;
    private javax.swing.JPanel typePanel;
    private javax.swing.JTextField ugrFireFightingTank;
    private javax.swing.JPanel waterDemandOverviewPanel;
    private javax.swing.JButton waterDemandPDFBtn;
    // End of variables declaration//GEN-END:variables

    private void addCheckBoxForTower(String towerName) {
        addCheckBoxForTower(towerName, true);
    }
  
    private void addCheckBoxForTower(String towerName, boolean status){
        JCheckBox checkbox = new JCheckBox(towerName, false);
        checkbox.setName(towerName);
        checkbox.setMargin(new Insets(5,5,5,5));
        checkbox.setEnabled(status);
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
    
    private void addRowToTowerTable(String towerName, TreeMap<StandardType,Integer> flatPerTower, boolean checkboxStatus) {
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
        addCheckBoxForTower(towerName, checkboxStatus);
    }


}
