package frc.robot.subsystem;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;

public class climbingSubsystem extends SubsystemBase {

    // Climbing motors & balancing - מנועי טיפוס ואיזון
    TalonSRX right_motor;
    TalonSRX left_motor;
    VictorSP balance;

    // Ultrasonic - אולטרסוני
    Ultrasonic high_L;
    Ultrasonic high_R;

    // Joystick - ג'ויסטיק
    Joystick stick;

    // autoBalance toggle - מפסק איזון אוטומטי
    boolean autoBalance;

    public climbingSubsystem() {
        // Initialize Ultrasonic sensors - חיישנים אולטרסוני
        high_L=new Ultrasonic(1,2);//echo 2
        high_R=new Ultrasonic(3,4);//echo 2

        // Initialize climbing motors & balancing - מנועי טיפוס ואיזון
        right_motor = new TalonSRX(5);
        left_motor = new TalonSRX(4);
        balance = new VictorSP(0);

        // Initialize joystick - ג'ויסטיק
        stick = new Joystick(0);

        // Initialize autonomous toggle - מפסק איזון אוטומטי
        autoBalance = false;
    }

    public void climbing(){
        // Manual climbing - קריאה לטיפוס ידני
        manualClimbing();

        // Autonomous balancing - אם מפסק חיובי קרא לטיפוס אוטומטי
        if (autoBalance)
            autonomousBalance();
    }

    private void manualClimbing(){
        SmartDashboard.putNumber("distance (L)",high_L.getRangeMM());
        SmartDashboard.putNumber("distance (R)",high_R.getRangeMM());

        // Toggle auto-balancing - שינוי מפסק איזון
        if (stick.getTop() && !autoBalance)
            autoBalance = true;
        else if (stick.getTop() && autoBalance)
            autoBalance = false;

        // Spin motor - הפעלת מנוע
        if(stick.getRawButton(3))
            spin(1); // Clockwise -
        else if(stick.getRawButton(5))
            spin(-1); // Counter Clockwise
        else
            spin(0); // Neutral mode

        // Balancing motor - מנוע איזון
        if(stick.getRawButton(4))
            balance.set(1); // Right
        else if(stick.getRawButton(6))
            balance.set(-1); // Left
        else
            balance.set(0); // None
    }

    public void autonomousBalance(){
        /* If sensor detects range less than 200 on either side
         אם החיישן מזהה מרחק קטן מ200 בכל צד */
        if (high_L.getRangeMM() <= 200 ||
            high_R.getRangeMM() <= 200)
            spin(1); // Clockwise - כיוון השעון
        /* If sensor detects range greater than 400 on either side
         אם החיישן מזהה מרחק גדול מ400 בכל צד */
        else if (high_L.getRangeMM() >= 400 ||
                high_R.getRangeMM() >= 400)
            spin(-1); // Counter Clockwise - נגד הכיוון השעון
        else
            spin(0); // Set 0 - בטל מנוע
    }

    /* Set motor on 0.5, -0.5 or 0, Depending on the mode value
    הפעל את המנוע על 0.5, -0.5 אם 0, לפי ה-mode */
    private void spin(int mode){
        right_motor.set(ControlMode.PercentOutput, mode *  0.5);
        left_motor.set(ControlMode.PercentOutput, mode * -0.5);
    }

}

