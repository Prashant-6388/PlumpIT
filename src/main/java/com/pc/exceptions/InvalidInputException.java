/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.exceptions;

/**
 *
 * @author Prashant
 */
public class InvalidInputException extends Exception {
    private String message;

    public InvalidInputException(String message) {
        super(message);
    }
}
