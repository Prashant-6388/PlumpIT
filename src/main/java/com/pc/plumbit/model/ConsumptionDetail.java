/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

import com.pc.plumbit.enums.ConsumptionType;

/**
 *
 * @author Prashant
 */
public class ConsumptionDetail {
    private String name;
    private double domestic;
    private double flush;
    private double sewer;
    private ConsumptionType type;
    private String subType;

    public ConsumptionDetail(String name, String subType, double domestic, double flush, double sewer, ConsumptionType type) {
        this.name = name;
        this.subType = subType;
        this.domestic = domestic;
        this.flush = flush;
        this.sewer = sewer;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public double getDomestic() {
        return domestic;
    }

    public void setDomestic(double domestic) {
        this.domestic = domestic;
    }

    public double getFlush() {
        return flush;
    }

    public void setFlush(double flush) {
        this.flush = flush;
    }

    public double getSewer() {
        return sewer;
    }

    public void setSewer(double sewer) {
        this.sewer = sewer;
    }

    public ConsumptionType getType() {
        return type;
    }

    public void setType(ConsumptionType type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
        
}
