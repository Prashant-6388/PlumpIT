/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.plumbit.enums;

/**
 *
 * @author Prashant
 */
public enum StandardType {
    ONE_BHK("1 BHK"),
    TWO_BHK("2 BHK"),
    TWO_N_HALF_BHK("2.5 BHK"),
    THREE_BHK("3 BHK"),
    THREE_N_HALF_BHK("3.5 BHK"),
    FOUR_BHK("4 BHK"),
    FOUR_AND_HALF_BHK("4.5 BHK"),
    SERVANT("SERVANT"),
    DRIVER("DRIVER"),

    LANDSCAPE("Landscape"),
    SWIMMING_POOL("Swimming Pool"),
    CLUB_HOUSE("Club House"),
    PODS("PODS"),
    STUDIO("STUDIO"),
    
    OFFICE("OFFICE"),
    SHOWROOM("SHOWROOM"),
    
    WATER_DEMAND("WATER DEMAND"),
    SOLID_WASTE_KG_PERSON_DAY("SOLID WASTE Kg/person/day"),
    SOLID_WASTE_LITER_20_FAMILY("SOLID WASTE Liter/20 families"),
    GAS_BANK("GAS_BANK"),
    SOLAR("SOLAR"),
    
    TERRACE("TERRACE"),
    GREEN_AREA("GREEN AREA"),
    PAVED_AREA("PAVED AREA"),
    TOTAL_PLOT_AREA("TOTAL PLOT AREA"),
    EXTRA_AREA_TO_BE_CATERED("TOTAL AREA TO BE CATERED"),
    
    HEIGHT_OF_TALLEST_BUILDING_FROM_GR("HEIGHT OF TALLEST BUILDING FROM GR"),
    BASEMENT_AREA("BASEMENT AREA"),
    RESIDUAL_HEAD_PLUMBING("RESIDUAL HEAD (PLUMBING)"),
    RESIDUAL_HEAD_FIRE("RESIDUAL HEAD (FIRE)"),
    FRICTION_LOSS("FRICTION LOSS"),
    
    FLOW_TO_SEWER_DOM("FLOW TO SEWER DOMESTIC"),
    FLOW_TO_SEWER_FLS("FLOW TO SEWER FLUSH"),
    
    RAW_WATER_TANK("RAW WATER TANK"),
    DOMESTIC_TANK("DOMESTIC TANK"),
    STP_FLUSHING_TANK("STP FLUSHING TANK"),
    
    LANDSCAPE_AREA_WATER_PERCENTAGE("Landscape water percentage"),
    SWIMMING_POOL_AREA_PERCENT("Swimming pool area percentage"),
    UGR_FIRE_FIGHTING_TANK("UGR FIRE FIGHTING TANK"),
    OHT_FIRE_FIGHTING_TANK("OHT FIRE FIGHTING TANK")
    ;

    private final String value;

    StandardType(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }
}
