/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

import com.pc.plumbit.enums.StandardType;

/**
 *
 * @author Prashant
 */
public class StandardValues {
    private double value;
    private boolean round;
    private String description;
    
    public StandardValues(StandardValues standardValues, double val) {
        this.description = standardValues.description;
        this.round = standardValues.round;
        this.value = val;
    }
    public StandardValues(double value, boolean round, String description) {
        this.value = value;
        this.round = round;
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isRound() {
        return round;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        if(round){
            return ""+(int)this.value + " " + this.description;
        } else {
            return ""+this.value +" " + this.description;
        }
    }
}
