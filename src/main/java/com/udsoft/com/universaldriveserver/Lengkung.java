/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udsoft.com.universaldriveserver;

import com.udsoft.com.universaldriveserver.experiment.RemoteLogic;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author udw
 */
public class Lengkung implements Runnable {

    /**
     * This is the part of the Remote logic which ensure the the Lengkung class 
     * is carried out in a new Thread.
     */
    
    //Data collection 
    private final BlockingQueue<String> dataqueue = 
            new LinkedBlockingQueue<String>();
    
    
    /**
     * Private Variable to make logical decision with the condition of the car.
     */
    private boolean isTurningRight;
    private boolean isSteeringStraight;
    private int previousSteering;
    
    final GpioController gpio = GpioFactory.getInstance();
    
    Pin leftSteeringPin = RaspiPin.GPIO_23;
    Pin rightSteeringPin = RaspiPin.GPIO_24;
     
    final GpioPinPwmOutput leftSteering = 
            gpio.provisionPwmOutputPin(leftSteeringPin);
    final GpioPinPwmOutput rightSteering = 
            gpio.provisionPwmOutputPin(rightSteeringPin);
    
    
    
    
    public void init(){
        this.isTurningRight = false;
        this.isSteeringStraight = true;
        
    }
    
    /**
     * @param data :this is the data from the DataAnalyse only for the steering
     */
    public void setData(String data){
        //sending the data to the BlockingQueue.
        dataqueue.offer(data);
    }
    
    /**
     * 
     * @return 
     */
    private int extractDataFromBlockingQueue(){
        int lengkungData = 0;
        
        try {
            String temp = dataqueue.take();
            lengkungData = Integer.parseInt(temp);
        } catch (InterruptedException ex) {
            Logger.getLogger(Lengkung.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.out.println("Error in getDataFromBlockingQueue function");
        }
        
        
        return lengkungData;
    }
    
    /**
     * 
     * @param steering 
     */
     private void TurnLeft(int steering){
        leftSteering.setPwm(steering);
        isTurningRight = false;
        isSteeringStraight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
     /**
      * 
      * @param steering 
      */
    private void TurnRight(int steering){
        rightSteering.setPwm(steering);
        isTurningRight = true;
        isSteeringStraight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * 
     */
     private void SteeringStraight(){
        leftSteering.setPwm(0);
        rightSteering.setPwm(0);
        isSteeringStraight = true;
        isTurningRight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
 
    /**
     * 
     * @param lengkung 
     */
    private void Logic(int steering){
        /**
         *               Condition of Steering
         * 1. Check if the previous steering Value is not equal to steering.
         * 2. Check if the steering value is positive or negative.
         * 3. Check if the Steering condition was in left or right Turning.
         * 4. --> 
         */
        if (previousSteering != steering){
            if(isSteeringStraight){
               if(steering > 0){
                   TurnRight(steering);
               }else if (steering < 0){
                   TurnLeft(-steering);
               }else{
                   SteeringStraight();
               }
            }else{
                if(isTurningRight){
                    if(steering > 0){
                        TurnRight(steering);
                    }else if (steering < 0){
                        SteeringStraight();
                    }
                }else{
                    if(steering > 0){
                        SteeringStraight();
                    }else{
                        TurnLeft(steering);
                    }
                }
            }
        }
        previousSteering = steering;
    }
    
    private void inAction(){
        int lengkung = extractDataFromBlockingQueue();
        Logic(lengkung);
    }
    
    
    @Override
    public void run() {
         System.out.println("Started Lengkung Thread");
         init();
         inAction();
    }
}
