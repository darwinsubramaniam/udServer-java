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
public class DataBank implements Runnable {
    private String ID;
    private String ClientTime;
    private String ArduinoTime;

    public String getID(){
        return ID;
    }

    public void setID(String ID){
        this.ID = ID;
    }

    public String getClientTime(){
        return ClientTime;
    }

    public void setClientTime(String ClientTime){
        this.ClientTime = ClientTime;
    }


    private String getArduinoTime(){
        return ArduinoTime;
    }

    public void setArduinoTime(String ArduinoTime){
        this.ArduinoTime = ArduinoTime;
    }

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
