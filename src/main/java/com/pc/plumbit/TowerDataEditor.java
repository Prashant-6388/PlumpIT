/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pc.plumbit;

import com.pc.exceptions.InvalidInputException;
import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.input.TowerData;
import com.pc.utils.DataUtils;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Prashant
 */
public class TowerDataEditor extends javax.swing.JFrame {

    private List<TowerData> towerDataList;
    private int index;
    private SaveListener saveListener;
    
    public TowerDataEditor(List<TowerData> towerDataList, int index, SaveListener saveListener) {
        initComponents();
        this.towerDataList = towerDataList;
        this.index = index;
        this.saveListener = saveListener;
        setTowerData(towerDataList.get(index));
    }

    private TowerDataEditor() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        container = new javax.swing.JPanel();
        towerDataEditorPanel = new javax.swing.JPanel();
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
        saveTowerDataBtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        towerNameInput = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tower Data Editor");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        towerDataEditorPanel.setForeground(new java.awt.Color(255, 255, 0));

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("1 BHK");
        jLabel40.setAlignmentX(0.5F);
        jLabel40.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel40.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel40.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats1BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats1BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("2 BHK");
        jLabel41.setAlignmentX(0.5F);
        jLabel41.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel41.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel41.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("2.5 BHK");
        jLabel42.setAlignmentX(0.5F);
        jLabel42.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel42.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel42.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("3 BHK");
        jLabel43.setAlignmentX(0.5F);
        jLabel43.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel43.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel43.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel43.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel44.setText("3.5 BHK");
        jLabel44.setAlignmentX(0.5F);
        jLabel44.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel44.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel44.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel44.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel45.setText("4 BHK");
        jLabel45.setAlignmentX(0.5F);
        jLabel45.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel45.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel45.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel45.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel46.setText("4.5 BHK");
        jLabel46.setAlignmentX(0.5F);
        jLabel46.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel46.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel46.setPreferredSize(new java.awt.Dimension(20, 80));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("Studio");
        jLabel47.setAlignmentX(0.5F);
        jLabel47.setMaximumSize(new java.awt.Dimension(68, 16));
        jLabel47.setMinimumSize(new java.awt.Dimension(68, 16));
        jLabel47.setPreferredSize(new java.awt.Dimension(20, 80));

        nrStudioInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrStudioInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4AndHalfBHKInput.setName(""); // NOI18N
        nrFlats4AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats4BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats4BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3AndHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3AndHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats3BHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats3BHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        nrFlats2NHalfBHKInput.setMaximumSize(new java.awt.Dimension(20, 40));
        nrFlats2NHalfBHKInput.setPreferredSize(new java.awt.Dimension(20, 80));

        saveTowerDataBtn.setText("Save");
        saveTowerDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTowerDataBtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Type");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("# of Flats");

        jLabel62.setText("Tower Name");

        towerNameInput.setEnabled(false);

        javax.swing.GroupLayout towerDataEditorPanelLayout = new javax.swing.GroupLayout(towerDataEditorPanel);
        towerDataEditorPanel.setLayout(towerDataEditorPanelLayout);
        towerDataEditorPanelLayout.setHorizontalGroup(
            towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(63, 63, 63)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                        .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(nrStudioInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                                    .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                        .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(18, 18, 18)
                                    .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(towerNameInput, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))))
                    .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(saveTowerDataBtn)))
                .addGap(0, 56, Short.MAX_VALUE))
        );
        towerDataEditorPanelLayout.setVerticalGroup(
            towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(towerDataEditorPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(towerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nrFlats1BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats2NHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats3AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4BHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrFlats4AndHalfBHKInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(towerDataEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nrStudioInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(saveTowerDataBtn)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout containerLayout = new javax.swing.GroupLayout(container);
        container.setLayout(containerLayout);
        containerLayout.setHorizontalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(towerDataEditorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        containerLayout.setVerticalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, containerLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(towerDataEditorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(container, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveTowerDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTowerDataBtnActionPerformed
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
        TreeMap<StandardType, Integer> flatPerTower = new TreeMap<>();

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
      
        if(valid) {
            TowerData towerData = new TowerData(towerName, flatPerTower);
            saveListener.onSave(towerData, index);
            this.dispose();
        } else {
            flatPerTower.clear();
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveTowerDataBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel container;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JTextField nrFlats1BHKInput;
    private javax.swing.JTextField nrFlats2BHKInput;
    private javax.swing.JTextField nrFlats2NHalfBHKInput;
    private javax.swing.JTextField nrFlats3AndHalfBHKInput;
    private javax.swing.JTextField nrFlats3BHKInput;
    private javax.swing.JTextField nrFlats4AndHalfBHKInput;
    private javax.swing.JTextField nrFlats4BHKInput;
    private javax.swing.JTextField nrStudioInput;
    private javax.swing.JButton saveTowerDataBtn;
    private javax.swing.JPanel towerDataEditorPanel;
    private javax.swing.JTextField towerNameInput;
    // End of variables declaration//GEN-END:variables

    private void setTowerData(TowerData towerData) {
        towerNameInput.setText(towerData.getName());
        nrFlats1BHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.ONE_BHK)));
        nrFlats2BHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.TWO_BHK)));
        nrFlats2NHalfBHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.TWO_N_HALF_BHK)));
        nrFlats3BHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.THREE_BHK)));
        nrFlats3AndHalfBHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.THREE_N_HALF_BHK)));
        nrFlats4BHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.FOUR_BHK)));
        nrFlats4AndHalfBHKInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.FOUR_AND_HALF_BHK)));
        nrStudioInput.setText(String.valueOf(towerData.getFlatsData().get(StandardType.STUDIO)));
    }
}
