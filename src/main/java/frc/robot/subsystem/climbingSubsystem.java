package frc.robot.subsystem;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;

public class climbingSubsystem extends SubsystemBase {

    // Declaration
    TalonSRX right_motor;
    TalonSRX left_motor;
    VictorSP balance;

    Ultrasonic dis_L;
    Ultrasonic dis_R;

    Joystick stick;

    public climbingSubsystem() {
        // Initialize Ultrasonic sensors
        dis_L=new Ultrasonic(1,2);//echo 2
        dis_R=new Ultrasonic(3,4);//echo 2

        // Initialize motors
        right_motor = new TalonSRX(5);
        left_motor = new TalonSRX(4);
        balance = new VictorSP(0);

        // Initialize joystick
        stick = new Joystick(0);

    }

    public void manualClimbing(){
        SmartDashboard.putNumber("distance",dis_L.getRangeInches());

        if(stick.getRawButton(3))
            spin(1); // Clockwise
        else if(stick.getRawButton(5))
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode

        if(stick.getRawButton(4))
            balance.set(1);
        else if(stick.getRawButton(6))
            balance.set(-1);
        else
            balance.set(0);
    }

    public void autonomousClimbing(){

    }

    private void spin(int mode){
        right_motor.set(ControlMode.PercentOutput, mode *  0.5);
        left_motor.set(ControlMode.PercentOutput, mode * -0.5);
    }

}

