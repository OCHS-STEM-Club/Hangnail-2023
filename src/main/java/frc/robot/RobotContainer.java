// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PS4Controller.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Autos.Pathplanner.StraightMobility;
import frc.robot.Autos.Pathplanner.TurnMobility;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.TurnToAngle;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.MAXSwerveModule;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/*
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_robotDrive = new DriveSubsystem();

  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  // The driver's controller
  XboxController m_driverController = new XboxController(OIConstants.kDriverControllerPort);

  // The joystick controllers
  Joystick m_drivingJoystick = new Joystick(OIConstants.kDriveJoystickPort);
  Joystick m_turningJoystick = new Joystick(OIConstants.kTurnJoystickPort);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    SmartDashboard.putData("auto", autoChooser);
    autoChooser.setDefaultOption("example", loadPathplannerTrajectory("src/main/deploy/pathplanner/generatedJSON/Segment 2 Test.wpilib.json", true));
  
   
    //autoChooser.addOption("Straight Mobility", new StraightMobility(m_robotDrive));

    // Configure default commands
    m_robotDrive.setDefaultCommand(
        // The left stick controls translation of the robot.
        // Turning is controlled by the X axis of the right stick.
        new RunCommand(
            () -> m_robotDrive.drive(
               -MathUtil.applyDeadband(m_driverController.getLeftY(), OIConstants.kDriveDeadband),
               -MathUtil.applyDeadband(m_driverController.getLeftX(), OIConstants.kDriveDeadband),
              -MathUtil.applyDeadband(m_driverController.getRightX(), OIConstants.kDriveDeadband),
                  // -MathUtil.applyDeadband(m_drivingJoystick.getY(), OIConstants.kDriveDeadband),
                  // -MathUtil.applyDeadband(m_drivingJoystick.getX(), OIConstants.kDriveDeadband),
                  // -MathUtil.applyDeadband(m_turningJoystick.getZ(), OIConstants.kDriveDeadband),
                true, true),
            m_robotDrive));

    
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its
   * subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling
   * passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(m_driverController, Button.kR1.value)
        .whileTrue(new RunCommand(
            () -> m_robotDrive.setX(),
            m_robotDrive));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {

   // return null;

  //  return autoChooser.getSelected();
  

    // Create config for trajectory
    TrajectoryConfig config = new TrajectoryConfig(
        AutoConstants.kMaxSpeedMetersPerSecond,
        AutoConstants.kMaxAccelerationMetersPerSecondSquared)
        // Add kinematics to ensure max speed is actually obeyed
        .setKinematics(DriveConstants.kDriveKinematics);

    // An example trajectory to follow. All units in meters.
    // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    //     // Start at the origin facing the +X direction
    //     new Pose2d(0, 0, new Rotation2d(0)),
    //     // Pass through these two interior waypoints, making an 's' curve path
    //     List.of(new Translation2d(1, 0.5), new Translation2d(2, -0.5)),
    //    // List.of(new Translation2d(1, 0.5)),
    //    // List.of(new Translation2d(1, 0.5), new Translation2d(2, -0.5), new Translation2d(3, 0)),
    //     // End 3 meters straight ahead of where we started, facing forward
    //     new Pose2d(3, 0, new Rotation2d(0)),
    //     config);

//     example = PathPlanner.generatePath(
//           new PathConstraints(4, 3),
//           new PathPoint(new Translation2d(0, 0), Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(0)),
// // position, heading(direction of travel), holonomic rotation
//           new PathPoint(new Translation2d(3, 5), Rotation2d.fromDegrees(90), Rotation2d.fromDegrees(90)),
// // position, heading(direction of travel), holonomic rotation
//           new PathPoint(new Translation2d(5, 5), Rotation2d.fromDegrees(0), Rotation2d.fromDegrees(0))
//           // position, heading(direction of travel), holonomic rotation

    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
        // Start at the origin facing the +X direction
        new Pose2d(0, 0, new Rotation2d(0)),
        // Pass through these two interior waypoints, making an 's' curve path
        //List.of(new Translation2d(1.4, 0)),
      List.of(new Translation2d(1.5, 0)),
      //, new Translation2d(3, 0)),
        //List.of(new Translation2d(2, 0)),
       // List.of(new Translation2d(2, 0), new Translation2d(4, 0), new Translation2d(6, 0)),
        // End 3 meters straight ahead of where we started, facing forward
        new Pose2d(3,0 , new Rotation2d(0)),
        config);

    var thetaController = new ProfiledPIDController(
        AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
        exampleTrajectory,
        m_robotDrive::getPose, // Functional interface to feed supplier
        DriveConstants.kDriveKinematics,

        // Position controllers
        new PIDController(AutoConstants.kPXController, 0, 0),
        new PIDController(AutoConstants.kPYController, 0, 0),
        thetaController,
        m_robotDrive::setModuleStates,
        m_robotDrive);

    // Reset odometry to the starting pose of the trajectory.
    m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    // Run path following command, then stop at the end.
    return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false, false));


   /*
   * RED
   */

   /* */

   
    //   // Create config for trajectory
    // TrajectoryConfig config = new TrajectoryConfig(
    //     AutoConstants.kMaxSpeedMetersPerSecond,
    //     AutoConstants.kMaxAccelerationMetersPerSecondSquared)
    //     // Add kinematics to ensure max speed is actually obeyed
    //     .setKinematics(DriveConstants.kDriveKinematics);

    // // An example trajectory to follow. All units in meters.
    // // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    // //     // Start at the origin facing the +X direction
    // //     new Pose2d(0, 0, new Rotation2d(0)),
    // //     // Pass through these two interior waypoints, making an 's' curve path
    // //     List.of(new Translation2d(1, 0.5), new Translation2d(2, -0.5)),
    // //    // List.of(new Translation2d(1, 0.5)),
    // //    // List.of(new Translation2d(1, 0.5), new Translation2d(2, -0.5), new Translation2d(3, 0)),
    // //     // End 3 meters straight ahead of where we started, facing forward
    // //     new Pose2d(3, 0, new Rotation2d(0)),
    // //     config);

    // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    //     // Start at the origin facing the +X direction
    //     new Pose2d(0, 0, new Rotation2d(0)),
    //     // Pass through these two interior waypoints, making an 's' curve path
    //     //List.of(new Translation2d(1.4, 0)),
    //   List.of(new Translation2d(1, 0)),
    //   //, new Translation2d(3, 0)),
    //     //List.of(new Translation2d(2, 0)),
    //    // List.of(new Translation2d(2, 0), new Translation2d(4, 0), new Translation2d(6, 0)),
    //     // End 3 meters straight ahead of where we started, facing forward
    //     new Pose2d(2
        
    //     ,0 , new Rotation2d(0)),
    //     config);

    // var thetaController = new ProfiledPIDController(
    //     AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    // thetaController.enableContinuousInput(-Math.PI, Math.PI);

    // SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
    //     exampleTrajectory,
    //     m_robotDrive::getPose, // Functional interface to feed supplier
    //     DriveConstants.kDriveKinematics,

    //     // Position controllers
    //     new PIDController(AutoConstants.kPXController, 0, 0),
    //     new PIDController(AutoConstants.kPYController, 0, 0),
    //     thetaController,
    //     m_robotDrive::setModuleStates,
    //     m_robotDrive);

    // // Reset odometry to the starting pose of the trajectory.
    // m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    // Run path following command, then stop at the end.
    //return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false, false));

    
   }

   public void calibrateNavX() {
    m_robotDrive.zeroHeading();
   }


   public Command loadPathplannerTrajectory(String filename, boolean resetOdometry){
    Trajectory trajectory;

    try{
      Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(filename);
      trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
    }

    catch(IOException exception){
    DriverStation.reportError("unableto open trajectory file "+filename, exception.getStackTrace());

      System.out.println("Unable to read from file"+filename);

      return new InstantCommand();
    }

    var thetaController = new ProfiledPIDController(
        AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
        trajectory,
        m_robotDrive::getPose, // Functional interface to feed supplier
        DriveConstants.kDriveKinematics,

        // Position controllers
        new PIDController(AutoConstants.kPXController, 0, 0),
        new PIDController(AutoConstants.kPYController, 0, 0),
        thetaController,
        m_robotDrive::setModuleStates,
        m_robotDrive);

    // Reset odometry to the starting pose of the trajectory.
    m_robotDrive.resetOdometry(trajectory.getInitialPose());

    // Run path following command, then stop at the end.
    return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false, false));

   }

  }

