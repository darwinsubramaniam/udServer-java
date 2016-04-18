/*
 * Copyright 2016 darwin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udsoft.com.universaldriveserver;

/**
 *
 * @author darwin
 */

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DataAnalyse implements Runnable{
    //alle Notwediger Parameter zu initializierung
    private String dataspalt; //wo wird die daten splaten
    private String idSpalt; // wo wird die daten enden
    private int idPosition ;
    private int commandPosition;
    private int speedPosition;
    private int steeringPosition;
    private String finalDataConnector;
    private int finalDataArrayBlock;  // in a case of error the data CurrentdataArray will hv less or more array
                                      // than expected .. this will be a checking if the data recieved is a
                                      // clean data or a corrupted on. arrays are counted from 0
    private boolean isSplitEqualConnector;

    private String id = " ";
    private String data;
    private String[] currentArrayData;

    //DataAnalyse's Constructot

    //einfach Konstruktor
    public DataAnalyse(String dataspalt,String idSpalt,int idPosition,
            int commandPosition,int speedPosition,int steeringPosition,
            boolean isSplitEqualConnector, int finalDataArrayBlock){
        this.dataspalt = dataspalt;
        this.idSpalt = idSpalt;
        this.idPosition = idPosition;
        this.commandPosition = commandPosition;
        this.speedPosition = speedPosition;
        this.steeringPosition = steeringPosition;
        this.finalDataConnector = dataspalt;
        this.isSplitEqualConnector = isSplitEqualConnector;
        this.finalDataArrayBlock = finalDataArrayBlock;

    }

    public DataAnalyse(String dataspalt,String idSpalt,int idPosition,
            int commandPosition,int speedPosition,int steeringPosition,
            String finalDataConnector, int finalDataArrayBlock){
        this.dataspalt = dataspalt;
        this.idSpalt = idSpalt;
        this.idPosition = idPosition;
        this.speedPosition = speedPosition;
        this.steeringPosition = steeringPosition;
        this.finalDataConnector = finalDataConnector;
        this.isSplitEqualConnector = false;
        this.finalDataArrayBlock = finalDataArrayBlock;

    }

    public void setData(String data){
        this.data = data;
        runAnalyseData();
    }

    private void runAnalyseData(){
        currentArrayData = dataTrennen();
        idTrennen();
        

    }

    //Trennen die Daten in Array und Array erste position ist 0
    private String[] dataTrennen(){
        String[] arraydata = data.split(dataspalt);
        return arraydata;
    }

    // Trennen die IDposition um die ID nummer zu kriegen
    private void idTrennen(){
        String IDLocation = currentArrayData[idPosition];
        String[] temp_array = IDLocation.split(idSpalt);
        id = temp_array[1];
    }

    public String getID(){
        return id;
    }
    
    public String getCommand(){
        String command = currentArrayData[commandPosition];
        return command;
    }
    
    
    /**
     * checks if the data received and the expected data length received is same
     * if data is more or less then then expected length of array data is corrupted
     * @return 
     */
    public boolean isDataValid(){
        boolean valid = false;
        if(currentArrayData.length == finalDataArrayBlock){
            valid = true;
        }
        return valid;
    }



    //Return String Form of the Full data Recieved
    public String getCompleteData(){

        int length = currentArrayData.length;
        String temp = " ";
        if (length == finalDataArrayBlock){
            if(isSplitEqualConnector){
                temp =  data;
                //System.out.println(temp);
            }else{
                /***this part is not complete .. The output data is not how
                 * i planned... Research on this more
                 */
                for(int x = 0 ; x<= length-1; x++){
                    if(x== length-1){
                        //The last data Added
                        temp = temp + currentArrayData[x];

                    }else{
                        //The still havent reach the last data
                        temp = temp + currentArrayData[x];
                        temp = temp + (finalDataConnector);
                    }
                }
            }

        }else {
            temp ="ID:e:ER:00:00@";
            System.out.println("Data beschädigt");
        }
        return temp;
    }
    
    /**
     * 
     * @return String of speed
     */
    public int getSpeedInt(){
         String temp = currentArrayData[speedPosition];
         int speed = Integer.parseInt(temp);
         return speed;
    }
    
    public int getSteeringInt(){
        String temp = currentArrayData[steeringPosition];
        int steering = Integer.parseInt(temp);
        return steering;
    }
    
    public double getSpeedDouble(){
         String temp = currentArrayData[speedPosition];
         Double speed = Double.parseDouble(temp);
         return speed;
    }
    
    public double getSteeringDouble(){
        String temp = currentArrayData[steeringPosition];
        double steering = Double.parseDouble(temp);
        return steering;
    }

//    public void csv(String A_ID) throws IOException {
//        ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
//        strat.setType(DataBank.class);
//        String[] columns = new String[]{"ID", "Client Zeit", "Arduino Zeit", }; // The Field to Bind with Java Bean
//        strat.setColumnMapping(columns);
//
//        CsvToBean csv = new CsvToBean();
//        String csvFilename = "DataBank.csv";
//        CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
//
//        List list = csv.parse(strat , csvReader);
//        for (Object object : list){
//            ID id1 = (ID) object;
//
//        }
//
//
//
//
//
//    }

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
