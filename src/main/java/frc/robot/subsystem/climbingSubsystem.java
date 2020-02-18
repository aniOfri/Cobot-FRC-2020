package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class climbingSubsystem extends SubsystemBase {

    // Climbing motors & balancing
    VictorSP right_motor;
    VictorSP left_motor;
    VictorSP balance;
    VictorSP elevator;

    // Ultrasonic
    Ultrasonic high_L;
    Ultrasonic high_R;

    // LimitSwitch
    DigitalInput left;
    DigitalInput right;

    // Joystick
    Joystick stick;

    // autoBalance toggle
    boolean autoBalance;

    public climbingSubsystem() {
        /*// Initialize Ultrasonic sensors
        high_L=new Ultrasonic(1,2);//echo 2
        high_R=new Ultrasonic(3,4);//echo 2*/

        // Initialize climbing motors & balancing
        right_motor = new VictorSP(8);
        left_motor = new VictorSP(9);
        balance = new VictorSP(4);
        elevator = new VictorSP(3);

        // Initialize joystick
        stick = new Joystick(0);

        // Initialize LimitSwitch
        left = new DigitalInput(3); // Balance (Left)
        right = new DigitalInput(4); // Balance (Right)

        // Initialize autonomous toggle
        autoBalance = false;
    }

    public void climbing(){
        // Manual climbing
        manualClimbing();

        // Autonomous balancing
        if (autoBalance)
            autonomousBalance();
    }

    // Manual climbing
    private void manualClimbing(){
        //SmartDashboard.putNumber("distance (L)",high_L.getRangeMM());
        //SmartDashboard.putNumber("distance (R)",high_R.getRangeMM());
        SmartDashboard.putBoolean("left", !left.get());
        SmartDashboard.putBoolean("right", !right.get());

        /*// Toggle auto-balancing
        if (stick.getRawButton(11)
            autoBalance = !autoBalance;*/

        /// Spin motor
        if(stick.getRawButton(3))
            spin(1); // Clockwise
        else if(stick.getRawButton(5))
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode*/

        // Balancing motor
        if(stick.getRawButton(4) && right.get())
            balance.set(1); // Right
        else if(stick.getRawButton(6) && left.get())
            balance.set(-1); // Left
        else
            balance.set(0); // None
    }

    // Autonomous balancing
    public void autonomousBalance(){
        /* If sensor detects range less than 200 on either side*/
        if (high_L.getRangeMM() <= 200 ||
            high_R.getRangeMM() <= 200)
            spin(1); // Clockwise
        /* If sensor detects range greater than 400 on either side*/
        else if (high_L.getRangeMM() >= 400 ||
                high_R.getRangeMM() >= 400)
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode
    }

    private void spin(int mode){
        right_motor.set(mode *  0.5);
        left_motor.set(mode * -0.5);
    }

}

