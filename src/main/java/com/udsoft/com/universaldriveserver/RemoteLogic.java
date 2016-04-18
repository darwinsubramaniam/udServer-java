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
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RemoteLogic implements Runnable 
{
    //Condition 
    boolean inStatic;      //not Moving 
    boolean isMovingForward;// is moving Forward direction.
    boolean isTurningRight; // is making right turning.
    boolean isSteeringStraight;
    //store value
    int previousSpeed = 0;
    int previousSteering = 0;
    // store value when using gyroscope
    double double_previousSpeed = 0.0;
    double double_previousSteering = 0.0;
    
    //Setting the pin naming.
    Pin forwardPin = RaspiPin.GPIO_01;
    Pin backwardPin = RaspiPin.GPIO_26;
    Pin leftSteeringPin = RaspiPin.GPIO_23;
    Pin rightSteeringPin = RaspiPin.GPIO_24;
    
    
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
    final GpioPinPwmOutput leftSteering = 
            gpio.provisionPwmOutputPin(leftSteeringPin);
    final GpioPinPwmOutput rightSteering = 
            gpio.provisionPwmOutputPin(rightSteeringPin);
    
    public void Test() throws InterruptedException{
        //Create gpio controller 
        
        
        //provision gpio pin #01 as an output pin and turn on
        //final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(forwardPin,"MyLed",PinState.HIGH);
        
        //Set shutdown state for this pin
        //pin.setShutdownOptions(true, PinState.LOW);
        
        final GpioPinPwmOutput pin = gpio.provisionPwmOutputPin(forwardPin);
        
        
        for(int x = 0 ; x <= 1024; x++){
            pin.setPwm(x);
            System.out.println(x);
            try{
                Thread.sleep(100);
            }catch(Exception e){
                
            }
        } 
       
      
        
        //Stop all GPIO activity/threads by shutting down GPIO controller
        //this method will forcefully shutdown all the GPIO monitoring threads and scheduled tasks
        gpio.shutdown();
        
        
    }
    
    /**
     * Initialize the Logic function.
     */
    public void init(){
        this.inStatic = true;
        this.isMovingForward = false;
        this.isTurningRight = false;
        this.isSteeringStraight = true;
        
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
    
    private void SteeringStraight(){
        leftSteering.setPwm(0);
        rightSteering.setPwm(0);
        isSteeringStraight = true;
        isTurningRight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
    
    private int DoubleToInt(double speedInDouble){
        double temp_double = speedInDouble* 100;
        int result = (int)temp_double;
        return result;
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
    private void MainLogic(int speed, int steering){
        
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
    
    
    private void MainLogic(double speed, double steering){
        
        int speed_int = DoubleToInt(speed);
        int steering_int = DoubleToInt(steering);
        
        if(isMovingForward && speed > 0){
            Forward(speed_int);
        }else if (isMovingForward && speed <= 0){
            Brake();
        }else if (isMovingForward && speed < 0){
            Brake();
            Reverse(speed_int);
        }
        
    }

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
