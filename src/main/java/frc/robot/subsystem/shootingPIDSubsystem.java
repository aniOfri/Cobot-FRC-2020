package frc.robot.subsystem;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.ArrayList;

public class shootingPIDSubsystem extends SubsystemBase {

    double Px, Py;
    double Ix, Iy;
    double Dx, Dy;
    double error_x, error_y;
    double avg_error_x, avg_error_y;
    double new_fix_x, new_fix_y;
    double derivative_x, derivative_y;
    double integral_x, integral_y;
    double previous_error_x, previous_error_y;

    ArrayList<Double> last_errors_x, last_errors_y;
    int last_errors_x_idx, last_errors_y_idy;

    public shootingPIDSubsystem() {
        integral_x = 0;
        integral_y = 0;
        previous_error_x = 0;
        previous_error_y = 0;
        last_errors_x = new ArrayList<Double>();
        last_errors_y = new ArrayList<Double>();
        last_errors_x_idx = 0;
        last_errors_y_idy = 0;
    }

    public double PID_X(double target, double actual){
        error_x = (target - actual); // Error = Target - Actual
        last_errors_x.add(error_x);
        last_errors_x_idx += 1;
        if (last_errors_x_idx == 10) {
            last_errors_x.remove(0);
            avg_error_x = 0;
            for (int i = 0; i < last_errors_x.size(); i++) {
                avg_error_x += last_errors_x.get(i);
            }
            avg_error_x /= last_errors_x.size();

            error_x = avg_error_x;
            last_errors_x_idx--;
        }
        integral_x += (error_x*.02); // Integral is increased by the error*time (which is .02 seconds using normal IterativeRobot)
        derivative_x = (error_x - previous_error_x) / .02;
        previous_error_x = error_x;
        Px = 0.1;
        Ix = 0.03;
        Dx = 0.02;
        new_fix_x = Px*error_x + Ix*integral_x + Dx*derivative_x;
        SmartDashboard.putNumber("integral_x", integral_x);
        return new_fix_x;
    }

    public double PID_Y(double target, double actual){
        error_y = (target - actual); // Error = Target - Actual
        last_errors_y.add(error_y);
        last_errors_y_idy += 1;
        if (last_errors_y_idy == 10) {
            last_errors_y.remove(0);
            avg_error_y = 0;
            for (int i = 0; i < last_errors_y.size(); i++) {
                avg_error_y += last_errors_y.get(i);
            }
            avg_error_y /= last_errors_y.size();
            error_y = avg_error_y;
            last_errors_y_idy--;
        }
        integral_y += (error_y*.02); // Integral is increased by the error*time (which is .02 seconds using normal IterativeRobot)
        derivative_y = (error_y - previous_error_y) / .02;
        previous_error_y = error_y;
        Py = 0.1;
        Iy = 0.03;
        Dy = 0.02;
        new_fix_y = Py*error_y + Iy*integral_y + Dy*derivative_y;
        SmartDashboard.putNumber("integral_y", integral_y);
        return new_fix_y;
    }
}

