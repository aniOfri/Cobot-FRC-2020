package frc.robot.subsystem;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Joystick;

public class drivingSubsystem extends SubsystemBase{

    // Declaration
    VictorSPX left_front;
    VictorSPX left_rear;
    VictorSPX right_front;
    VictorSPX right_rear;

    Joystick stick;

    public drivingSubsystem() {
        // Initialize motors
        left_front=new VictorSPX(3);
        left_rear=new VictorSPX(2);
        right_front=new VictorSPX(0);
        right_rear=new VictorSPX(1);

        // Initialize joystick
        stick = new Joystick(0);
    }

    public void movement() {
        // Declare variables
        double maxLim,
                sqrt,
                xVal,
                yVal,
                finalL = 0,
                finalR = 0;

        // Get Joystick's input
        xVal = stick.getX();
        yVal = -stick.getY();

        // Algorithm Beep Boop *-*
        if (yVal > 0.2 || yVal < -0.2) {
            int negFix = yVal < -0.2 ? -1 : 1,
                    negPos = (int) (yVal / Math.abs(yVal));

            maxLim = 0.85;
            sqrt = Math.sqrt(xVal * xVal + yVal * yVal);

            finalL = sqrt * maxLim * negPos;
            finalR = (sqrt + (0.8 * xVal * -negPos * negFix)) * maxLim * -negPos;
        } else {
            finalL = xVal;
            finalR = xVal;
        }

        SmartDashboard.putNumber("FinalR", finalR);
        SmartDashboard.putNumber("FinalL", finalL);

        // Sending values to motors
        right_front.set(ControlMode.PercentOutput, finalR);
        right_rear.set(ControlMode.PercentOutput, finalR);
        left_front.set(ControlMode.PercentOutput, finalL);
        left_rear.set(ControlMode.PercentOutput, finalL);
    }
}

