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
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.VictorSP;



public class Robot extends TimedRobot {
  Joystick stick = new Joystick(0);
  /*VictorSPX V1 = new VictorSPX(0);
  VictorSPX V2 = new VictorSPX(1);
  VictorSPX V3 = new VictorSPX(2);
  VictorSPX V4 = new VictorSPX(3);

  SpeedControllerGroup Left = new SpeedControllerGroup(V1,V2);
  SpeedControllerGroup Right = new SpeedControllerGroup(V3,V4);
  DifferentialDrive drive = new DifferentialDrive(Left, Right);*/

  VictorSPX[] motors;
  VictorSP[] climb;

  //VictorSPX[] shooter;

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
    motors = new VictorSPX[7];
    // Set item one as motor one
    motors[0] = new VictorSPX(0);
    // Set item two as motor two
    motors[1] = new VictorSPX(1);
    // Set item three as motor three
    motors[2] = new VictorSPX(2);
    // Set item four as motor four
    motors[3] = new VictorSPX(3);
    // Set item four as camera motor
    //motors[4] = new VictorSP(4);
    
    climb = new VictorSP[3];
    climb[0] = new VictorSP(0);
    climb[1] = new VictorSP(1);
    climb[2] = new VictorSP(2);

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

    //drive.arcadeDrive(stick.getRawAxis(1),stick.getRawAxis(0));
    
    if(stick.getRawButton(5)){
      climb[0].set(0.5);
      climb[1].set(0.5);
    }
    else if(stick.getRawButton(3)){
      climb[0].set(-0.5);
      climb[1].set(-0.5);
    }
    else{
      climb[0].set(0);
      climb[1].set(0);
    }

    if(stick.getRawButton(6)){
      climb[2].set(1);
    }
    else if(stick.getRawButton(4)){
      climb[2].set(-1);
    }
    else{
      climb[2].set(0);
     
    }
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
        
        SmartDashboard.putNumber("finalR", finalR);
        SmartDashboard.putNumber("finalL", finalL);

        motors[0].set(ControlMode.PercentOutput, finalR);
        motors[1].set(ControlMode.PercentOutput, finalR);
        motors[2].set(ControlMode.PercentOutput, finalL);
        motors[3].set(ControlMode.PercentOutput, finalL);
    }
}