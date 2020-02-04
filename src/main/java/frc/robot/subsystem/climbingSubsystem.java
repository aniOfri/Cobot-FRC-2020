package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class climbingSubsystem extends SubsystemBase {

    // Declaration
    TalonSRX right_motor;
    TalonSRX left_motor;
    VictorSP balance;
    Ultrasonic dis_L;
    Joystick stick;

    public climbingSubsystem() {
        // Initialize motors
        dis_L=new Ultrasonic(1,2);//echo 2
        right_motor = new TalonSRX(5);
        left_motor = new TalonSRX(4);

        balance = new VictorSP(0);

        // Initialize joystick
        stick = new Joystick(0);

    }

    public void manualClimbing(){
        SmartDashboard.putNumber("distance",dis_L.getRangeInches());

        if(stick.getRawButton(3)){
            right_motor.set(ControlMode.PercentOutput, 0.5);//right motor
            left_motor.set(ControlMode.PercentOutput, -0.5);//left motor
        }
        else if(stick.getRawButton(5)){
            right_motor.set(ControlMode.PercentOutput, -0.5);
            left_motor.set(ControlMode.PercentOutput, 0.5);
        }
        else{
            right_motor.set(ControlMode.PercentOutput, 0);
            left_motor.set(ControlMode.PercentOutput, 0);
        }

        if(stick.getRawButton(4))
            balance.set(1);
        else if(stick.getRawButton(6))
            balance.set(-1);
        else
            balance.set(0);
    }

    public void autonomousClimbing(){

    }

}

