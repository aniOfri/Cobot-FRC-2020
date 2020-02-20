package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

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
    DigitalInput elevatorMax;
    DigitalInput elevatorMin;

    // Joystick
    Joystick stickA;

    // autoBalance toggle
    boolean autoBalance;

    public climbingSubsystem() {
        /*// Initialize Ultrasonic sensors
        high_L=new Ultrasonic(1,2);//echo 2
        high_R=new Ultrasonic(3,4);//echo 2*/

        // Initialize climbing motors & balancing
        right_motor = Constants.VICTORSP.right_climb;
        left_motor = Constants.VICTORSP.left_climb;
        elevator = Constants.VICTORSP.elevator;
        balance = Constants.VICTORSP.balance;

        // Initialize joystickA
        stickA = Constants.MISC.joystick_a;

        // Initialize LimitSwitch
        left = Constants.DIO.balancing_left; // Balance (Left)
        right = Constants.DIO.balancing_right; // Balance (Right)
        elevatorMin = Constants.DIO.elevator_min; // Elevator (Min)
        elevatorMax = Constants.DIO.elevator_max; // Elevator (Max)

        // Initialize autonomous toggle
        autoBalance = false;
    }

    public void climbing(){
        // Manual climbing
        manualClimbing();

        // Elevator
        elevator();

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
        SmartDashboard.putBoolean("elevatorMin", !elevatorMin.get());
        SmartDashboard.putBoolean("elevatorMax", !elevatorMax.get());

        /*// Toggle auto-balancing
        if (stickA.getRawButton(11)
            autoBalance = !autoBalance;*/

        /// Spin motor
        if(stickA.getRawButton(3))
            spin(1); // Clockwise
        else if(stickA.getRawButton(5))
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode*/

        // Balancing motor
        if(stickA.getRawButton(4) && right.get())
            balance.set(0.6); // Right
        else if(stickA.getRawButton(6) && left.get())
            balance.set(-0.6); // Left
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
    public void elevator(){
        if(stickA.getRawButton(9) && !elevatorMin.get()){//up
            elevator.set(1);
        }
        if(stickA.getRawButton(11)&& !elevatorMax.get()){//down
            elevator.set(-1);
        }
        else{
            elevator.set(0);
        }
    }

}

