package frc.robot.subsystem;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;

public class climbingSubsystem extends SubsystemBase {

    // Climbing motors & balancing
    TalonSRX right_motor;
    TalonSRX left_motor;
    VictorSP balance;

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
        // Initialize Ultrasonic sensors
        high_L=new Ultrasonic(1,2);//echo 2
        high_R=new Ultrasonic(3,4);//echo 2

        // Initialize climbing motors & balancing
        right_motor = new TalonSRX(5);
        left_motor = new TalonSRX(4);
        balance = new VictorSP(0);

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

    private void manualClimbing(){
        SmartDashboard.putNumber("distance (L)",high_L.getRangeMM());
        SmartDashboard.putNumber("distance (R)",high_R.getRangeMM());

        // Toggle auto-balancing
        if (stick.getTop() && !autoBalance)
            autoBalance = true;
        else if (stick.getTop() && autoBalance)
            autoBalance = false;

        // Spin motor
        if(stick.getRawButton(3))
            spin(1); // Clockwise
        else if(stick.getRawButton(5))
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode

        // Balancing motor
        if(stick.getRawButton(4))
            balance.set(1); // Right
        else if(stick.getRawButton(6))
            balance.set(-1); // Left
        else
            balance.set(0); // None
    }

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

    /* Set motor on 0.5, -0.5 or 0, Depending on the mode value*/
    private void spin(int mode){
        right_motor.set(ControlMode.PercentOutput, mode *  0.5);
        left_motor.set(ControlMode.PercentOutput, mode * -0.5);
    }

}

