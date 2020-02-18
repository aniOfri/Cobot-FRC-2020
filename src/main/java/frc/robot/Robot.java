/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Motors and sensors chart:
/*
  Joystick:
Joystick [name] = new Joystick(0); # USB connected joystick

  USB Camera:
UsbCamera [name] = CameraServer.getInstance()
    .startAutomaticCapture(); # USB0 connected camera to the Roborio

    Wheels:
VictorSPX [name] = new VictorSPX(0); # Right CIM(1/2)
VictorSPX [name] = new VictorSPX(1); # Right CIM(2/2)
VictorSPX [name] = new VictorSPX(2); # Left CIM(1/2)
VictorSPX [name] = new VictorSPX(3); # Left CIM (2/2)

    Siding and lifting (Aim):
      <!- USE LIMIT SWITCHES -!>
TalonSRX [name] = new TalonSRX(5); # Siding mechanism (Right - Left)
TalonSRX [name] = new TalonSRX(4); # Lifting mechanism (Up - Down)

    Shooting:
VictorSP [name] = new VictorSP(0); # Right Mini-CIM (Shooting mechanism)
VictorSP [name] = new VictorSP(1); # Left Mini-CIM (Shooting mechanism)
VictorSP [name] = new VictorSP(2); # Pushing Mechanism

    Climbing:
VictorSP [name] = new VictorSP(3); # Elevator mechanism
VictorSP [name] = new VictorSP(4); # Balancing mechanism
VictorSP [name] = new VictorSP(8); # Climbing mechanism (Right)
VictorSP [name] = new VictorSP(9); # Climbing mechanism (Left)

    Slots (Ball storing)
VictorSP [name] = new VictorSP(5); # Bottom slot motor
VictorSP [name] = new VictorSP(6); # Middle slot motor
VictorSP [name] = new VictorSP(7); # Top slot motor(s)

    Limit Switches:
DigitalInput [name] = new DigitalInput(1); # Lifting (Bottom)
DigitalInput [name] = new DigitalInput(2); # Lifting (Top)
DigitalInput [name] = new DigitalInput(3); # Balancing (Left)
DigitalInput [name] = new DigitalInput(4); # Balancing (Right)
DigitalInput [name] = new DigitalInput(5); # Siding (Right)
DigitalInput [name] = new DigitalInput(6); # Siding (Left)
 */

// Joystick buttons' purposes
/*
  > .getY() - Driving / Manual Siding
  > .getX() - Driving / Manual Lifting

  > .getTop() - Shooting
  > .getTrigger - Pushing

  > .getRawButton(7) - Reloading
  > .getRawButton(8) - Manual Siding-Lifting/Driving toggle

  > .getRawButton(3) - Climbing Motors (Clockwise)
  > .getRawButton(5) - Climbing Motors (Counter-Clockwise)

  > .getRawButton(4) - Balancing (Right)
  > .getRawButton(6) - Balancing (Left)
 */

package frc.robot;

// Import Essentials
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.cameraserver.CameraServer;
import frc.robot.subsystem.climbingSubsystem;
import frc.robot.subsystem.drivingSubsystem;
import frc.robot.subsystem.shootingSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  Joystick stick;
  UsbCamera usbCam;
  drivingSubsystem drive;
  climbingSubsystem climb;
  shootingSubsystem shoot;


  @Override
  public void robotInit() {
    // Initialize stick as a Joystick 
    stick = new Joystick(0);

    // Initialize subsystems
    drive = new drivingSubsystem();
    climb = new climbingSubsystem();
    shoot = new shootingSubsystem();

    usbCam = CameraServer.getInstance().startAutomaticCapture();
    usbCam.setResolution(320,240);
    }

  @Override
  public void robotPeriodic() {
    // Movement
    if (!shoot.isManual())
      drive.movement();

    // Climbing
    //climb.climbing();

    // Shooting
    shoot.shooting();
  }
}