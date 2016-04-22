/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udsoft.com.universaldriveserver;

import com.udsoft.com.universaldriveserver.experiment.RemoteLogic;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author udw
 */
public class Beschleunigung implements Runnable{
 
    //Data collection
     private final BlockingQueue<String> dataqueue = 
         new LinkedBlockingQueue<String>();  
     
    //Condition 
    boolean inStatic;      //not Moving 
    boolean isMovingForward;// is moving Forward direction.
   
    //store value
    int previousSpeed = 0;
    
    //Setting the pin naming.
    Pin forwardPin = RaspiPin.GPIO_01;
    Pin backwardPin = RaspiPin.GPIO_26;
    
    
    
    final GpioController gpio = GpioFactory.getInstance();
    
    /**
     *  Setting the GPIO pin to PWM mode to respective GPIO Pins.
     *  Use this variables to set PWM
     *  example forward.setPWM
     */
    final GpioPinPwmOutput forward = 
            gpio.provisionPwmOutputPin(forwardPin);
    final GpioPinPwmOutput backward = 
            gpio.provisionPwmOutputPin(backwardPin);
   
    /**
     * initialise the Logic function.
     */
    public void init(){
        this.inStatic = true;
        this.isMovingForward = false;   
    }
    
     public void setData(String data){
        //sending the data to the BlockingQueue.
        dataqueue.offer(data);
    }
     
     /**
     * 
     * @return 
     */
    private int extractDataFromBlockingQueue(){
        int speedData = 0;
        
        try {
            String temp = dataqueue.take();
            speedData = Integer.parseInt(temp);
        } catch (InterruptedException ex) {
            Logger.getLogger(Lengkung.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.out.println("Error in getDataFromBlockingQueue function");
        }
        
        
        return speedData;
    }
    
    private void Brake(){
        inStatic = true;
        /**
         * forward and backward is set to zero 
         * so that the the previous PWM value 
         * is not stored in the respective pin. 
         * IF this is not taken care , there 
         * will be in accuracy in the action given to the car.
         */
        forward.setPwm(0);
        backward.setPwm(0);
        /**
         * Thread is set to be inactive for 2 seconds for the complete braking 
         * to take place the time is due to how the hardware works and giving 
         * the time to be execute b4 adding more 
         * data to be execute while the hardware haven't fully 
         * done the job given.
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
     private void Forward(int speed){
        forward.setPwm(speed);
        isMovingForward = true;
        inStatic = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
    
    }
     
     private void Reverse(int speed){
        backward.setPwm(speed);
        isMovingForward = false;
        inStatic = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
     
         /**
     * This MainLogic is the main Brain for all the logic decision.
     * The rules of the condition is 
     * 1. Braking takes 2 second interval to fully executed
     * 2. Only after the fully braked the 
     *    another condition of forward or Reverse can be executed.. 
     * 3. While braking the steering is still free to function
     * 4. For Steering the only one condition need to be checked
     *    isTurningRight. If the condition to be changed the previous 
     *    active GPIO should be set to 0 and activate the New GPIO.
     * @param speed positive if forward , negative if reverse.
     * @param steering positive if Right, negative if Left
     */
    private void Logic(int speed){
        
        /**
         *                Condition of Speed
         * 1.Check if the car in static or non Static condition.
         * 2.If in static , check if the speed is positive or Negative 
         *      - Positive value will trigger Forward Motion;
         *      - Negative value will trigger Reverse Motion;
         * 3.If car in a motion , 
         *      check if the previous Value of speed is equal to current speed.
         *  --> If it is same do not change anything.
         * 
         * 3.1 If the value is not equal then check for direction of the Motion
         * --->>If car is in Forward Motion. 
         *  --> check if the current speed is positive or negative.
         *  --> If it is Positive then Trigger Forward Motion with the speed;
         *  --> If it is negative then Trigger Brake ;
         * 
         * -->>If car is in Backward Motion.
         *  --> Check if the current speed is positive or negative.
         *  --> If it it Negative then Trigger Backward Motion with the speed;
         *  --> If it is Positive then Trigger Brake;
         * 
         * 4. Save the current speed as a new value for previous speed.
         * 
         */
        if (inStatic) {
            if(speed > 0){
                Forward(speed);
            }else if(speed < 0){
                Reverse(-speed);
            }else{
                System.out.println("Auto Noch in Ruhe");
            }

        }else{
            /**
             * Code to execute if the Car already in non Static Condition
             */
            if(previousSpeed != speed){
                if(isMovingForward){
                    /**
                     * Code to execute if the car is already in Forward motion
                     */
                    if(speed >0){
                        Forward(speed);
                    }else{
                        Brake();
                    }
                }else{
                    /**
                     * Code to execute if the car is already in Reverse Motion
                     */
                    if(speed < 0){
                        Reverse(-speed);
                    }else{
                        Brake();
                    }
                }
            }
        }
        previousSpeed = speed;
    }
    
    private void inAction(){
        int speed = extractDataFromBlockingQueue();
        Logic(speed);
    }
    
    
   
    @Override
    public void run() {
        System.out.println("Started Speed Thread");
        init();
        inAction();
    }
    
}
