package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import frc.robot.subsystem.shootingSubsystem;

public class Constants {
    public static class MISC {
            // Joystick
        public static final Joystick joystick_a = new Joystick(1);
        public static final Joystick joystick_b = new Joystick(0);
            // Timer
        public static final Timer top_slot_timer = new Timer();
        public static final Timer feeder_timer = new Timer();
        public static final Timer push_timer = new Timer();
        public static final Timer ball_presence_timer = new Timer();
            // PID
        public static final double Px = 0.24;
        public static final double Ix = 0.05;
        public static final double Dx = 0.04;
        public static final double Py = 0.12;
        public static final double Iy = 0.08;
        public static final double Dy = 0.01;
            // Integer
        public static final int ultrasonic_range = 100;
            // Accelerometer
        public static final ADXL345_I2C acc_sens = new
                ADXL345_I2C(I2C.Port.kOnboard, Accelerometer.Range.k2G, 0x53);
            // Subsystem
        public static final shootingSubsystem shoot = new shootingSubsystem();
                
                
    }
    public static class VICTORSP {
            // Shooting mechanism
        public static final VictorSP right_shooter = new VictorSP(0);
        public static final VictorSP left_shooter = new VictorSP(1);
        public static final VictorSP push = new VictorSP(2);
            // Balancing mechanism
        public static final VictorSP elevator = new VictorSP(3);
        public static final VictorSP balance = new VictorSP(4);
            // Slots : Ball storage
        public static final VictorSP bot_slot = new VictorSP(5);
        public static final VictorSP mid_slot = new VictorSP(6);
        public static final VictorSP top_slot = new VictorSP(7);
            // Climbing mechanism
        public static final VictorSP right_climb = new VictorSP(8);
        public static final VictorSP left_climb = new VictorSP(9);
    }
    public static class CAN{
            // Wheels
        public static final WPI_VictorSPX right_rear = new WPI_VictorSPX(0);
        public static final WPI_VictorSPX right_front = new WPI_VictorSPX(1);
        public static final WPI_VictorSPX left_rear = new WPI_VictorSPX(2);
        public static final WPI_VictorSPX left_front = new WPI_VictorSPX(3);
            // Siding and lifting
        public static final TalonSRX lifting = new TalonSRX(4);
        public static final TalonSRX siding = new TalonSRX(5);
    }
    public static class DIO{
            // Balancing
        public static final DigitalInput balancing_left = new DigitalInput(10);
        public static final DigitalInput balancing_right = new DigitalInput(11);
            // Lifting
        public static final DigitalInput lifting_min = new DigitalInput(18);
        public static final DigitalInput lifting_max = new DigitalInput(19);
            // Siding
        public static final DigitalInput siding_right = new DigitalInput(20);
        public static final DigitalInput siding_left = new DigitalInput(21);
            // Elevator
        public static final DigitalInput elevator_min = new DigitalInput(12);
        public static final DigitalInput elevator_max = new DigitalInput(13);
    }
    public static class ULTRASONIC{
            // Slots : Ball storage sensors
        public static final Ultrasonic top_slot_sens = new Ultrasonic(0,1);
        public static final Ultrasonic mid_slot_sens = new Ultrasonic(2,3);
        public static final Ultrasonic bot_slot_sens = new Ultrasonic(4,5);
            // Shooter sensors
        public static final Ultrasonic top_shoot_sens = new Ultrasonic(6,7);
        public static final Ultrasonic bot_shoot_sens = new Ultrasonic(8,9);
            // Balancing : Climbing sensors
        public static final Ultrasonic high_L = new Ultrasonic(17,16);
        public static final Ultrasonic high_R = new Ultrasonic(14,15);
    }
}
