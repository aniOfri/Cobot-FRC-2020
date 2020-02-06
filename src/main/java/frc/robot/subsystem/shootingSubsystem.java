package frc.robot.subsystem;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;

public class shootingSubsystem extends SubsystemBase {

    // Shooting motors
    //TalonSRX right_shooter;
    //TalonSRX left_shooter;

    // Siding and lifting
    VictorSP siding;
    //TalonSRX lifting;

    // Siding and lifting value
    double cX;
    double cY;
    double imgW;
    double imgH;

    // Ball Slotting
    //TalonSRX slots[];

    // Limit Switch
    //DigitalInput[] sensors;

    // Joystick
    Joystick stick;

    public shootingSubsystem() {
    // Initialize shooters
    //right_shooter = new TalonSRX(6);
    //left_shooter = new TalonSRX(7);

    // Initialize siding and lifting
    siding = new VictorSP(0);
    //lifting = new TalonSRX(9);

    /*
    // Initialize slot motors
    slots = new TalonSRX[4];
    slots[0] = new TalonSRX(10);
    slots[1] = new TalonSRX(11);
    slots[2] = new TalonSRX(12);
    slots[3] = new TalonSRX(13);

    // Initialize sensors
    sensors = new DigitalInput[5];
    sensors[0] = new DigitalInput(0);
    sensors[1] = new DigitalInput(1);
    sensors[2] = new DigitalInput(2);
    sensors[3] = new DigitalInput(3);
    sensors[4] = new DigitalInput(4);*/

    // Initialize joystick
    stick = new Joystick(0);
    }

    public void shooting(){
        // Manual shooting
        //manualShooting();

        // Siding and lifting
        sidingAndLifting();
    }

    private void manualShooting(){
    // If trigger is pressed
    if (stick.getTrigger())
        spin(1);
    // Else, set the motors to 0
    else
        spin(0);
    }

    private void sidingAndLifting(){
        if (SmartDashboard.getBoolean("found", false)){
            cX = SmartDashboard.getNumber("cX", 0);
            imgW = SmartDashboard.getNumber("imgW", 1);
            if (cX < imgW/2 - imgW/2*0.1 ||
                cX > imgW/2 + imgW/2*0.1)
                siding.set(0.8 * (imgW/2 - cX) / imgW);
            else
                siding.set(0);
        }
        else
            siding.set(0);
    }

    /* Set motor on 1 or 0, Depending on the mode value*/
    private void spin(int mode){
        //right_shooter.set(ControlMode.PercentOutput, mode);
        //left_shooter.set(ControlMode.PercentOutput, mode);
    }

}

