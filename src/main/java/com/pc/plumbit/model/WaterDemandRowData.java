/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

/**
 *
 * @author Prashant
 */
public class WaterDemandRowData {
    private String description;
    protected float lpcdDom;
    protected float lpda;
    protected float flowToSewerDom;
    protected float lpcdFlush;
    protected float lpdb;
    protected float flowToSewerFlush;
    protected float grossLPD;
    protected float totalFlowToSewer;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLpcdDom() {
        return lpcdDom;
    }

    public void setLpcdDom(float lpcdDom) {
        this.lpcdDom = lpcdDom;
    }

    public float getLpda() {
        return lpda;
    }

    public void setLpda(float ldpa) {
        this.lpda = ldpa;
    }

    public float getFlowToSewerDom() {
        return flowToSewerDom;
    }

    public void setFlowToSewerDom(float flowToSewerDom) {
        this.flowToSewerDom = flowToSewerDom;
    }

    public float getLpcdFlush() {
        return lpcdFlush;
    }

    public void setLpcdFlush(float lpcdFlush) {
        this.lpcdFlush = lpcdFlush;
    }

    public float getLpdb() {
        return lpdb;
    }

    public void setLpdb(float lpdb) {
        this.lpdb = lpdb;
    }

    public float getFlowToSewerFlush() {
        return flowToSewerFlush;
    }

    public void setFlowToSewerFlush(float flowToSewerFlush) {
        this.flowToSewerFlush = flowToSewerFlush;
    }

    public float getGrossLPD() {
        return grossLPD;
    }

    public void setGrossLPD(float grossLPD) {
        this.grossLPD = grossLPD;
    }

    public float getTotalFlowToSewer() {
        return totalFlowToSewer;
    }

    public void setTotalFlowToSewer(float toatlFlowToSewer) {
        this.totalFlowToSewer = toatlFlowToSewer;
    }
               
}
