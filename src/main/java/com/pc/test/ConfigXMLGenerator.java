/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pc.test;

import com.pc.plumbit.enums.StandardType;
import com.pc.plumbit.model.WaterConsumption;
import com.pc.plumbit.model.StandardValues;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Prashant
 */
public class ConfigXMLGenerator {
    
    public static void main(String[] args){
        populationCriteriaConfig();
//        domesticWaterRequirementConfig();
    }
    
    private static void domesticWaterRequirementConfig() {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypesByWildcard(new String[] {
            "com.pc.plumbit.*"
        });
        
        WaterConsumption twoBHKDomWater =  new WaterConsumption(StandardType.TWO_BHK, 90, "lpcd");
        WaterConsumption threeBHKDomWater =  new WaterConsumption(StandardType.THREE_BHK, 90, "lpcd");
        WaterConsumption threeAndHalfBHKDomWater =  new WaterConsumption(StandardType.THREE_N_HALF_BHK, 90, "lpcd");
        WaterConsumption fourBHKDomWater =  new WaterConsumption(StandardType.FOUR_BHK, 90, "lpcd");
        WaterConsumption podsBHKDomWater =  new WaterConsumption(StandardType.STUDIO, 90, "lpcd");
        WaterConsumption studioBHKDomWater =  new WaterConsumption(StandardType.PODS, 90, "lpcd");
        WaterConsumption servantBHKDomWater =  new WaterConsumption(StandardType.SERVANT, 90, "lpcd");
        WaterConsumption driverBHKDomWater =  new WaterConsumption(StandardType.DRIVER, 90, "lpcd");
        WaterConsumption landscapeBHKDomWater =  new WaterConsumption(StandardType.LANDSCAPE, 90, "lpcd");
        WaterConsumption swimmingPoolDomWater =  new WaterConsumption(StandardType.SWIMMING_POOL, 90, "lpcd");
        WaterConsumption clubHouseDomWater =  new WaterConsumption(StandardType.CLUB_HOUSE, 90, "lpcd");
       
        List<WaterConsumption> list = new ArrayList<>();
        list.add(twoBHKDomWater);
        list.add(threeBHKDomWater);
        list.add(threeAndHalfBHKDomWater);
        list.add(fourBHKDomWater);
        list.add(podsBHKDomWater);
        list.add(studioBHKDomWater);
        list.add(servantBHKDomWater);
        list.add(driverBHKDomWater);
        list.add(landscapeBHKDomWater);
        list.add(swimmingPoolDomWater);
        list.add(clubHouseDomWater);
        System.out.println(xstream.toXML(list));
    }
    
    private static void populationCriteriaConfig() {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypesByWildcard(new String[] {
            "com.pc.plumbit.enums.*",
            "com.pc.plumbit.model.*",
        });
        
        HashMap<StandardType, StandardValues> standardValMap = new HashMap();
        StandardValues oneBHKCriteria =  new StandardValues(4.0, false, "person average");
        standardValMap.put(StandardType.ONE_BHK,oneBHKCriteria);
        StandardValues twoBHKCriteria =  new StandardValues(5, false, "person average");
        standardValMap.put(StandardType.TWO_BHK,twoBHKCriteria);
        StandardValues threeBHKCriteria =  new StandardValues( 6, false, "person average");
        standardValMap.put(StandardType.THREE_BHK,threeBHKCriteria);
        StandardValues threeAndHalfBHKCriteria =  new StandardValues(6,false,  "person average");
        standardValMap.put(StandardType.THREE_N_HALF_BHK, threeAndHalfBHKCriteria);
        StandardValues fourBHKCriteria =  new StandardValues( 7,false,  "person average");
        standardValMap.put(StandardType.FOUR_BHK,fourBHKCriteria);
        StandardValues fourAndHalfBHKCriteria =  new StandardValues( 7,false,  "person average");
        standardValMap.put(StandardType.FOUR_AND_HALF_BHK,fourAndHalfBHKCriteria);
        StandardValues studioCriteria =  new StandardValues( 3, false, "person average");
        standardValMap.put(StandardType.STUDIO,studioCriteria);
        StandardValues podsCriteria =  new StandardValues( 3, false, "person average");
        standardValMap.put(StandardType.PODS,podsCriteria);
        StandardValues servantCriteria =  new StandardValues( 1, false, "per 4 BHK flat");
        standardValMap.put(StandardType.SERVANT,servantCriteria);
        StandardValues driverBHKCriteria =  new StandardValues( 1, false, "per 4 BHK flat");
        standardValMap.put(StandardType.DRIVER,driverBHKCriteria);
        StandardValues landscapeCriteria =  new StandardValues( 0, false,  "-");
        standardValMap.put(StandardType.LANDSCAPE,landscapeCriteria);
        StandardValues swimmingPoolCriteria =  new StandardValues( 0, false,  "-");
        standardValMap.put(StandardType.SWIMMING_POOL,swimmingPoolCriteria);
        StandardValues clubHouseCriteria =  new StandardValues( 10, false, "sq. mtr per person");
        standardValMap.put(StandardType.CLUB_HOUSE,clubHouseCriteria);
        
        StandardValues waterCriteriaCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.WATER_DEMAND, waterCriteriaCriteria);
        StandardValues solidWasterPerKGPersonDayCriteria =  new StandardValues( 0.312, false, "lpcd");
        standardValMap.put(StandardType.SOLID_WASTE_KG_PERSON_DAY,solidWasterPerKGPersonDayCriteria);
        StandardValues solidWasterLtrPer20FamilyCriteria =  new StandardValues(65.9, false, "lpcd");
        standardValMap.put(StandardType.SOLID_WASTE_LITER_20_FAMILY, solidWasterLtrPer20FamilyCriteria);
        StandardValues gasBankCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.GAS_BANK, gasBankCriteria);
        StandardValues solarCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.SOLAR,solarCriteria);
        
        StandardValues terraceCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.TERRACE, terraceCriteria);
        StandardValues greenAreaCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.GREEN_AREA,greenAreaCriteria);
        StandardValues pavedAreaCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.PAVED_AREA,pavedAreaCriteria);
        StandardValues totalPlotAreadCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.TOTAL_PLOT_AREA,totalPlotAreadCriteria);
        StandardValues extraAreaToBeCateredCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.EXTRA_AREA_TO_BE_CATERED, extraAreaToBeCateredCriteria);
        
        StandardValues heightOfTallestBldFromGrCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.HEIGHT_OF_TALLEST_BUILDING_FROM_GR,heightOfTallestBldFromGrCriteria);
        StandardValues basementAreaCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.BASEMENT_AREA,basementAreaCriteria);
        StandardValues residentialHeadPlumbingCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.RESIDUAL_HEAD_PLUMBING, residentialHeadPlumbingCriteria);
        StandardValues residentialHeadFireCriteria =  new StandardValues( 135, false, "lpcd");
        standardValMap.put(StandardType.RESIDUAL_HEAD_FIRE,residentialHeadFireCriteria);
        StandardValues frictionLossCriteria =  new StandardValues(135, false, "lpcd");
        standardValMap.put(StandardType.FRICTION_LOSS, frictionLossCriteria);
        
        /*List<StandardValues> list = new ArrayList<>();
        list.add(twoBHKCriteria);
        list.add(threeBHKCriteria);
        list.add(threeAndHalfBHKCriteria);
        list.add(fourBHKCriteria);
        list.add(studioCriteria);
        list.add(podsCriteria);
        list.add(servantCriteria);
        list.add(driverBHKCriteria);
        list.add(landscapeCriteria);
        list.add(swimmingPoolCriteria);
        list.add(clubHouseCriteria);
        
        list.add(waterCriteriaCriteria);
        list.add(solidWasterPerKGPersonDayCriteria);
        list.add(solidWasterLtrPer20FamilyCriteria);
        list.add(gasBankCriteria);
        list.add(solarCriteria);
        
        list.add(terraceCriteria);
        list.add(greenAreaCriteria);
        list.add(pavedAreaCriteria);
        list.add(totalPlotAreadCriteria);
        list.add(extraAreaToBeCateredCriteria);
        
        list.add(heightOfTallestBldFromGrCriteria);
        list.add(basementAreaCriteria);
        list.add(residentialHeadPlumbingCriteria);
        list.add(residentialHeadFireCriteria);
        list.add(frictionLossCriteria);*/
        
        
        
        String xmlVals = xstream.toXML(standardValMap);
        System.out.println(xmlVals);
        xstream.allowTypesByWildcard(new String[] {
            "com.pc.plumbit.*"
        });
        
        HashMap<StandardType, StandardValues> maps = (HashMap<StandardType, StandardValues>) xstream.fromXML(xmlVals);
        maps.keySet().forEach(key->System.out.println(maps.get(key)));
        
        
        
    }
}
