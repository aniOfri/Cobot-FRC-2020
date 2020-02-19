package frc.robot.subsystem;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import frc.robot.subsystem.shootingPIDSubsystem;

import java.util.ArrayList;
import java.util.Queue;

public class shootingSubsystem extends SubsystemBase {

    // Shooting motors
    VictorSP right_shooter;
    VictorSP left_shooter;
    VictorSP push;

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
    boolean wayX;
    boolean wayY;

    shootingPIDSubsystem PID;

    // Ball Slotting
    VictorSP slots[];

    // Ultrasonics
    Ultrasonic[] sensors;
    int maxSlot;
    int slot;

    // LimitSwitch
    DigitalInput[] lmtSwitch;

    // Manual Toggler
    boolean manual;
    boolean auto;

    // Joystick
    Joystick stick;

    public shootingSubsystem() {
        //
        PID = new shootingPIDSubsystem();
        // Initialize shooters
        right_shooter = new VictorSP(0);
        left_shooter = new VictorSP(1);
        push = new VictorSP(2);

        // Initialize siding and lifting
        siding = new TalonSRX(5);
        lifting = new TalonSRX(4);


        // Initialize slot motors
        slots = new VictorSP[3];
        slots[0] = new VictorSP(5); // Bottom slot motor
        slots[1] = new VictorSP(6); // Mid slot motor
        slots[2] = new VictorSP(7); // Top slot motor(s)

        // Initialize sensors
        sensors = new Ultrasonic[5];
        sensors[0] = new Ultrasonic(4, 5);
        sensors[1] = new Ultrasonic(2, 3);
        sensors[2] = new Ultrasonic(0, 1);
        sensors[3] = new Ultrasonic(6, 7);
        sensors[4] = new Ultrasonic(8, 9);


        for (int i = 0; i < sensors.length; i++) {
            sensors[i].setAutomaticMode(true);
        }

        // Initialize Limit Switch
        lmtSwitch = new DigitalInput[4];
        lmtSwitch[0] = new DigitalInput(18); // Lifting (Bottom)
        lmtSwitch[1] = new DigitalInput(19); // Lifting (Top)
        lmtSwitch[2] = new DigitalInput(20); // Siding (Right)
        lmtSwitch[3] = new DigitalInput(21); // Siding (Left)

        // Toggler
        manual = false;
        auto = false;


        // Initialize joystick
        stick = new Joystick(0);
    }

    public void shooting() {
        // Manual shooting
        manualShooting();

        // Manual siding and lifting
        /**/

        if (auto) {
            if (!manual)
                sidingAndLifting();
            else
                manualSidingAndLifting();
        } else {
            siding.set(ControlMode.PercentOutput, 0);
            lifting.set(ControlMode.PercentOutput, 0);
        }

        // Siding and lifting
        //if (stick.getTop())
        //    sidingAndLifting();

        // Reloading
        reloading();

        if (stick.getRawButtonPressed(8))
            manual = !manual;

        if (stick.getRawButtonPressed(10))
            auto = !auto;

        SmartDashboard.putBoolean("Lifting (Min)", !lmtSwitch[0].get());
        SmartDashboard.putBoolean("Lifting (Max)", !lmtSwitch[1].get());
        SmartDashboard.putBoolean("Siding (Right)", !lmtSwitch[2].get());
        SmartDashboard.putBoolean("Siding (Left)", !lmtSwitch[3].get());
        SmartDashboard.putBoolean("Manual?", manual);
        SmartDashboard.putBoolean("Auto?", auto);

        for (int i = 0; i < sensors.length; i++) {
            SmartDashboard.putNumber("Ultrasonic["+i+"]", sensors[i].getRangeMM());
            SmartDashboard.putBoolean("Ultrasonic["+i+"] Detection", detect(i, 100));
        }
        SmartDashboard.putNumber("Siding", siding.getMotorOutputPercent());
        SmartDashboard.putNumber("Lifting", lifting.getMotorOutputPercent());
    }

    // Manual shooting
    private void manualShooting()   {
        // If trigger is pressed
        if (stick.getTop())
            spin(1);
            // Else, set the motors to 0
        else
            spin(0);

        if (stick.getTrigger())
            push.set(0.6);
        else
            push.set(0);

    }

    private void spin(int mode) {
        right_shooter.set(mode);
        left_shooter.set(-mode);
    }


    // Manual siding and lifting
    private void manualSidingAndLifting() {
        // Default value
        outputX = 0;
        outputY = 0;

        // Manual siding and lifting movement
        // Siding
        if (!lmtSwitch[2].get() && stick.getX() < 0)
            outputX = 0.3 * -stick.getX();
        else if (!lmtSwitch[3].get() && -stick.getX() > 0)
            outputX = 0.3 * -stick.getX();
        else outputX = 0.3 * -stick.getX();
        // Lifting
        if (!lmtSwitch[0].get() && -stick.getY() > 0)
            outputY = 0.3 * stick.getY();
        else if (!lmtSwitch[1].get() && -stick.getY() < 0)
            outputY = 0.3 * stick.getY();
        else outputY = 0.3 * stick.getY();

        // Set values
        siding.set(ControlMode.PercentOutput, outputX);
        lifting.set(ControlMode.PercentOutput, outputY);
    }


    // Autonomous siding and lifting
    private void sidingAndLifting() {
        // If the goal is found
        if (SmartDashboard.getBoolean("found", false)) {
            // Get values from NetworkTable (RaspberryPi)
            cX = SmartDashboard.getNumber("cX", 0);
            cY = SmartDashboard.getNumber("cY", 0);
            imgWidCenter = SmartDashboard.getNumber("imgW", 1) / 2;
            imgHiCenter = SmartDashboard.getNumber("imgH", 1) / 2;
            SmartDashboard.putNumber("imgWCenter", imgWidCenter);
            SmartDashboard.putNumber("imgHCenter", imgHiCenter);


            outputX = PID.PID_X(imgWidCenter, cX)*0.01;
            outputY = PID.PID_Y(imgHiCenter, cY)*0.01;
            outputY = 0;


            SmartDashboard.putNumber("outputX", outputX);
            SmartDashboard.putNumber("outputY", outputY);

            // Set output value depending on the goal's location
            // Siding
            if (cX < imgWidCenter * 0.98 || cX > imgWidCenter * 1.02) {
                SmartDashboard.putBoolean("CenteredX?", false);
                if ((!lmtSwitch[2].get() || !lmtSwitch[3].get())) {
                    SmartDashboard.putBoolean("LimitSwitchX?", true);
                    if (!lmtSwitch[2].get() && outputX > 0)
                        siding.set(ControlMode.PercentOutput, outputX);
                    else if (!lmtSwitch[3].get() && outputX < 0)
                        siding.set(ControlMode.PercentOutput, outputX);
                    else
                        siding.set(ControlMode.PercentOutput, 0);

                } else {
                    SmartDashboard.putBoolean("LimitSwitchX?", false);
                    siding.set(ControlMode.PercentOutput, outputX);
                }
            } else {
                SmartDashboard.putBoolean("CenteredX?", true);
                siding.set(ControlMode.PercentOutput, 0);
            }
            // Lifting
            if (cY < imgHiCenter * 1.2 || cY > imgHiCenter * 1.24) {
                SmartDashboard.putBoolean("CenteredY?", false);
                if ((!lmtSwitch[0].get() || !lmtSwitch[1].get())) {
                    SmartDashboard.putBoolean("LimitSwitchY?", true);
                    lifting.set(ControlMode.PercentOutput, 0);
                } else {
                    SmartDashboard.putBoolean("LimitSwitchY?", false);
                    lifting.set(ControlMode.PercentOutput, outputY);
                }
            } else {
                SmartDashboard.putBoolean("CenteredY?", true);
                lifting.set(ControlMode.PercentOutput, 0);
            }
        }
        // If the goal is not found
        else
            // Search for the goal
            // If limitSwitch
            if (!lmtSwitch[0].get() || !lmtSwitch[1].get())
                wayX = !wayX;

        if (!lmtSwitch[2].get() || !lmtSwitch[3].get())
            wayY = !wayY;
        // Set values
        //siding.set(ControlMode.PercentOutput, outputVal(wayX));
        //lifting.set(ControlMode.PercentOutput, outputVal(wayY));
    }

    private double outputVal(boolean way) {
        if (way)
            return 0.1;
        else
            return -0.1;
    }

    // Reloading
    private void reloading() {
        if(stick.getRawButton(7) && !detect(0, 100)){
            SmartDashboard.putBoolean("loading", true);
            for (int i = 0; i < 3; i++)
                slots[i].set(0.6);}
        else{
            SmartDashboard.putBoolean("loading", false);
            for (int i = sensors.length - 1; i >= 0; i--)
                if(!detect(i, 100)){
                    maxSlot = i;
                    SmartDashboard.putNumber("maxSlot", maxSlot);
                    break;}
                for (int j = 0; j < maxSlot; j++) {
                    if (j > 2)
                        slot = 2;
                    else
                        slot = j;

                    if (detect(j, 100)){
                        slots[slot].set(0.6);
                        if (slot == 0)
                            slots[1].set(0.6);}
                    else{
                        slots[slot].set(0);
                        if (slot == 0)
                            slots[1].set(0);}
                }
        }

        /*if (stick.getRawButton(7)) {
            for (int i = 0; i < 3; i++)
                slots[i].set(0.6);
        } else {
            for (int i = 0; i < 3; i++)
                slots[i].set(0);
        }*/
    }

    private boolean detect(int i, double range){
        return sensors[i].getRangeMM() < range;
    }

    public boolean isManual(){
        return manual;
    }
}

