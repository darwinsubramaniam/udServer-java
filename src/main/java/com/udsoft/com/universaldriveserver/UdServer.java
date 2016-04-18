/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udsoft.com.universaldriveserver;

/**
 *@author darwin
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class UdServer implements Runnable {

    //setting the ServerSocket as t
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean isConnected = false;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String oldinput = "";

    public UdServer(int portNumber){

        if (portNumberManager(portNumber)){

            try {
                serverSocket = new ServerSocket(portNumber);
                try {
                    clientSocket = serverSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Denied connection from a client");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("Error in Port number");

        }



    }


    private boolean portNumberManager(int port){
        boolean succes;

        //Check if the interger send is consist of 4 digit number
        if(port >= 1000 && port<= 9999){
            succes = true;
        }else{
            System.out.println();
            System.out.println("Port Number should be consist of 4 digit number only");
            System.out.println();
            succes = false;
        }


        return succes;
    }

    public String readClient(){
        String dataReadable = " ";
        if (!serverSocket.isClosed()){
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));// in is a java.io.BufferReader Type
                dataReadable = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("BufferedReader getting error");
            }
        }else{
            System.out.println("Server socket is closed");
        }
        return dataReadable;
    }

    public void writeClient(String data){
        if(!serverSocket.isClosed()){
            try {
                out = new PrintWriter(clientSocket.getOutputStream(),true);
                out.println(data);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(" PrintWriter giving error");
            }
        }else {
            System.out.println("Server socket is closed");
        }
    }

    public boolean isConnected(){

        isConnected = clientSocket.isConnected();
        return isConnected;

    }
    
    public boolean newData(){
        boolean newdata;
        String readClient = readClient();
        if(!readClient.equals(oldinput)){
            newdata = true;
        }else newdata = false;
        
        return newdata;
        
    }

    @Override
    public void run() {
        
    }

    
}
