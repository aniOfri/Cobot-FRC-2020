package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ballFeedingSubsystem extends SubsystemBase {

    // Joystick
    Joystick stickA;
    
    // Constants
    int range;

    // Ultrasonics
    Ultrasonic[] sensors;

    // Shooting Subsystem
    shootingSubsystem shoot;

    // Booleans
    boolean ball_present;
    boolean ball_loading_limit_switch;
    boolean prev_reloading_state;

    // Timers
    Timer ball_loading_timer;
    Timer feeding_extender_timer;
    Timer ball_presence_timer;

    public ballFeedingSubsystem() {
        // Subsystem
        shoot = Constants.MISC.shoot;

        // Initialize sensors
        sensors = new Ultrasonic[5];
        sensors[0] = Constants.ULTRASONIC.bot_slot_sens;
        sensors[1] = Constants.ULTRASONIC.mid_slot_sens;
        sensors[2] = Constants.ULTRASONIC.top_slot_sens;
        sensors[3] = Constants.ULTRASONIC.top_shoot_sens;
        sensors[4] = Constants.ULTRASONIC.bot_shoot_sens;

        // Set Automatic Mode
        for (Ultrasonic sensor : sensors)
            sensor.setAutomaticMode(true);

        // Ultrasonic range
        range = Constants.MISC.ultrasonic_range;

        // Toggles
        prev_reloading_state = false;

        // Timers
        feeding_extender_timer = Constants.MISC.top_slot_timer;
        ball_loading_timer = Constants.MISC.feeder_timer;
        ball_presence_timer = Constants.MISC.ball_presence_timer;
        ball_presence_timer.start();

        // Boolean
        ball_present = false;

        // Joystick

        stickA = Constants.MISC.joystick_a;
    }

    public void ballFeeding(){
        //Reload
        reloading();

        // SmartDashboard
        smartDashboard();
    }

    // Prepare ball for shooting
    private void prepareBallForShooting() {
        if (ball_loading_timer.get() > 0.5) {
            if (ball_loading_limit_switch) {
                if (ball_loading_timer.get() > 0.5 && ball_loading_timer.get() < 1) {
                    shoot.slots[3].set(0.2);
                    shoot.lifting.set(ControlMode.PercentOutput, 0);
                }
                if (ball_loading_timer.get() > 1 && ball_loading_timer.get() < 1.5)
                    shoot.slots[3].set(-0.2);
                if (ball_loading_timer.get() > 1.5) {
                    shoot.slots[3].set(0);
                    ball_loading_timer.stop();
                    ball_loading_timer.reset();
                }
            } else {
                shoot.lifting.set(ControlMode.PercentOutput, -0.25);
                if (!shoot.lmtSwitch[0].get()){
                    ball_loading_limit_switch = true;
                    SmartDashboard.putBoolean("tasdas", true);}
            }
        }
    }

    // Reloading
    private void reloading() {
        // Motor (0)
        if ((stickA.getRawButton(7) && !detect(0, range)) ||
                (!detect(1, range) && detect(0, range))) {
            shoot.slots[0].set(0.6);
        } else {
            shoot.slots[0].set(0);
        }
        // Motor (1)
        if ((!detect(1, range) && detect(0, range)) ||
                (!detect(2, range) && detect(1, range))) {
                    shoot.slots[1].set(0.6);
        } else {
            shoot.slots[1].set(0);
        }
        // Motor (2)
        if ((!detect(2, range) && detect(1, range)) ||
                (!detect(3, range) && detect(2, range))) {
                    shoot.slots[2].set(0.6);
            feeding_extender_timer.start();
        } else {
            if (!detect(3, range)) {
                if (feeding_extender_timer.get() > 0.25) {
                    shoot.slots[2].set(0);
                    feeding_extender_timer.stop();
                    feeding_extender_timer.reset();
                }
            } else
                shoot.slots[2].set(0);
        }
        // Prepare ball for shooting
        // No ball in shooter && ball in feeder
        if ((!detect(4, range + 80) && detect(3, range))) {
            if (ball_loading_timer.get() == 0)
                ball_loading_timer.start();
            prepareBallForShooting();}
        else {
            //lifting.set(ControlMode.PercentOutput, 0);
            shoot.slots[3].set(0);
        }
    }

    public boolean detect(int i, double range) {
        return sensors[i].getRangeMM() < range || sensors[i].getRangeMM() > 600;
    }

    private void smartDashboard(){
        SmartDashboard.putNumber("Ball presence timer", ball_presence_timer.get());
        SmartDashboard.putNumber("Ball loading timer", ball_loading_timer.get());
        SmartDashboard.putBoolean("Motor3", (!ball_present && detect(3, range)));

        for (int i = 0; i < sensors.length; i++) {
            SmartDashboard.putNumber("Ultrasonic[" + i + "]", sensors[i].getRangeMM());
            if (i == 4)
                SmartDashboard.putBoolean("Ultrasonic[" + i + "] Detection", detect(i, range + 80));
            else
                SmartDashboard.putBoolean("Ultrasonic[" + i + "] Detection", detect(i, range));
        }
    }
}

