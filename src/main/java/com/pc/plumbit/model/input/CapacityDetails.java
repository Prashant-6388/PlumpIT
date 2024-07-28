/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input;

import com.pc.plumbit.model.input.capacity.OHTCapacity;
import com.pc.plumbit.model.input.capacity.STPCapacity;
import com.pc.plumbit.model.input.capacity.UGTCapacity;

/**
 *
 * @author Prashant
 */
public class CapacityDetails {
    private UGTCapacity uGTCapacity;
    private OHTCapacity oHTCapacity;
    private STPCapacity sTPCapacity;

    public CapacityDetails(UGTCapacity uGTCapacity, OHTCapacity oHTCapacity, STPCapacity sTPCapacity) {
        this.uGTCapacity = uGTCapacity;
        this.oHTCapacity = oHTCapacity;
        this.sTPCapacity = sTPCapacity;
    }

    public UGTCapacity getuGTCapacity() {
        return uGTCapacity;
    }

    public void setuGTCapacity(UGTCapacity uGTCapacity) {
        this.uGTCapacity = uGTCapacity;
    }

    public OHTCapacity getoHTCapacity() {
        return oHTCapacity;
    }

    public void setoHTCapacity(OHTCapacity oHTCapacity) {
        this.oHTCapacity = oHTCapacity;
    }

    public STPCapacity getsTPCapacity() {
        return sTPCapacity;
    }

    public void setsTPCapacity(STPCapacity sTPCapacity) {
        this.sTPCapacity = sTPCapacity;
    }
    
}
