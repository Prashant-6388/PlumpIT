/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Element;
import java.awt.Color;

/**
 *
 * @author Prashant
 */
public class CellStyle {
    private int rowSpan;
    private int colSpan;
    private int verticalAlignment;
    private int horizontalAlignment;
    private int rotation;
    private Color backgroundColor; 
    private CellPadding padding;
    
    public CellStyle() {
        this(1, 1);
    }

    public CellStyle(boolean alignCenter) {
        this(1, 1, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
    }

    public CellStyle(int rowSpan, int colSpan) {
        this(rowSpan, colSpan, -1, -1);
    }
    
    public CellStyle(int rowSpan, int colSpan, boolean alignCenter) {
        this(rowSpan, colSpan, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
    }
    
    public CellStyle(int rowSpan, int colSpan, int verticalAlignment, int horizontalAlignment) {
        this(rowSpan, colSpan, verticalAlignment, horizontalAlignment, 0);
    }
    
    public CellStyle(int rowSpan, int colSpan, int verticalAlignment, int horizontalAlignment, int rotation) {
        this(rowSpan, colSpan, verticalAlignment, horizontalAlignment, rotation, Color.white);
    }
    
    public CellStyle(int rowSpan, int colSpan, int verticalAlignment, int horizontalAlignment, int rotation, Color backgroundColor) {
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.verticalAlignment = verticalAlignment;
        this.horizontalAlignment = horizontalAlignment;
        this.rotation = rotation;
        this.backgroundColor = backgroundColor;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public CellPadding getPadding() {
        return padding;
    }

    public void setPadding(CellPadding padding) {
        this.padding = padding;
    }

    
}

