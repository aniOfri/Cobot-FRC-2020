/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

// Import Essentials
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;



public class Robot extends TimedRobot {
  Joystick stick;
  VictorSPX[] motors;
  //VictorSPX[] shooter;
  TalonSRX[] climb;
  VictorSP balance;

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

    
    // Initialize motors as a four-item array
    motors = new VictorSPX[4];
    // Initialization
    motors[0] = new VictorSPX(0);
    motors[1] = new VictorSPX(1);
    motors[2] = new VictorSPX(2);
    motors[3] = new VictorSPX(3);
    
    // Initialize climb as a three-item array
    climb = new TalonSRX[2];
    // Initialization
    climb[0] = new TalonSRX(0);
    climb[1] = new TalonSRX(1);
    balance = new VictorSP(0);

    /* 
    //Initialize shooter as a two-item array 
    shooter = new VictorSP[2];
    // Initialization
    shooter[0] = new VictorSP(5);
    shooter[1] = new VictorSP(6);*/
    }

  @Override
  public void robotPeriodic() {
    // Movement
    movement();

    if(stick.getRawButton(4)){
      climb[0].set(ControlMode.PercentOutput, 0.5);
      climb[1].set(ControlMode.PercentOutput, 0.5);
    }
    else if(stick.getRawButton(4) && stick.getTop()){
      climb[0].set(ControlMode.PercentOutput, -0.5);
      climb[1].set(ControlMode.PercentOutput, -0.5);
    }
    else{
      climb[0].set(ControlMode.PercentOutput, 0);
      climb[1].set(ControlMode.PercentOutput, 0);
    }

    if(stick.getRawButton(4))
      balance.set(1);
    else if(stick.getRawButton(4) && stick.getTop())
      balance.set(-1);
    else
      balance.set(0);

    // SmartDashboard
    SmartDashboard.putBoolean("Trigger: ", stick.getTrigger());

    /*
    // If trigger is pressed
    if (stick.getTrigger()){
      shooter[0].set(1);
      shooter[1].set(1);
    }
    // Else, set the motors to 0
    else{
      shooter[0].set(0);
      shooter[1].set(0);
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

            maxLim = 0.85;
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
        motors[0].set(ControlMode.PercentOutput, finalR);
        motors[1].set(ControlMode.PercentOutput, finalR);
        motors[2].set(ControlMode.PercentOutput, finalL);
        motors[3].set(ControlMode.PercentOutput, finalL);
    }
}