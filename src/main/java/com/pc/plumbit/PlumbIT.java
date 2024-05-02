/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.pc.plumbit;

import com.pc.plumbit.enums.StandardType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prashant
 */
public class PlumbIT {
//    public static final TreeMap<ConsumptionType, Integer> peoplePerType = new TreeMap<>();

    public static void main(String[] args) {
        /*peoplePerType.put(ConsumptionType.TWO_BHK, 5);
        peoplePerType.put(ConsumptionType.TWO_N_HALF_BHK, 5);
        peoplePerType.put(ConsumptionType.THREE_BHK, 6);
        peoplePerType.put(ConsumptionType.THREE_N_HALF_BHK, 6);
        peoplePerType.put(ConsumptionType.FOUR_BHK, 7);
        peoplePerType.put(ConsumptionType.STUDIO, 3);
        peoplePerType.put(ConsumptionType.PODS, 3);
        peoplePerType.put(ConsumptionType.SERVANT, 1);
        peoplePerType.put(ConsumptionType.DRIVER, 1);
        peoplePerType.put(ConsumptionType.LANDSCAPE, 0);
        peoplePerType.put(ConsumptionType.SWIMMING_POOL, 0);
        peoplePerType.put(ConsumptionType.CLUB_HOUSE, 10);*/
//        XStream xstream = new XStream(new DomDriver());
//        System.out.println(xstream.toXML(peoplePerType));
        
        DataCalculator cal = new DataCalculator();
        cal.setVisible(true);
    }
}
