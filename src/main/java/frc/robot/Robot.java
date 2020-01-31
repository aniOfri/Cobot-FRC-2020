/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

// Import Essentials
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;

// Import Camera
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;

// Import Image Processing
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;


public class Robot extends TimedRobot {
  Joystick stick;
  VictorSP[] motors;
  VictorSP[] shooter;
  VideoCapture capture;
  UsbCamera cam;

  /*
    Initialize all variables at deploy.
    
    @param stick: Joystick class which 
      gets inputs from the first Joystick
      connected to the machine.
      
    @param motors: VictorSP class array
      which sets the motors of the shooting
      mechanism, movement and camera's 
      motor as items in an array.
    
    @param usbCam: UsbCamera class which
      stores the CameraServer of the camera
      and streams it to the SmartDashboard or so.
  */
  @Override
  public void robotInit() {
    // Initialize stick as a Joystick 
    stick = new Joystick(0);

    // Initialize motors as a two-item array
    motors = new VictorSP[7];
    // Set item one as motor one
    motors[0] = new VictorSP(0);
    // Set item two as motor two
    motors[1] = new VictorSP(1);
    // Set item three as motor three
    motors[2] = new VictorSP(2);
    // Set item four as motor four
    motors[3] = new VictorSP(3);
    // Set item four as camera motor
    motors[4] = new VictorSP(4);
    
    /* Initialize motors as a two-item array 
    for shooting mechanism

    // Declaration
    shooter = new VictorSP[2];
    // Initialization
    shooter[0] = new VictorSP(5);
    shooter[1] = new VictorSP(6);*/
    }
  
  /*
    Periodic function which triggers repeatedly
    while the teleOperated state is enabled.

    @param throttle: double class which stores
      the state of the Joystick's throttle. 
  */
  @Override
  public void robotPeriodic() {
    double throttle;
    boolean trigger;

    // Movement
    movement();

    // Get the Joystick's throttle's state.
    throttle = stick.getThrottle();
    trigger = stick.getTrigger();

    // SmartDashboard
    SmartDashboard.putNumber("Throttle: ", throttle);
    SmartDashboard.putBoolean("Trigger: ", trigger);


    /*
    // If trigger is pressed
    if (stick.getTrigger()){
      motors[2].set(-throttle);
      //shooter[1].set(throttle);
    }
    // Else, set the motors to 0
    else{
      motors[2].set(0);
      //shooter[1].set(0);
    }*/
    }

    public void movement(){
        // Declare variables
        double maxLim,
        sqrt,
        xVal,
        yVal,
        finalL = 0,
        finalR = 0;

        // Get Joystick's input
        xVal = stick.getX();
        yVal = -stick.getY();

        // Algorithm Beep Boop *-*
        if (yVal > 0.2 || yVal < -0.2){
            int negFix = yVal < -0.2 ? -1 : 1,
                negPos = (int)(yVal / Math.abs(yVal));

            maxLim = 0.7 + (-stick.getThrottle()  + 1) / 2 * 0.3;
            sqrt = Math.sqrt(xVal * xVal + yVal * yVal);

            finalL = sqrt * maxLim * negPos;
            finalR = (sqrt + (0.8 * xVal * -negPos * negFix)) * maxLim * -negPos;
        }
        else{
            finalL = xVal;
            finalR = xVal;
        }

        SmartDashboard.putNumber("FinalR", finalR);
        SmartDashboard.putNumber("FinalL", finalL);

        // Sending values to motor(0), motor(1)
        motors[0].set(finalR);
        motors[1].set(finalR);
        motors[2].set(finalL);
        motors[3].set(finalL);
    }
}