/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udsoft.com.universaldriveserver.experiment;

/**
 *
 * @author darwin
 */

import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import java.util.Date;

public class csvTest extends BeanToCsv
{
    ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
    
    BeanToCsv data = new BeanToCsv();
    
    public static void main(String[] args){
        
    }
}
