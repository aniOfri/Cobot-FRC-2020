package frc.robot.subsystem;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Constants;

public class drivingSubsystem extends SubsystemBase{
 
    // Wheels
    WPI_VictorSPX fl;
    WPI_VictorSPX bl;
    WPI_VictorSPX fr;
    WPI_VictorSPX br;

    // Diff. Drive
    DifferentialDrive diffDrive;
        // SpeedCont. Group
    SpeedControllerGroup left;
    SpeedControllerGroup right;
        // Values
    double throttle, yVal, xVal;
    
    // Joystick
    Joystick stickB;

    public drivingSubsystem() {
        // Initialize wheels
        fl = Constants.CAN.left_front;
        fr = Constants.CAN.right_front;
        bl = Constants.CAN.left_rear;
        br = Constants.CAN.right_rear;

        // Initialize SpeedCont. Groups
        left = new SpeedControllerGroup(fl,bl);
        right = new SpeedControllerGroup(fr, br);
        diffDrive = new DifferentialDrive(left, right);
        
        // Initialize joystick
        stickB = Constants.MISC.joystick_b;
    }
    public void driving(){
        throttle = 1;
        if(stickB.getThrottle() < 0)
            throttle=-1;
        
        yVal = 0.7 * throttle * -stickB.getY();
        xVal = 0.7 * stickB.getX();

        //xVal controls the turn of the driving 
        //yVal controls the speed of the driving   
        diffDrive.curvatureDrive(yVal, xVal, true);
    }
}

