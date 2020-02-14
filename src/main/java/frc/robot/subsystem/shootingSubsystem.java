package frc.robot.subsystem;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class shootingSubsystem extends SubsystemBase {

    // Shooting motors
    VictorSP right_shooter;
    VictorSP left_shooter;

    // Siding and lifting
    TalonSRX siding;
    TalonSRX lifting;

    // Siding and lifting value
    double cX;
    double cY;
    double imgWidCenter;
    double imgHiCenter;
    double outputX;
    double outputY;

    // Ball Slotting
    VictorSP slots[];

    // Ultrasonics
    Ultrasonic[] sensors;

    // LimitSwitch
    DigitalInput[] lmtSwitch;

    // Joystick
    Joystick stick;

    public shootingSubsystem() {
    // Initialize shooters
    //right_shooter = new VictorSP(4);
    //left_shooter = new VictorSP(5);

    // Initialize siding and lifting
    siding = new TalonSRX(5);
    lifting = new TalonSRX(4);


    /*// Initialize slot motors
    slots = new VictorSP[4];
    slots[0] = new VictorSP(3);
    slots[1] = new VictorSP(4);
    slots[2] = new VictorSP(5);
    slots[3] = new VictorSP(6);

    // Initialize sensors
    sensors = new Ultrasonic[5];
    sensors[0] = new Ultrasonic(0 ,0);
    sensors[1] = new Ultrasonic(1,1);
    sensors[2] = new Ultrasonic(2,2);
    sensors[3] = new Ultrasonic(3,3);*/

    // Initialize Limit Switch
    lmtSwitch = new DigitalInput[4];
    lmtSwitch[0] = new DigitalInput(1); // Lifting (Min)
    lmtSwitch[1] = new DigitalInput(2); // Lifting (Max)
    lmtSwitch[2] = new DigitalInput(5); // Siding (Right)
    lmtSwitch[3] = new DigitalInput(6); // Siding (Left)

    // Initialize joystick
    stick = new Joystick(0);
    }

    public void shooting(){
        // Manual shooting
        manualShooting();

        // Siding and lifting
        //sidingAndLifting();

        // Reloading
        //reloading();
    }

    private void manualShooting(){
        // If trigger is pressed
        if (stick.getTrigger()) {
            if (!lmtSwitch[2].get())
                if (stick.getX() < 0)
                    siding.set(ControlMode.PercentOutput, 0.2 * stick.getX());
                else
                    siding.set(ControlMode.PercentOutput, 0);
            else if (!lmtSwitch[3].get())
                if (stick.getX() > 0)
                    siding.set(ControlMode.PercentOutput, 0.2 * stick.getX());
                else
                    siding.set(ControlMode.PercentOutput, 0);
            else
                siding.set(ControlMode.PercentOutput, 0.2 * stick.getX());

            if (!lmtSwitch[0].get())
                if (-stick.getY() > 0)
                    lifting.set(ControlMode.PercentOutput, 0.3 * stick.getY());
                else
                    lifting.set(ControlMode.PercentOutput, 0);
            else if (!lmtSwitch[1].get())
                if (-stick.getY() < 0)
                    lifting.set(ControlMode.PercentOutput, 0.3 * stick.getY());
                else
                    lifting.set(ControlMode.PercentOutput, 0);
            else
                lifting.set(ControlMode.PercentOutput, 0.3 * stick.getY());
            // Else, set the motors to 0
        }
        else {
            siding.set(ControlMode.PercentOutput, 0);
            lifting.set(ControlMode.PercentOutput, 0);
        }

        for(int i = 0; i < 4; i++)
            SmartDashboard.putBoolean("lmtSwitch["+i+"]", !lmtSwitch[i].get());
    }

    private void sidingAndLifting(){
        if (SmartDashboard.getBoolean("found", false)){
            cX = SmartDashboard.getNumber("cX", 0);
            cY = SmartDashboard.getNumber("cY", 0);
            imgWidCenter = SmartDashboard.getNumber("imgW", 1) / 2;
            imgHiCenter = SmartDashboard.getNumber("imgH", 1) / 2;

            outputX = 0.6 * (imgWidCenter - cX) / imgWidCenter*2;
            outputY = 0.6 * (imgHiCenter - cY) / imgHiCenter*2;

            // Siding
            if (cX < imgWidCenter*0.9 || cX > imgWidCenter*1.1){
                if (lmtSwitch[2].get() || lmtSwitch[3].get())
                    outputX *= -1;
                siding.set(ControlMode.PercentOutput, outputX);
            }
            else
                siding.set(ControlMode.PercentOutput, 0);


            // Lifting
            if (cY < imgHiCenter*0.9 || cY > imgHiCenter*1.1){
                if (lmtSwitch[0].get() || lmtSwitch[1].get())
                    outputY *= -1;
                lifting.set(ControlMode.PercentOutput, 0.8 *
                        (imgHiCenter - cY) / imgHiCenter*2);
            }
            else
                lifting.set(ControlMode.PercentOutput, 0);
        }
        else
            siding.set(ControlMode.PercentOutput, 0);
            lifting.set(ControlMode.PercentOutput, 0);
    }

    private void reloading(){
        for(int i = 0; i < 5; i++){
            if (detect(i, true) &&
                detect(i, false))
                slots[i].set(1);
            else
                slots[i].set(0);
        }
    }

    private boolean detect(int i, boolean mode){
        if (mode)
            return sensors[i].getRangeMM() < 10;
        else
            return sensors[i + 1].getRangeMM() > 10;
    }

    /* Set motor on 1 or 0, Depending on the mode value*/
    private void spin(int mode){
        //right_shooter.set(mode);
        //left_shooter.set(mode);
    }

}

