/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.enums;

/**
 *
 * @author Prashant
 */
public enum ConsumptionType {

    TOWER("TOWER"),
    OUTSIDE_AREA("OUTSIDE AREA"),
    OFFICE("OFFICE");
    
    private final String value;

    ConsumptionType(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }
}
