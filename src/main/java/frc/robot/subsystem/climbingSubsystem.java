package frc.robot.subsystem;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class climbingSubsystem extends SubsystemBase {
    
    // Climbing motors & balancing
    VictorSP right_motor;
    VictorSP left_motor;
    VictorSP elevator;
    public VictorSP balance;

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
    Joystick stickB;

    // autoBalance toggle
    public boolean auto_balance;

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
        stickB = Constants.MISC.joystick_b;

        // Initialize LimitSwitch
        left = Constants.DIO.balancing_left; // Balance (Left)
        right = Constants.DIO.balancing_right;// Balance (Right)
        elevatorMin = Constants.DIO.elevator_min; // Elevator (Min)
        elevatorMax = Constants.DIO.elevator_max; // Elevator (Max)

        // Initialize autonomous toggle
        auto_balance = false;
    }

    public void climbing(){
        // Manual climbing
        if (stickA.getRawButtonPressed(12))
            auto_balance = !auto_balance;

        if (!auto_balance) {
            manualClimbing();
            // Elevator
            elevator();
        }
        // Autonomous balancing
        else
            autonomousBalance();

        // Climbing Smartdashboard
        smartDashboard();

        
    }

    // Manual climbing
    private void manualClimbing(){
        /// Spin motor
        if(stickA.getRawButton(6)) {
            spinR(1); // Clockwise
        }
        else if(stickA.getRawButton(4)){
            spinR(-1); // Counter Clockwise
        }
        else {
            spinR(0); // Neutral mode*/
        }

        if(stickA.getRawButton(5)) {
            spinL(1); // Clockwise
        }
        else if(stickA.getRawButton(3)){
            spinL(-1); // Counter Clockwise
        }
        else {
            spinL(0); // Neutral mode*/
        }

        // Balancing motor
        if(stickB.getRawButton(8) && left.get())//to the right
            balance.set(-1); // Right
        else if(stickB.getRawButton(7) && right.get())//to
            balance.set(1); // Left
        else
            balance.set(0);
    }

    // Autonomous balancing
    public void autonomousBalance(){
         //If sensor detects range less than 200 on either side
        if(elevatorMax.get()){
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
            if(high_L.getRangeMM() >= 150 && high_R.getRangeMM() >= 150){
                if(angle()<-5&& left.get()){
                    balance.set(-1);
                }
                else if(angle()>5&& right.get()){
                    balance.set(1);
                }
                else{
                    balance.set(0);
        }}
        }
        else{
            elevator.set(0.1);
        }
    }

    public double angle(){
        x=accelerometer.getX();
        y=accelerometer.getY();
        z=accelerometer.getZ();
        return (Math.atan(x/Math.sqrt(y*y+z*z))*180/Math.PI);
    }

    public void spinR(int mode){
        right_motor.set(mode);
    }

    public void spinL(int mode){
        left_motor.set(mode * -1);
    }

    public void elevator(){
        if(stickA.getRawButton(9)&& elevatorMax.get()) {//Up
            elevator.set(-0.6);
        }
        else{
            elevator.set(0);
        }
    }

    public void smartDashboard(){
        SmartDashboard.putBoolean("elevatorMin", !elevatorMin.get());
        SmartDashboard.putBoolean("elevatorMax", !elevatorMax.get());
        SmartDashboard.putBoolean("autoBalance?", auto_balance);
        SmartDashboard.putNumber("distance (R)", high_R.getRangeMM());
        SmartDashboard.putNumber("distance (L)", high_L.getRangeMM());
        SmartDashboard.putBoolean("left", !left.get());
        SmartDashboard.putBoolean("right", !right.get());
        SmartDashboard.putNumber("angle",angle());
    }

}

