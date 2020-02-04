/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

// Import Essentials
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.climbingSubsystem;
import frc.robot.subsystem.drivingSubsystem;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class Robot extends TimedRobot {
  Joystick stick;
  //VictorSPX[] shooter;
  drivingSubsystem drive;
  climbingSubsystem climb;

  VictorSPX left_front;
  VictorSPX left_rear;
  VictorSPX right_front;
  VictorSPX right_rear;


  @Override
  public void robotInit() {
    // Initialize stick as a Joystick 
    stick = new Joystick(0);

    // Initialize subsystems
    drive = new drivingSubsystem();
    climb = new climbingSubsystem();

    // Initialize motors
    left_front=new VictorSPX(3);
    left_rear=new VictorSPX(2);
    right_front=new VictorSPX(0);
    right_rear=new VictorSPX(1);

    // Initialize joystick
    stick = new Joystick(0);

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
<<<<<<< HEAD
    //drive.movement();
=======
    drive.movement();


>>>>>>> master

    // Climbing/
    climb.manualClimbing();

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
}