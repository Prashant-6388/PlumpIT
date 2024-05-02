/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.model;

import com.pc.plumbit.enums.StandardType;
import java.util.TreeMap;

/**
 *
 * @author Prashant
 */
public class TowerData {
    String name;
    TreeMap<StandardType, Integer> flatsData;

    public TowerData(String name, TreeMap<StandardType, Integer> numberOfFlats) {
        this.name = name;
        this.flatsData = numberOfFlats;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeMap<StandardType, Integer> getFlatsData() {
        return flatsData;
    }

    public void setFlatsData(TreeMap<StandardType, Integer> flatsData) {
        this.flatsData = flatsData;
    }   
}
