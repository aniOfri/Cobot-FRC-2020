/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

// Import Essentials
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import frc.robot.subsystem.climbingSubsystem;
import frc.robot.subsystem.drivingSubsystem;

public class Robot extends TimedRobot {
  Joystick stick;
  //VictorSPX[] shooter;
  drivingSubsystem drive;
  climbingSubsystem climb;


  @Override
  public void robotInit() {
    // Initialize stick as a Joystick 
    stick = new Joystick(0);

    // Initialize subsystems
    drive = new drivingSubsystem();
    climb = new climbingSubsystem();

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
    drive.movement();

    // Climbing
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