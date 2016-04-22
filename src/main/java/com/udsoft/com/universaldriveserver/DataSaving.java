/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udsoft.com.universaldriveserver;

/**
 *
 * @author darwin
 */

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSaving 
{
    /**
     * Setting all the private ,public and protected variables.
     */
    
    private String filename;
    private String completeFileName;
   
    
    /**
     * Constructor for DataSaving Class
     * @param filename create a file using this name
     */
    public DataSaving(String filename){
        this.filename = filename;
        CreateFile();

    }
    
    private void CreateFile(){
        LocalDateTime time = LocalDateTime.now();
        String timeStamp = time.toString();
        completeFileName = filename + timeStamp + ".csv";
    
       
    }
    
    
    
}
