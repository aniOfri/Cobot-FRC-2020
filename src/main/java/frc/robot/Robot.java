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
DigitalInput [name] = new DigitalInput(18); # Lifting (Bottom)
DigitalInput [name] = new DigitalInput(19); # Lifting (Top)
DigitalInput [name] = new DigitalInput(10); # Balancing (Left)
DigitalInput [name] = new DigitalInput(11); # Balancing (Right)
DigitalInput [name] = new DigitalInput(20); # Siding (Right)
DigitalInput [name] = new DigitalInput(21); # Siding (Left)

    Ultrasonic:
      Cable 1:
Ultrasonic [name] = new Ultrasonic(0, 1); # Top Ultrasonic (Trig:Black, Echo:Blue)
Ultrasonic [name] = new Ultrasonic(2, 3); # Middle Ultrasonic (Trig:Purple, Echo:White)
Ultrasonic [name] = new Ultrasonic(4, 5); # Bottom Ultrasonic (Trig:Yellow, Echo:Orange)
      Cable 2:
Ultrasonic [name] = new Ultrasonic(6, 7); # Shooter top Ultrasonic (Trig:Green, Echo:White)
Ultrasonic [name] = new Ultrasonic(8, 9); # Shooter bottom Ultrasonic (Trig:Blue, Echo:Yellow)
      Cable 3:
Ultrasonic [name] = new Ultrasonic(14, 15); # Right height Ultrasonic Ultrasonic (Trig:Green, Echo:Yellow)
Ultrasonic [name] = new Ultrasonic(17, 16); # Left height Ultrasonic (Trig:Orange, Echo:Blue)


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
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystem.ballFeedingSubsystem;
import frc.robot.subsystem.ballFeedingVer2Subsystem;
import frc.robot.subsystem.climbingSubsystem;
import frc.robot.subsystem.drivingSubsystem;
import frc.robot.subsystem.shootingSubsystem;

public class Robot extends TimedRobot {

  UsbCamera usbCam;
  UsbCamera usbCam2;
  drivingSubsystem drive;
  climbingSubsystem climb;
  shootingSubsystem shoot;
  ballFeedingVer2Subsystem ballFeed;

  // Ball Feeding or Climbing
  boolean isClimbing;

  @Override
  public void robotInit() {
    // Initialize subsystems
    shoot = Constants.MISC.shoot;
    drive = new drivingSubsystem();
    climb = new climbingSubsystem();
    ballFeed = new ballFeedingVer2Subsystem();

    // Ball Feeding or Climbing
    isClimbing = false;


    // Initialize and configure usbCam
    usbCam = CameraServer.getInstance().startAutomaticCapture();
    usbCam2 = CameraServer.getInstance().startAutomaticCapture();
    usbCam.setResolution(320,240);
    usbCam2.setResolution(320,240);
    usbCam2.setFPS(15);
    }

  @Override

  public void robotPeriodic() {
    // Toggle
    if (Constants.MISC.joystick_a.getRawButtonPressed(11))
      isClimbing = !isClimbing;

    // Driving
    drive.driving();

    if (!isClimbing) {
      // Ball Feeding
      ballFeed.ballFeeding();
      climb.auto_balance = false;
      climb.spinR(0);
      climb.spinL(0);
      SmartDashboard.putBoolean("autoBalance?", false);
    }
    else
      // Climbing
      climb.climbing();

    // Shooting
    shoot.shooting();

    // SmartDashboard
    SmartDashboard.putBoolean("isClimbing?", isClimbing);
  }

  @Override
  public void autonomousInit() {
    CommandScheduler.getInstance().schedule(new SequentialCommandGroup(drive.autonomous(), new InstantCommand(new Runnable() {
      @Override
      public void run() {
        drive.stop();
      }
    })));
  }


  public void autonomousPeriodic(){
    CommandScheduler.getInstance().run();
  }
}