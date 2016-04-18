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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author darwin This is the Main the whole Process of the UDResearch Lab 1. As
 * the Main starts it produce 2 Threads which will be - udServer - DataAnalyse
 *
 */
public class udReseachBrain {

    //Initialising Server --> havent start running 
    public static void main(String[] args) {

        boolean distributor_available;  //Indicate if the data from can be parcel 
        String data;                    //Current Data from the Client is saved.
        String idNummer = "";
        int speedInt = 0;
        int steeringInt = 0;
        double speedDouble = 0.0;
        double steeringDouble = 0.0;
        String command = "";
        long recieveTime;
        
        UdServer server = new UdServer(8080);
        DataAnalyse dataAnalyse = new DataAnalyse(":", "=", 0, 1, 2, 3, true, 4);
        if (server.isConnected()) {
             data = server.readClient();
             dataAnalyse.setData(data);
             recieveTime = System.currentTimeMillis();
             idNummer = dataAnalyse.getID();
             command = dataAnalyse.getCommand();
             if(command.equals("MC")){
               
                 
             }else if ( command.equals("CM")){
                 speedDouble = dataAnalyse.getSpeedDouble();
                 steeringDouble = dataAnalyse.getSteeringDouble();
             }
             
        }       

    }

}
