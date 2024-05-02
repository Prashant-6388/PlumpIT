/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

import com.lowagie.text.Element;

/**
 *
 * @author Prashant
 */
public class CellStyle {
    int rowSpan;
    int colSpan;
    int verticalAlignment;
    int horizontalAlignment;

    public CellStyle() {
        this.rowSpan = 0;
        this.colSpan = 0;
        this.verticalAlignment = -1;
        this.horizontalAlignment = -1;
    }

    public CellStyle(boolean alignCenter) {
        this.rowSpan = 0;
        this.colSpan = 0;
        this.verticalAlignment = Element.ALIGN_CENTER;
        this.horizontalAlignment = Element.ALIGN_CENTER;
    }

    public CellStyle(int rowSpan, int colSpan) {
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.verticalAlignment = -1;
        this.horizontalAlignment = -1;
    }

    public CellStyle(int rowSpan, int colSpan, int verticalAlignment, int horizontalAlignment) {
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.verticalAlignment = verticalAlignment;
        this.horizontalAlignment = horizontalAlignment;
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
}

