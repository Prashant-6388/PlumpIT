/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input;

/**
 *
 * @author Prashant
 */
public class PlotArea {
    private float terrace;
    private float greenArea;
    private float pavedArea;
    private float totalAreaPlot;
    private float extraAreaCatered;

    public PlotArea(float terrace, float greenArea, float pavedArea, float totalAreaPlot, float extraAreaCatered) {
        this.terrace = terrace;
        this.greenArea = greenArea;
        this.pavedArea = pavedArea;
        this.totalAreaPlot = totalAreaPlot;
        this.extraAreaCatered = extraAreaCatered;
    }
    
    public float getTerrace() {
        return terrace;
    }

    public void setTerrace(float terrace) {
        this.terrace = terrace;
    }

    public float getGreenArea() {
        return greenArea;
    }

    public void setGreenArea(float greenArea) {
        this.greenArea = greenArea;
    }

    public float getPavedArea() {
        return pavedArea;
    }

    public void setPavedArea(float pavedArea) {
        this.pavedArea = pavedArea;
    }

    public float getTotalAreaPlot() {
        return totalAreaPlot;
    }

    public void setTotalAreaPlot(float totalAreaPlot) {
        this.totalAreaPlot = totalAreaPlot;
    }

    public float getExtraAreaCatered() {
        return extraAreaCatered;
    }

    public void setExtraAreaCatered(float extraAreaCatered) {
        this.extraAreaCatered = extraAreaCatered;
    }
    
}
