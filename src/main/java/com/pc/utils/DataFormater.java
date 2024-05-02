/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.utils;

import com.pc.plumbit.model.StandardValues;

/**
 *
 * @author Prashant
 */
public class DataFormater {
    
    public static String getStandardValue(StandardValues standardVal){
        if(standardVal != null){
            double value = standardVal.getValue();
            if(standardVal.isRound()){
                Integer i = (int)value;
                return i.toString();
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }
    
    public static String getFlushWaterRequirement(StandardValues standardVal){
        if(standardVal != null){
            double value = standardVal.getValue() * 1/3;
            if(standardVal.isRound()){
                Integer i = (int)value;
                return i.toString();
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }
    
    public static String getDomWaterRequirement(StandardValues standardVal){
        if(standardVal != null){
            double value = standardVal.getValue() * 2/3;
            if(standardVal.isRound()){
                Integer i = (int)value;
                return i.toString();
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }
}
