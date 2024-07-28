/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model.input.capacity;

/**
 *
 * @author Prashant
 */
public class STPCapacity {
    private float domesticFlow;
    private float flushinhFlow;

    public STPCapacity(float domesticFlow, float flushinhFlow) {
        this.domesticFlow = domesticFlow;
        this.flushinhFlow = flushinhFlow;
    }

    public float getDomesticFlow() {
        return domesticFlow;
    }

    public void setDomesticFlow(float domesticFlow) {
        this.domesticFlow = domesticFlow;
    }

    public float getFlushinhFlow() {
        return flushinhFlow;
    }

    public void setFlushinhFlow(float flushinhFlow) {
        this.flushinhFlow = flushinhFlow;
    }
    
}
