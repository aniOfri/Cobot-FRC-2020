package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import frc.robot.Constants;

public class shootingSubsystem extends SubsystemBase {

    // Motors
    // Shooting
    VictorSP right_shooter;
    VictorSP left_shooter;
    VictorSP push;
    // Siding and lifting
    TalonSRX siding;
    TalonSRX lifting;
    // Ball Slots
    VictorSP[] slots;

    // PID Subsystem
    shootingPIDSubsystem PID;

    // Siding and lifting value
    double cX;
    double cY;
    double imgWidCenter;
    double imgHiCenter;
    double outputX;
    double outputY;


    // Sensors
    // Ultrasonics
    Ultrasonic[] sensors;
    // LimitSwitch
    DigitalInput[] lmtSwitch;

    // Constants
    int range;

    // Booleans
    boolean ball_present;
    boolean ball_loading_limit_switch;
    boolean manual_mode;
    boolean auto_mode;
    boolean prev_auto_mode_state;

    // Timers
    Timer ball_loading_timer;
    Timer feeding_extender_timer;
    Timer pushing_timer;
    Timer ball_presence_timer;

    // Joystick
    Joystick stickB;

    public shootingSubsystem() {
        // Subsystems
        PID = new shootingPIDSubsystem();

        // Initialize shooters
        right_shooter = Constants.VICTORSP.right_shooter;
        left_shooter = Constants.VICTORSP.left_shooter;
        push = Constants.VICTORSP.push;

        // Initialize siding and lifting
        siding = Constants.CAN.siding;
        lifting = Constants.CAN.lifting;
        lifting.setNeutralMode(NeutralMode.Brake);

        // Initialize slot motors
        slots = new VictorSP[4];
        slots[0] = Constants.VICTORSP.bot_slot; // Bottom slot motor
        slots[1] = Constants.VICTORSP.mid_slot; // Mid slot motor
        slots[2] = Constants.VICTORSP.top_slot; // Top slot motor
        slots[3] = Constants.VICTORSP.balance;

        // Initialize sensors
        sensors = new Ultrasonic[7];
        sensors[0] = Constants.ULTRASONIC.bot_slot_sens;
        sensors[1] = Constants.ULTRASONIC.mid_slot_sens;
        sensors[2] = Constants.ULTRASONIC.top_slot_sens;
        sensors[3] = Constants.ULTRASONIC.top_shoot_sens;
        sensors[4] = Constants.ULTRASONIC.bot_shoot_sens;
        sensors[5] = Constants.ULTRASONIC.high_R;
        sensors[6] = Constants.ULTRASONIC.high_L;

        // Set Automatic Mode
        for (Ultrasonic sensor : sensors)
            sensor.setAutomaticMode(true);

        // Ultrasonic range
        range = Constants.MISC.ultrasonic_range;

        // Initialize Limit Switch
        lmtSwitch = new DigitalInput[4];
        lmtSwitch[0] = Constants.DIO.lifting_min; // Lifting (Bottom)
        lmtSwitch[1] = Constants.DIO.lifting_max; // Lifting (Top)
        lmtSwitch[2] = Constants.DIO.siding_right; // Siding (Right)
        lmtSwitch[3] = Constants.DIO.siding_left; // Siding (Left)

        // Toggles
        manual_mode = false;
        auto_mode = false;
        prev_auto_mode_state = false;

        // Timers
        feeding_extender_timer = Constants.MISC.top_slot_timer;
        ball_loading_timer = Constants.MISC.feeder_timer;
        pushing_timer = Constants.MISC.push_timer;
        ball_presence_timer = Constants.MISC.ball_presence_timer;
        ball_presence_timer.start();

        // Initialize joystick
        stickB = Constants.MISC.joystick_b;
    }

    public void shooting() {
        // Manual shooting
        if (ball_present)
            manualShooting();
        else {
            spin(0);
            push.set(0);
        }
        // Manual siding and lifting
        /**/

        if (auto_mode) {
            if (!manual_mode)
                sidingAndLifting();
            else
                manualSidingAndLifting();
        } else {
            siding.set(ControlMode.PercentOutput, 0);
            //lifting.set(ControlMode.PercentOutput, 0);
        }

        // Reloading
        if (!stickB.getRawButton(4) && !stickB.getRawButton(6))
            reloading();

        // SmartDashboard
        smartDashboard();

        // Toggle
        if (stickB.getRawButtonPressed(8))
            manual_mode = !manual_mode;

        if (stickB.getRawButtonPressed(10))
            auto_mode = !auto_mode;


    }

    // Manual shooting
    private void manualShooting() {
        // If trigger is pressed
        if (stickB.getTop())
            spin(0.7);
            // Else, set the motors to 0
        else
            spin(0);

        if (stickB.getTrigger() && stickB.getTop()) {
            push.set(0.6);
            pushing_timer.start();
            ball_present = false;
        } else {
            push.set(0);
            if (pushing_timer.get() > 1) {
                ball_loading_timer.start();
                pushing_timer.stop();
                pushing_timer.reset();
            }
        }
    }

    private void spin(double mode) {
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
        if (!lmtSwitch[2].get() && stickB.getX() > 0)
            outputX = 0.3 * -stickB.getX();
        else if (!lmtSwitch[3].get() && stickB.getX() < 0)
            outputX = 0.3 * -stickB.getX();
        else if (lmtSwitch[2].get() && lmtSwitch[3].get())
            outputX = 0.3 * -stickB.getX();

        // Lifting
        if (!lmtSwitch[0].get() && -stickB.getY() > 0)
            outputY = 0.3 * -stickB.getY();
        else if (!lmtSwitch[1].get() && -stickB.getY() < 0)
            outputY = 0.3 * -stickB.getY();
        else if (lmtSwitch[0].get() && lmtSwitch[1].get())
            outputY = 0.3 * -stickB.getY();

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

            outputX = PID.PID_X(imgWidCenter, cX) * 0.01;
            outputY = PID.PID_Y(imgHiCenter + 30, cY) * 0.01;

            SmartDashboard.putNumber("outputX", outputX);
            SmartDashboard.putNumber("outputY", outputY);

            // Set output value depending on the goal's location
            // Siding
            if (cX < imgWidCenter * 0.98 || cX > imgWidCenter * 1.02) {
                SmartDashboard.putBoolean("CenteredX?", false);
                if (!lmtSwitch[2].get() || !lmtSwitch[3].get()) {
                    SmartDashboard.putBoolean("LimitSwitchX?", true);
                    if (!lmtSwitch[2].get() && outputX > 0)
                        siding.set(ControlMode.PercentOutput, outputX);
                    else if (!lmtSwitch[3].get() && outputX < 0)
                        siding.set(ControlMode.PercentOutput, outputX);
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
        else {
            // Set values
            siding.set(ControlMode.PercentOutput, 0);
            lifting.set(ControlMode.PercentOutput, 0);
        }
    }

    // Prepare ball for shooting
    private void prepareBallForShooting() {
        if (ball_loading_timer.get() > 0.5) {
            prev_auto_mode_state = auto_mode;
            auto_mode = false;
            if (ball_loading_limit_switch) {
                if (ball_loading_timer.get() > 0.5 && ball_loading_timer.get() < 0.75) {
                    slots[3].set(0.2);
                    lifting.set(ControlMode.PercentOutput, 0);
                }
                if (ball_loading_timer.get() > 0.75 && ball_loading_timer.get() < 1.25)
                    slots[3].set(-0.2);
                if (ball_loading_timer.get() > 1.25) {
                    slots[3].set(0);
                    ball_loading_timer.stop();
                    ball_loading_timer.reset();
                    auto_mode = prev_auto_mode_state;
                }
            } else {
                lifting.set(ControlMode.PercentOutput, -0.25);
                if (!lmtSwitch[0].get())
                    ball_loading_limit_switch = true;
            }
        }
    }

    // Reloading
    private void reloading() {
        // Motor (0)
        if ((stickB.getRawButton(7) && !detect(0, range)) ||
                (!detect(1, range) && detect(0, range))) {
            slots[0].set(0.6);
        } else {
            slots[0].set(0);
        }
        // Motor (1)
        if ((!detect(1, range) && detect(0, range)) ||
                (!detect(2, range) && detect(1, range))) {
            slots[1].set(0.6);
        } else {
            slots[1].set(0);
        }
        // Motor (2)
        if ((!detect(2, range) && detect(1, range)) ||
                (!detect(3, range) && detect(2, range))) {
            slots[2].set(0.6);
            feeding_extender_timer.start();
        } else {
            if (!detect(3, range)) {
                if (feeding_extender_timer.get() > 0.25) {
                    slots[2].set(0);
                    feeding_extender_timer.stop();
                    feeding_extender_timer.reset();
                }
            } else
                slots[2].set(0);
        }
        // Prepare ball for shooting
        // No ball in shooter && ball in feeder
        if ((!ball_present && detect(3, range))) {
            if (ball_loading_timer.get() == 0)
                ball_loading_timer.start();
            prepareBallForShooting();
        } else {
            //lifting.set(ControlMode.PercentOutput, 0);
            slots[3].set(0);
        }
        if (detect(4, range + 80) && ball_presence_timer.get() > 1) {
            ball_present = true;
            ball_presence_timer.reset();
        }
    }

    private boolean detect(int i, double range) {
        return sensors[i].getRangeMM() < range || sensors[i].getRangeMM() > 600;
    }

    // Smart Dashboard
    private void smartDashboard() {
        SmartDashboard.putBoolean("Lifting (Min)", !lmtSwitch[0].get());
        SmartDashboard.putBoolean("Lifting (Max)", !lmtSwitch[1].get());
        SmartDashboard.putBoolean("Siding (Right)", !lmtSwitch[2].get());
        SmartDashboard.putBoolean("Siding (Left)", !lmtSwitch[3].get());
        SmartDashboard.putBoolean("Manual?", manual_mode);
        SmartDashboard.putBoolean("Auto?", auto_mode);

        for (int i = 0; i < sensors.length; i++) {
            SmartDashboard.putNumber("Ultrasonic[" + i + "]", sensors[i].getRangeMM());
            if (i == 4)
                SmartDashboard.putBoolean("Ultrasonic[" + i + "] Detection", ball_present);
            else
                SmartDashboard.putBoolean("Ultrasonic[" + i + "] Detection", detect(i, range));
        }
    }
}

