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

    // Subsystems
    shootingPIDSubsystem PID;
    ballFeedingSubsystem ballFeed;

    // Siding and lifting value
    double cX;
    double cY;
    double imgWidCenter;
    double imgHiCenter;
    double outputX;
    double outputY;

    // LimitSwitch
    DigitalInput[] lmtSwitch;

    // Booleans
    boolean auto_mode;
    boolean real_auto;

    // Pushing timer
    Timer pushing_timer;

    // Joystick
    Joystick stickA;

    public shootingSubsystem() {
        // Subsystems
        PID = new shootingPIDSubsystem();
        ballFeed = new ballFeedingSubsystem();

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
        slots[3] = Constants.VICTORSP.balance; // Shooter slot

        // Initialize Limit Switch
        lmtSwitch = new DigitalInput[4];
        lmtSwitch[0] = Constants.DIO.lifting_min; // Lifting (Bottom)
        lmtSwitch[1] = Constants.DIO.lifting_max; // Lifting (Top)
        lmtSwitch[2] = Constants.DIO.siding_right; // Siding (Right)
        lmtSwitch[3] = Constants.DIO.siding_left; // Siding (Left)

        // Timers
        pushing_timer = Constants.MISC.push_timer;

        // Boolean
        real_auto = false;
        auto_mode = false;

        // Initialize joystick
        stickA = Constants.MISC.joystick_a;
    }

    public void shooting() {
        // Manual shooting
        manualShooting();
        // Manual siding and lifting
        /**/
        if (real_auto) {
            sidingAndLifting();
        } else {
            manualSidingAndLifting();
        }

        if (stickA.getRawButtonPressed(10))
            auto_mode = !auto_mode;

        real_auto = (auto_mode && ballFeed.detect(4, ballFeed.range+ 80));

        // SmartDashboard
        smartDashboard();
    }

    // Manual shooting
    private void manualShooting() {
        // Charge motors
        if (stickA.getTop()){
            spin(0.7);
        }
        else
            spin(0);

        if (stickA.getTrigger() && stickA.getTop()) {
            push.set(0.6);
            pushing_timer.start();
                ballFeed.ball_present = false;
        } else {
            push.set(0);
            if (pushing_timer.get() > 1) {
                ballFeed.ball_loading_timer.start();
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
        if (!lmtSwitch[2].get() && stickA.getX() > 0)
            outputX = 0.18 * -stickA.getX();
        else if (!lmtSwitch[3].get() && stickA.getX() < 0)
            outputX = 0.18 * -stickA.getX();
        else if (lmtSwitch[2].get() && lmtSwitch[3].get())
            outputX = 0.18 * -stickA.getX();

        // Lifting
        if (!lmtSwitch[0].get() && -stickA.getY() > 0)
            outputY = 0.15 * -stickA.getY();
        else if (!lmtSwitch[1].get() && -stickA.getY() < 0)
            outputY = 0.15 * -stickA.getY();
        else if (lmtSwitch[0].get() && lmtSwitch[1].get())
            outputY = 0.15 * -stickA.getY();

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
                        siding.set(ControlMode.PercentOutput, -outputX);
                    else if (!lmtSwitch[3].get() && outputX < 0)
                        siding.set(ControlMode.PercentOutput, -outputX);
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
    // Smart Dashboard
    private void smartDashboard() {
        SmartDashboard.putBoolean("Lifting (Min)", !lmtSwitch[0].get());
        SmartDashboard.putBoolean("Lifting (Max)", !lmtSwitch[1].get());
        SmartDashboard.putBoolean("Siding (Right)", !lmtSwitch[2].get());
        SmartDashboard.putBoolean("Siding (Left)", !lmtSwitch[3].get());
        SmartDashboard.putBoolean("Auto?", auto_mode);
    }
}

