package frc.robot.subsystem;


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

    //
    ADXL345_I2C accelerometer;
    double x,y,z;

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
        // Initialize Ultrasonic sensors
        high_L = Constants.ULTRASONIC.high_L;
        high_R = Constants.ULTRASONIC.high_R;

        // Initialize accelerometer
        accelerometer = Constants.MISC.acc_sens;

        // Initialize climbing motors & balancing
        right_motor = Constants.VICTORSP.right_climb;
        left_motor = Constants.VICTORSP.left_climb;
        elevator = Constants.VICTORSP.elevator;
        balance = Constants.VICTORSP.balance;

        // Initialize joystickA
        stickA = Constants.MISC.joystick_a;

        // Initialize LimitSwitch
        left = Constants.DIO.balancing_left; // Balance (Left)
        right = Constants.DIO.balancing_right;// Balance (Right)
        elevatorMin = Constants.DIO.elevator_min; // Elevator (Min)
        elevatorMax = Constants.DIO.elevator_max; // Elevator (Max)

        // Initialize autonomous toggle
        autoBalance = false;
    }

    public void climbing(){
        // Manual climbing
        if (!autoBalance)
            manualClimbing();
        // Autonomous balancing
        else
            autonomousBalance();

        // Elevator
        elevator();

        // Climbing Smartdashboard
        smartDashboard();

        // Toggle auto-balancing
        if (stickA.getRawButtonPressed(12))
            autoBalance = !autoBalance;
    }

    // Manual climbing
    private void manualClimbing(){
        SmartDashboard.putBoolean("left", !left.get());
        SmartDashboard.putBoolean("right", !right.get());

        /// Spin motor
        if(stickA.getRawButton(3)) {
            spinL(1); // Clockwise
            spinR(1); // Clockwise
        }
        else if(stickA.getRawButton(5)) {
            spinL(-1); // Counter Clockwise
            spinR(-1); // Counter Clockwise
        }
        else {
            spinL(0); // Neutral mode*/
            spinR(0); // Neutral mode*/
        }

        // Balancing motor
        if(stickA.getRawButton(4) && right.get())
            balance.set(0.6); // Right
        else if(stickA.getRawButton(6) && left.get())
            balance.set(-0.6); // Left
        else {
            balance.set(0);
        }
    }

    // Autonomous balancing
    public void autonomousBalance(){
         //If sensor detects range less than 200 on either side

        if (high_R.getRangeMM() <= 150)
            spinL(1); // Clockwise
        else if (high_R.getRangeMM() >= 300)
            spinL(-1);
        else
            spinL(0);

         //If sensor detects range greater than 400 on either side
        if (high_L.getRangeMM() <= 150)
            spinR(1); // Clockwise
        else if (high_L.getRangeMM() >= 300)
            spinR(-1);
        else
            spinR(0);

        if(angle()<-5&& left.get()){
            balance.set(-1);
        }
        else if(angle()>5&& right.get()){
            balance.set(1);
        }
        else{
            balance.set(0);
        }
    }

    public int angle(){
        x=accelerometer.getX();
        y=accelerometer.getY();
        z=accelerometer.getZ();
        return (int)(Math.atan(x/Math.sqrt(y*y+z*z))*180/Math.PI);
    }

    private void spinR(int mode){
        right_motor.set(mode);
    }

    private void spinL(int mode){
        left_motor.set(mode * -1);
    }

    public void elevator(){
        if(stickA.getRawButton(9)&& elevatorMin.get()){
            elevator.set(1);
        }
        else if(stickA.getRawButton(11)&& elevatorMax.get()){
            elevator.set(-0.5);
        }
        else{
            elevator.set(0);
        }
    }

    public void smartDashboard(){
        SmartDashboard.putBoolean("elevatorMin", !elevatorMin.get());
        SmartDashboard.putBoolean("elevatorMax", !elevatorMax.get());
        SmartDashboard.putNumber("distance (R)", high_R.getRangeMM());
        SmartDashboard.putNumber("distance (L)", high_L.getRangeMM());
        SmartDashboard.putNumber("angle", angle());
    }

}

