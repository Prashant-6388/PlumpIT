/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input;

import com.pc.plumbit.enums.StandardType;

/**
 *
 * @author Prashant
 */
public class OfficeData {
    private String floorNumer;
    private StandardType type;
    private int area;
    private int numberOfFloors;
    private int sqMtrPerPerson;
    private float nrOfShifts;

    public OfficeData(int sqMtrPerPerson) {
        this.sqMtrPerPerson = sqMtrPerPerson;
    }

    public String getFloorNumer() {
        return floorNumer;
    }

    public void setFloorNumer(String floorNumer) {
        this.floorNumer = floorNumer;
    }

    public StandardType getType() {
        return type;
    }

    public void setType(StandardType type) {
        this.type = type;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(int numberOffloors) {
        this.numberOfFloors = numberOffloors;
    }

    public int getSqMtrPerPerson() {
        return sqMtrPerPerson;
    }

    public float getNrOfShifts() {
        return nrOfShifts;
    }

    public void setNrOfShifts(float nrOfShifts) {
        this.nrOfShifts = nrOfShifts;
    }

    
}
