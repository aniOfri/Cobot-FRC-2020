package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Joystick;

public class shootingSubsystem extends SubsystemBase {

    // Declaration
    TalonSRX right_shooter;
    TalonSRX left_shooter;

    TalonSRX siding;
    TalonSRX lifting;

    DigitalInput[] sensors;

    Joystick stick;

    public shootingSubsystem() {
    // Initialize shooters
    right_shooter = new TalonSRX(6);
    left_shooter = new TalonSRX(7);

    // Initialize siding and lifting
    siding = new TalonSRX(8);
    lifting = new TalonSRX(9);

    // Initialize sensors
    sensors = new DigitalInput[5];
    sensors[0] = new DigitalInput(0);
    sensors[1] = new DigitalInput(1);
    sensors[2] = new DigitalInput(2);
    sensors[3] = new DigitalInput(3);
    sensors[4] = new DigitalInput(4);

    // Initialize joystick
    stick = new Joystick(0);
    }

    public void shooter(){
    // If trigger is pressed
    if (stick.getTrigger())
        spin(1);
    // Else, set the motors to 0
    else
        spin(0);
    }

    private void spin(int mode){
        right_shooter.set(ControlMode.PercentOutput, mode);
        left_shooter.set(ControlMode.PercentOutput, mode);
    }

}

