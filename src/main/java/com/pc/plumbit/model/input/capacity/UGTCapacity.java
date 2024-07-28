/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input.capacity;

/**
 *
 * @author Prashant
 */
public class UGTCapacity {
    private float domesticTank;
    private float rawWaterTank;
    private float fireFightingTank;
    private int flushingTank;

    public UGTCapacity(float domesticTank, float rawWaterTank, float fireFightingTank, int flushingTank) {
        this.domesticTank = domesticTank;
        this.rawWaterTank = rawWaterTank;
        this.fireFightingTank = fireFightingTank;
        this.flushingTank = flushingTank;
    }

    public float getDomesticTank() {
        return domesticTank;
    }

    public void setDomesticTank(float domesticTank) {
        this.domesticTank = domesticTank;
    }

    public float getRawWaterTank() {
        return rawWaterTank;
    }

    public void setRawWaterTank(float rawWaterTank) {
        this.rawWaterTank = rawWaterTank;
    }

    public float getFireFightingTank() {
        return fireFightingTank;
    }

    public void setFireFightingTank(float fireFightingTank) {
        this.fireFightingTank = fireFightingTank;
    }

    public int getFlushingTank() {
        return flushingTank;
    }

    public void setFlushingTank(int flushingTank) {
        this.flushingTank = flushingTank;
    }
    
    
}
