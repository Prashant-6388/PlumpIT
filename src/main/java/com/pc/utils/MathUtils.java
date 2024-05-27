/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.utils;

/**
 *
 * @author Prashant
 */
public class MathUtils {
    
    public static int roundUpToNearestTenThousand(int number) {
        int divisor = 1000;
        int remainder = number % divisor;
        if (remainder == 0) {
            return number; // Already a multiple of 10000
        } else {
            return (number / divisor + 1) * divisor;
        }
    }
}
