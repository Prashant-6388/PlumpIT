/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input.capacity;

/**
 *
 * @author Prashant
 */
public class OHTCapacity {
    private float domesticTank;
    private float fireFightingTank;
    private float flushingTank;

    public OHTCapacity(float domesticTank, float flushingTank, float fireFightingTank) {
        this.domesticTank = domesticTank;
        this.fireFightingTank = fireFightingTank;
        this.flushingTank = flushingTank;
    }

    public float getDomesticTank() {
        return domesticTank;
    }

    public void setDomesticTank(float domesticTank) {
        this.domesticTank = domesticTank;
    }

    public float getFireFightingTank() {
        return fireFightingTank;
    }

    public void setFireFightingTank(float fireFightingTank) {
        this.fireFightingTank = fireFightingTank;
    }

    public float getFlushingTank() {
        return flushingTank;
    }

    public void setFlushingTank(float flushingTank) {
        this.flushingTank = flushingTank;
    }
}
