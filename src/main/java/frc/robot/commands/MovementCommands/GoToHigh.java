// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.MovementCommands;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotMode;
import frc.robot.commands.SetElevatorPosition;
import frc.robot.commands.SetWristPosition;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Wrist;

public class GoToHigh extends SequentialCommandGroup {

    Wrist wrist;
    Elevator elevator;
    /** Creates a new DriveForward. */
    public GoToHigh(Wrist wrist, Elevator elevator) {
      // Use addRequirements() here to declare subsystem dependencies.
      this.wrist = wrist;
      this.elevator = elevator;
      addRequirements(wrist, elevator);

      if (RobotMode.mode == RobotMode.ModeOptions.CONE) {

        addCommands(
        
            new SetWristPosition(wrist, Constants.WRIST_CONE_STOW_POSITION)
                .until(() -> wrist.atPosition(Constants.WRIST_CONE_STOW_POSITION)),
            new SetElevatorPosition(elevator, Constants.ELEVATOR_CONE_HIGH_LEVEL)
                .until(() -> elevator.atPosition(Constants.ELEVATOR_CUBE_HIGH_LEVEL)),
            new SetWristPosition(wrist, Constants.WRIST_CONE_HIGH_POSITION)

        );

      } else {

        addCommands(

            new SetWristPosition(wrist, Constants.WRIST_CUBE_STOW_POSITION)
                .until(() -> wrist.atPosition(Constants.WRIST_CUBE_STOW_POSITION)),
            new SetElevatorPosition(elevator, Constants.ELEVATOR_CUBE_HIGH_LEVEL)
                .until(() -> elevator.atPosition(Constants.ELEVATOR_CUBE_HIGH_LEVEL)),
            new SetWristPosition(wrist, Constants.WRIST_CUBE_HIGH_POSITION)

        );

      }

    }
}

    