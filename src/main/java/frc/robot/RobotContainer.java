package frc.robot;

import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.autos.*;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    /* Controllers */
    private final Joystick driver = new Joystick(0);

    /* Drive Controls */
    private final int translationAxis = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    private final int rotationAxis = XboxController.Axis.kRightX.value;
    private final POVButton dPADButtonDownArm = new POVButton(driver, 180);
    private final POVButton dPADButtonUpArm = new POVButton(driver, 0);
    private final POVButton dPADButtonRighPovButton = new POVButton(driver, 270);
    /* Driver Buttons */
    private final JoystickButton zeroGyro = new JoystickButton(driver, XboxController.Button.kY.value);
    private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kLeftBumper.value);
    private final JoystickButton resetAbsolute = new JoystickButton(driver, XboxController.Button.kX.value);

    private final JoystickButton driverA = new JoystickButton(driver, XboxController.Button.kA.value);
    private final JoystickButton driverB = new JoystickButton(driver, XboxController.Button.kB.value);
    private final JoystickButton purplelights = new JoystickButton(driver, XboxController.Button.kRightBumper.value);
    private final JoystickButton yellowlights = new JoystickButton(driver, XboxController.Button.kLeftBumper.value);
    
    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();
    private final LEDs leds = new LEDs();
    private final Intake intake = new Intake();
    private final Arm arm = new Arm();


    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        arm.setDefaultCommand(new MoveArmManual(arm, driver));
        s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve, 
                () -> -driver.getRawAxis(translationAxis), 
                () -> -driver.getRawAxis(strafeAxis), 
                () -> -driver.getRawAxis(rotationAxis), 
                () -> robotCentric.getAsBoolean()
            )
            // new DriveForward(s_Swerve)
        );

        // Configure the button bindings
        configureButtonBindings();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroGyro()));
        resetAbsolute.onTrue(new InstantCommand(() -> s_Swerve.resetModulesToAbsolute()));
        intakeHandler();

        //purplelights.onTrue(new InstantCommand(( new RainbowLED(leds))));

        purplelights.onTrue(new PurpleLED(leds));
        yellowlights.onTrue(new YellowLED(leds));
        dPADButtonUpArm.onTrue(new MoveArmUp(arm));
        dPADButtonDownArm.onTrue(new MoveArmDown(arm));
        dPADButtonRighPovButton.onTrue(new MoveToLowPositiom(arm));

    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return new exampleAuto(s_Swerve);
    }

    public void resetAbsolute() {
        s_Swerve.resetModulesToAbsolute();
    }

    public Command followTrajectoryCommand(PathPlannerTrajectory traj, boolean isFirstPath) {
        return s_Swerve.followTrajectoryCommand(traj, isFirstPath);
    }


    public void intakeHandler() {

        driverA.whileTrue(new RunIntake(intake));
        driverB.whileTrue(new RunIntakeBackwards(intake));
    }

    public void armHandler() {
        
    }
}
