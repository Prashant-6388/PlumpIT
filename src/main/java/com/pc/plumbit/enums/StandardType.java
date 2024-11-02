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
    
    OFFICE("Office"),
    SHOWROOM("Showroom"),
    
    SQ_MTR_PER_PERSON_AT_GROUND("Sq. Mtr. Per people at ground Floor"),
    SQ_MTR_PER_PERSON_ABOVE_GROUND("Sq. Mtr. Per people above ground Floor"),
    SQ_MTR_PER_PERSON_OFFICE("Sq. Mtr. Per people"),
    
    WATER_DEMAND("WATER DEMAND"),
    SOLID_WASTE_KG_PERSON_DAY("SOLID WASTE Kg/person/day"),
    GAS_BANK("GAS_BANK"),
    SOLAR("SOLAR"),
    SOLID_WASTE_DRY_GARBAGE("SOLID WASTE DRY GARBAGE"),
    SOLID_WASTE_WET_GARBAGE("SOLID WASTE WET GARBAGE"),
    
    WATER_DEMAND_COMMERCIAL("WATER DEMAND Commertial"),
    SOLID_WASTE_KG_PERSON_DAY_COMMERCIAL("SOLID WASTE Kg/person/day Commertial"),
    GAS_BANK_COMMERCIAL("GAS_BANK Commertial"),
    SOLAR_COMMERCIAL("SOLAR Commertial"),
    SOLID_WASTE_DRY_GARBAGE_COMMERCIAL("SOLID WASTE DRY GARBAGE Commertial"),
    SOLID_WASTE_WET_GARBAGE_COMMERCIAL("SOLID WASTE WET GARBAGE Commertial"),
    
    LANDSCAPE_AREA_WATER_PERCENTAGE("Landscape water percentage"),
    SWIMMING_POOL_AREA_PERCENT("Swimming pool area percentage"),
    ;

    private final String value;

    StandardType(String type) {
        this.value = type;
    }

    public String getValue() {
        return value;
    }
}
