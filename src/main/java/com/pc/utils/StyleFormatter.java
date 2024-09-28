/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.utils;

import java.awt.Color;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Prashant
 */
public class StyleFormatter {
    
    public static void setTableColumnAlignment(JTable table, int alignment, List<Integer> columnIndexes) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);
        for(int columnIndex : columnIndexes) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }
    
    public static void setTableColumnAlignment(JTable table, int alignment, boolean allColumns) {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(alignment);
//        headerRenderer.setVerticalAlignment(alignment);
        headerRenderer.setForeground(Color.blue);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(alignment);
//        cellRenderer.setVerticalAlignment(alignment);
        cellRenderer.setForeground(Color.black);
        int columnCount = table.getColumnModel().getColumnCount();
        for(int columnIndex=0 ; columnIndex < columnCount; columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
        }
    }
}
