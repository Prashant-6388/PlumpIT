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
public class WaterConsumption {
    private StandardType type;
    private int qty;
    private String message;

    public WaterConsumption(StandardType type, int qty, String message) {
        this.type = type;
        this.qty = qty;
        this.message = message;
    }

    public StandardType getType() {
        return type;
    }

    public void setType(StandardType type) {
        this.type = type;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
