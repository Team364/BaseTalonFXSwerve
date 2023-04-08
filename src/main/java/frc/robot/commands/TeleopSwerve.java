package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.Swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;


public class TeleopSwerve extends CommandBase {    
    public Swerve s_Swerve;    
    private DoubleSupplier translationSup;
    private DoubleSupplier strafeSup;
    private DoubleSupplier rotationSup;
    private BooleanSupplier robotCentricSup;
    private BooleanSupplier slowModeSup;
    private double rotationSpeed;
    private ProfiledPIDController PID;
    private DoubleSupplier targetRotation;
    private boolean isAutoRotating;
    private static final double AUTO_ROTATE_DEADBAND = 0.3;
    // private Timer timer;

    public TeleopSwerve(Swerve s_Swerve, DoubleSupplier translationSup, DoubleSupplier strafeSup, 
            DoubleSupplier rotationSup, BooleanSupplier robotCentricSup, DoubleSupplier targetRotation,
            BooleanSupplier slowModeSup, double rotationSpeed, boolean isAutoRotating) {
        this.s_Swerve = s_Swerve;
        addRequirements(s_Swerve);

        this.translationSup = translationSup;
        this.strafeSup = strafeSup;
        this.rotationSup = rotationSup;
        this.robotCentricSup = robotCentricSup;
        this.slowModeSup = slowModeSup;
        this.rotationSpeed = rotationSpeed;
        this.targetRotation = targetRotation;
        this.isAutoRotating = isAutoRotating;
        
        PID = new ProfiledPIDController(
            Constants.ROTATE_KP, 
            Constants.ROTATE_KI, 
            Constants.ROTATE_KD, 
            new Constraints(Constants.ROTATE_VELOCITY, Constants.ROTATE_ACCELERATION)
        ); 

        // timer = new Timer();
    }

    @Override
    public void execute() {
        /* Get Values, Deadband*/
        double translationVal = MathUtil.applyDeadband(translationSup.getAsDouble(), Constants.stickDeadband);
        double strafeVal = MathUtil.applyDeadband(strafeSup.getAsDouble(), Constants.stickDeadband) * rotationSpeed;
        double rotationVal = MathUtil.applyDeadband(rotationSup.getAsDouble(), Constants.stickDeadband);

        /*slowmode*/
        if (slowModeSup.getAsBoolean()) {
            translationVal = translationVal * Constants.SLOW_MODE_PERCENT_TRANSLATION;
            strafeVal = strafeVal * Constants.SLOW_MODE_PERCENT_STRAFE;
            rotationVal = rotationVal * Constants.SLOW_MODE_PERCENT_ROTATION;
        }
        
        /* Rotate to Score */
        if (-AUTO_ROTATE_DEADBAND <= rotationVal && rotationVal <= AUTO_ROTATE_DEADBAND && isAutoRotating) {
            double yaw = s_Swerve.getYaw().getDegrees() % 360;
            double targetRotationVal = targetRotation.getAsDouble();
            double error = yaw - targetRotationVal;
            if (error > 180) {
                yaw = -1 * (360 - yaw);
            }

            rotationVal = PID.calculate(yaw, targetRotation.getAsDouble());
        }

        /* Drive */
        s_Swerve.drive(
            new Translation2d(translationVal, strafeVal).times(Constants.Swerve.maxSpeed), 
            rotationVal * Constants.Swerve.maxAngularVelocity, 
            !robotCentricSup.getAsBoolean(), 
            false
        );
    }
}