/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.utils;

import com.pc.exceptions.InvalidInputException;

/**
 *
 * @author Prashant
 */
public class DataUtils {
    public static Integer getIntegerVal(String text) throws InvalidInputException {
        if(text.isEmpty()){
            return 0;
        }
        try {
            return Integer.valueOf(text);
        } catch(NumberFormatException ex){
            throw new InvalidInputException("unable to convert "+text);
        }
    }
}
