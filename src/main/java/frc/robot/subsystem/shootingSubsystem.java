package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import frc.robot.Constants;
import frc.robot.subsystem.shootingPIDSubsystem;
import frc.robot.subsystem.climbingSubsystem;

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

    // Subsystems
    shootingPIDSubsystem PID;
    climbingSubsystem climb;

    // Ball Slotting
    VictorSP slots[];

    // Ultrasonics
    Ultrasonic[] sensors;
    Timer posCheck;
    Timer motor;
    Timer m;
    int range;
    boolean shot;
    boolean wasTrue;

    // LimitSwitch
    DigitalInput[] lmtSwitch;

    // Manual Toggler
    boolean manual;
    boolean auto;

    // Joystick
    Joystick stick;

    public shootingSubsystem() {
        // Subsystems
        PID = new shootingPIDSubsystem();
        // Initialize shooters
        right_shooter = new VictorSP(0);
        left_shooter = new VictorSP(1);
        push = new VictorSP(2);

        // Initialize siding and lifting
        siding = new TalonSRX(5);
        lifting = new TalonSRX(4);


        // Initialize slot motors
        slots = new VictorSP[4];
        slots[0] = new VictorSP(5); // Bottom slot motor
        slots[1] = new VictorSP(6); // Mid slot motor
        slots[2] = new VictorSP(7); // Top slot motor
        slots[3] = Constants.VICTORSP.balance;

        // Initialize sensors
        sensors = new Ultrasonic[7];
        sensors[0] = new Ultrasonic(4, 5);
        sensors[1] = new Ultrasonic(2, 3);
        sensors[2] = new Ultrasonic(0, 1);
        sensors[3] = new Ultrasonic(6, 7);
        sensors[4] = new Ultrasonic(8, 9);
        sensors[5] = new Ultrasonic(14, 15);
        sensors[6] = new Ultrasonic(16, 17);


        for (int i = 0; i < sensors.length; i++) {
            sensors[i].setAutomaticMode(true);
        }

        // Range in MM
        range = 100;


        // Initialize Limit Switch
        lmtSwitch = new DigitalInput[4];
        lmtSwitch[0] = new DigitalInput(18); // Lifting (Bottom)
        lmtSwitch[1] = new DigitalInput(19); // Lifting (Top)
        lmtSwitch[2] = new DigitalInput(20); // Siding (Right)
        lmtSwitch[3] = new DigitalInput(21); // Siding (Left)

        // Toggler
        manual = false;
        auto = false;
        wasTrue = false;

        // Timer
        posCheck = new Timer();
        posCheck.start();
        motor=new Timer();
        // Initialize joystick
        stick = new Joystick(0);
    }

    public void shooting() {
        // Manual shooting
        if (detect(4, 100))
            manualShooting();
        else{
            spin(0);
            push.set(0);
        }


        // Manual siding and lifting
        /**/

        if (auto) {
            if (!manual)
                sidingAndLifting();
            else
                manualSidingAndLifting();
        } else {
            siding.set(ControlMode.PercentOutput, 0);
            //lifting.set(ControlMode.PercentOutput, 0);
        }

        // Siding and lifting
        //if (stick.getTop())
        //    sidingAndLifting();

        // Reloading
        if(!stick.getRawButton(4) || !stick.getRawButton(6))
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
            SmartDashboard.putBoolean("Ultrasonic["+i+"] Detection", detect(i, range));
        }
        SmartDashboard.putNumber("Siding", siding.getMotorOutputPercent());
        SmartDashboard.putNumber("Lifting", lifting.getMotorOutputPercent());
        SmartDashboard.putNumber("Lifting Encoder", lifting.getSelectedSensorPosition());
    }

    // Manual shooting
    private void manualShooting()   {
        // If trigger is pressed
        if (stick.getTop())
            spin(1);
            // Else, set the motors to 0
        else
            spin(0);

        if (stick.getTrigger() && stick.getTop())
            push.set(0.6);
        else {
            push.set(0);
            posCheck.start();
            shot=true;
        }

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
        else if (lmtSwitch[2].get() && lmtSwitch[3].get())
            outputX = 0.3 * -stick.getX();
        // Lifting
        if (!lmtSwitch[0].get() && -stick.getY() > 0)
            outputY = 0.3 * stick.getY();
        else if (!lmtSwitch[1].get() && -stick.getY() < 0)
            outputY = 0.3 * stick.getY();
        else if (lmtSwitch[0].get() && lmtSwitch[1].get())
            outputY = 0.3 * stick.getY();

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
                    if (!lmtSwitch[0].get() && outputY > 0)
                        lifting.set(ControlMode.PercentOutput, outputY);
                    else if (!lmtSwitch[1].get() && outputY < 0)
                        lifting.set(ControlMode.PercentOutput, outputY);
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
        // Motor (0)
        if((stick.getRawButton(7) && !detect(0, range)) ||
            (!detect(1, range) && detect(0, range))){
            SmartDashboard.putBoolean("Motor(0):", true);
            slots[0].set(0.6);}
        else{
            SmartDashboard.putBoolean("Motor(0):", false);
            slots[0].set(0);}
        // Motor (1)
        if ((!detect(1, range) && detect(0, range)) ||
            (!detect(2, range) && detect(1, range))){
            SmartDashboard.putBoolean("Motor(1):", true);
            slots[1].set(0.6);}
        else{
            SmartDashboard.putBoolean("Motor(1):", false);
            slots[1].set(0);}
        // Motor (2)
        if ((!detect(2, range) && detect(1, range)) ||
                (!detect(3, range) && detect(2, range))) {
            SmartDashboard.putBoolean("Motor(2):", true);
            slots[2].set(0.6);
            motor.start();
        }

        else{
            SmartDashboard.putBoolean("Motor(2):", false);
            if (!detect(3, range)){
                if(motor.get() > 0.25){
                    slots[2].set(0);
                    motor.stop();
                    motor.reset();}}
            else
                slots[2].set(0);

        }
        // Motor (3)
        if ((!detect(3, range) && detect(2, range)) ||
                (!detect(4, range) && detect(3, range) || shot)) {
            SmartDashboard.putBoolean("Motor(3):", true);
            shot = false;
            if (posCheck.get() > 1) {
                posCheck.stop();
                wasTrue = auto;
                auto = false;
                if (!lmtSwitch[0].get()) {
                    lifting.set(ControlMode.PercentOutput, 0);
                    slots[3].set(0.6);
                    posCheck.start();
                    posCheck.reset();
                    if (posCheck.get() > 0.5) {
                        slots[3].set(-0.6);
                        if (posCheck.get() > 1) {
                            slots[3].set(0);
                            posCheck.stop();
                            posCheck.reset();
                        }
                    }
                } else {
                    lifting.set(ControlMode.PercentOutput, 0.25);
                }
                if (wasTrue)
                    auto = true;
            }
        }
        else {
            SmartDashboard.putBoolean("Motor(3):", false);
            //balance.balance(0);
            slots[3].set(0);
        }
        /*if (stick.getRawButton(7)) {
            //for (int i = 0; i < 4; i++)
                slots[1].set(0.6);
        } else {
            //for (int i = 0; i < 4; i++)
                slots[1].set(0);
        }*/
    }

    private boolean detect(int i, double range){
        return sensors[i].getRangeMM() < range || sensors[i].getRangeMM()>600;
    }

    public boolean isManual(){
        return manual;
    }
}

