/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.generator;

/**
 *
 * @author Prashant
 */
public class CellPadding {
    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;

    public CellPadding() {
        this.paddingLeft = 0;
        this.paddingRight = 0;
        this.paddingTop = 0;
        this.paddingBottom = 0;
    }

    public CellPadding(float paddingLeft) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = 0;
        this.paddingTop = 0;
        this.paddingBottom = 0;
    }

    public CellPadding(float paddingLeft, float paddingRight) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = 0;
        this.paddingBottom = 0;
    }

    public CellPadding(float paddingLeft, float paddingRight, float paddingTop) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = 0;
    }

    public CellPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
    }

    public float getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public float getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public float getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }
    
    
    
}
