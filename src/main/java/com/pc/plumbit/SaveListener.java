/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pc.plumbit;

import com.pc.plumbit.model.input.OfficeData;
import com.pc.plumbit.model.input.TowerData;

/**
 *
 * @author Prashant
 */
public interface SaveListener {
    void onSave(TowerData data, int index);
    void onSaveOfficeData(OfficeData data, int index);
}
