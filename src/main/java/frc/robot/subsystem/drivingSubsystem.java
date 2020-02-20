package frc.robot.subsystem;


import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;

public class drivingSubsystem extends SubsystemBase{

    // Wheels
    VictorSPX left_front;
    VictorSPX left_rear;
    VictorSPX right_front;
    VictorSPX right_rear;

    // Joystick
    Joystick stickA;

    public drivingSubsystem() {
        // Initialize wheels
        right_rear= Constants.CAN.right_rear;
        right_front= Constants.CAN.right_front;
        left_rear= Constants.CAN.left_rear;
        left_front= Constants.CAN.left_front;

        // Initialize joystick
        stickA = Constants.MISC.joystick_a;
    }

    public void movement() {
        // Declare variables
        double maxLim,
                sqrt,
                xVal,
                yVal,
                throttle,
                finalL = 0,
                finalR = 0;

        // Get Joystick's input
        xVal = -stickA.getX();
        yVal = stickA.getY();

        // Throttle
        throttle = stickA.getThrottle() * 0.7;

        // Algorithm Beep Boop *-*
        if (yVal > 0.2 || yVal < -0.2){
            int negFix = yVal < -0.2 ? -1 : 1,
                    negPos = (int) (yVal / Math.abs(yVal));

            maxLim = 0.85;
            sqrt = Math.sqrt(xVal * xVal + yVal * yVal);

            finalL = sqrt * maxLim * negPos;
            finalR = (sqrt + (0.8 * xVal * -negPos * negFix)) * maxLim * -negPos;
        }
        else{
            finalL = -xVal * throttle;
            finalR = -xVal * throttle;
        }

        SmartDashboard.putNumber("FinalR", finalR);
        SmartDashboard.putNumber("FinalL", finalL);

        finalR *= throttle;
        finalL *= throttle;

        // Sending values to motors
        right_front.set(ControlMode.PercentOutput, finalR);
        right_rear.set(ControlMode.PercentOutput, finalR);

        left_front.set(ControlMode.PercentOutput, finalL);
        left_rear.set(ControlMode.PercentOutput, finalL);
    }
}

