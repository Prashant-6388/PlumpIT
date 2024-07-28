/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input;

/**
 *
 * @author Prashant
 */
public class FireFightingDetails {
    private float heightOfTallestBuilding;
    private float basementArea;
    private float residualHeadPlumbing;
    private float residualHeadFire;
    private float frictionLoss;

    public FireFightingDetails(float heightOfTallestBuilding, float basementArea, float residualHeadPlumbing, float residualHeadFire, float frictionLoss) {
        this.heightOfTallestBuilding = heightOfTallestBuilding;
        this.basementArea = basementArea;
        this.residualHeadPlumbing = residualHeadPlumbing;
        this.residualHeadFire = residualHeadFire;
        this.frictionLoss = frictionLoss;
    }
    
    public float getHeightOfTallestBuilding() {
        return heightOfTallestBuilding;
    }

    public void setHeightOfTallestBuilding(float heightOfTallestBuilding) {
        this.heightOfTallestBuilding = heightOfTallestBuilding;
    }

    public float getBasementArea() {
        return basementArea;
    }

    public void setBasementArea(float basementArea) {
        this.basementArea = basementArea;
    }

    public float getResidualHeadPlumbing() {
        return residualHeadPlumbing;
    }

    public void setResidualHeadPlumbing(float residualHeadPlumbing) {
        this.residualHeadPlumbing = residualHeadPlumbing;
    }

    public float getResidualHeadFire() {
        return residualHeadFire;
    }

    public void setResidualHeadFire(float residualHeadFire) {
        this.residualHeadFire = residualHeadFire;
    }

    public float getFrictionLoss() {
        return frictionLoss;
    }

    public void setFrictionLoss(float frictionLoss) {
        this.frictionLoss = frictionLoss;
    }
   
}
