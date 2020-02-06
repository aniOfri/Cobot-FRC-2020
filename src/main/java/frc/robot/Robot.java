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
import edu.wpi.first.wpilibj.VictorSP;
import frc.robot.subsystem.climbingSubsystem;
import frc.robot.subsystem.drivingSubsystem;
import frc.robot.subsystem.shootingSubsystem;

public class Robot extends TimedRobot {

  Joystick stick;
  //drivingSubsystem drive;
  //climbingSubsystem climb;
  shootingSubsystem shoot;


  @Override
  public void robotInit() {
    // Initialize stick as a Joystick 
    stick = new Joystick(0);

    // Initialize subsystems
    //drive = new drivingSubsystem();
    //climb = new climbingSubsystem();
    shoot = new shootingSubsystem();
    }

  @Override
  public void robotPeriodic() {
    // Movement
    //drive.movement();

    // Climbing
    //climb.climbing();

    // Shooting
    shoot.shooting();
    }
}