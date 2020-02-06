package frc.robot.subsystem;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Joystick;

public class shootingSubsystem extends SubsystemBase {

    // Shooting motors - מנועי ירייה
    TalonSRX right_shooter;
    TalonSRX left_shooter;

    // Siding and lifting - צידוד והרמה
    TalonSRX siding;
    TalonSRX lifting;

    // Ball Slotting - שמירת כדורים
    TalonSRX slots[];

    // Limit Switch - מפסק גבול
    DigitalInput[] sensors;

    // Joystick - ג'ויסטיק
    Joystick stick;

    public shootingSubsystem() {
    // Initialize shooters - מנועים של ירייה
    right_shooter = new TalonSRX(6);
    left_shooter = new TalonSRX(7);

    // Initialize siding and lifting - מנועים של צידוד והרמה
    siding = new TalonSRX(8);
    lifting = new TalonSRX(9);

    // Initialize slot motors - מנועים של שמירת כדורים
    slots = new TalonSRX[4];
    slots[0] = new TalonSRX(10);
    slots[1] = new TalonSRX(11);
    slots[2] = new TalonSRX(12);
    slots[3] = new TalonSRX(13);

    // Initialize sensors - חיישנים
    sensors = new DigitalInput[5];
    sensors[0] = new DigitalInput(0);
    sensors[1] = new DigitalInput(1);
    sensors[2] = new DigitalInput(2);
    sensors[3] = new DigitalInput(3);
    sensors[4] = new DigitalInput(4);

    // Initialize joystick - ג'ויסטיק
    stick = new Joystick(0);
    }

    public void shooting(){
    // If trigger is pressed
    if (stick.getTrigger())
        spin(1);
    // Else, set the motors to 0
    else
        spin(0);
    }

    /* Set motor on 1 or 0, Depending on the mode value
       הפעל מנוע על 0 או 1 לפי ה-mode */
    private void spin(int mode){
        right_shooter.set(ControlMode.PercentOutput, mode);
        left_shooter.set(ControlMode.PercentOutput, mode);
    }

}
