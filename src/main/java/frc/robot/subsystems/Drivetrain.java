// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;




public class Drivetrain extends SubsystemBase {
  
  //spark motor controllers
  /*
  public static CANSparkMax leftSpark1 = null;
  
  public static CANSparkMax leftSpark2 = null;
  public static CANSparkMax leftSpark3 = null;
  public static CANSparkMax rightSpark1 = null;
  public static CANSparkMax rightSpark2 = null;
  public static CANSparkMax rightSpark3 = null;*/

  public static SpeedController leftSpark1 = null;
  public static SpeedController leftSpark2 = null;
  public static SpeedController leftSpark3 = null;
  public static SpeedController rightSpark1 = null;
  public static SpeedController rightSpark2 = null;
  public static SpeedController rightSpark3 = null;
  private CANEncoder leftEncoder = null;
  private CANEncoder rightEncoder = null;
  //encoders
  
  
  
  //navX
  public AHRS gyro;
  //public DifferentialDriveKinematics kDriveKinematics;// = new DifferentialDriveKinematics(Constants.kTrackwidthMeters);
  public DifferentialDriveOdometry odometry;// = new DifferentialDriveOdometry(gyro.getRotation2d());

  //motor groups + drive
  private SpeedControllerGroup leftMotors;
  private SpeedControllerGroup rightMotors;
  private DifferentialDrive drive;


  /** Creates a new Drivetrain. */
  public Drivetrain() {
    //define sparks
    leftSpark1 = new CANSparkMax(Constants.left1, MotorType.kBrushless);
    leftSpark2 = new CANSparkMax(Constants.left2, MotorType.kBrushless);
    leftSpark3 = new CANSparkMax(Constants.left3, MotorType.kBrushless);
    rightSpark1 = new CANSparkMax(Constants.right1, MotorType.kBrushless);
    rightSpark2 = new CANSparkMax(Constants.right2, MotorType.kBrushless);
    rightSpark3 = new CANSparkMax(Constants.right3, MotorType.kBrushless);

    leftEncoder = ((CANSparkMax) leftSpark1).getEncoder();
      //private final Encoder rightEncoder = new Encoder(Constants.kRightEncoderPorts[0], Constants.kRightEncoderPorts[1]);
    rightEncoder = ((CANSparkMax) rightSpark1).getEncoder();

    leftMotors = new SpeedControllerGroup(leftSpark1, leftSpark2, leftSpark3);
    rightMotors = new SpeedControllerGroup(rightSpark1, rightSpark2, rightSpark3);
    drive = new DifferentialDrive(leftMotors, rightMotors);
    // Sets the distance per pulse for the encoders
    //leftEncoder.setDistancePerPulse(Constants.kEncoderDistancePerPulse);
    leftEncoder.setPositionConversionFactor(Constants.kEncoderDistancePerPulse);
    //rightEncoder.setDistancePerPulse(Constants.kEncoderDistancePerPulse);
    rightEncoder.setPositionConversionFactor(Constants.kEncoderDistancePerPulse);

    //kDriveKinematics = new DifferentialDriveKinematics(Constants.kTrackwidthMeters);
    gyro = new AHRS();
    odometry = new DifferentialDriveOdometry(gyro.getRotation2d());

    
    /*try {
      leftEncoder = leftSpark1.getEncoder();
      //private final Encoder rightEncoder = new Encoder(Constants.kRightEncoderPorts[0], Constants.kRightEncoderPorts[1]);
      rightEncoder = rightSpark1.getEncoder();
    }
    catch(Exception e) {
      System.out.println("error");
    }*/

    resetEncoders();
  }

  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public DifferentialDriveWheelSpeeds getWheelSpeeds() { // this averaging is probably bad
    /*
    leftSpeed = (leftSpark1.getEncoder().getVelocity() + leftSpark2.getEncoder().getVelocity() + leftSpark3.getEncoder().getVelocity()) / 3.0 * Constants.ENCODER_RPM_TO_MPS;
    rightSpeed = (rightSpark1.getEncoder().getVelocity() + rightSpark2.getEncoder().getVelocity() + rightSpark3.getEncoder().getVelocity()) / 3.0 * Constants.ENCODER_RPM_TO_MPS;*/
    //return new DifferentialDriveWheelSpeeds(leftEncoder.getVelocity(), rightEncoder.getRate());
    return new DifferentialDriveWheelSpeeds(leftEncoder.getVelocity(), rightEncoder.getVelocity());
  }

  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    odometry.resetPosition(pose, gyro.getRotation2d());
  }

  public void arcadeDrive(double linearSpeed, double angularSpeed) {
    drive.arcadeDrive(linearSpeed, angularSpeed);
  }

    //does this matter if we are arcade
  public void tankDriveVolts(double leftVolts, double rightVolts) {
    leftMotors.setVoltage(leftVolts);
    rightMotors.setVoltage(-rightVolts);
    drive.feed();
  }

  public void resetEncoders() {
    //leftEncoder.reset();
    leftEncoder.setPosition(0);
    //rightEncoder.reset();
    rightEncoder.setPosition(0);
  }

  public double getAverageEncoderDistance() {
    //return (leftEncoder.getPosition() + rightEncoder.getDistance()) / 2.0;
    return (leftEncoder.getPosition() + rightEncoder.getPosition()) / 2.0;
  }

  public CANEncoder getLeftEncoder() {
    return leftEncoder;
  }

  public CANEncoder getRightEncoder() {
    return rightEncoder;
  }

  public void setMaxOutput(double maxOutput) {
    drive.setMaxOutput(maxOutput);
  }

  public void zeroHeading() {
    gyro.reset();
  }

  public double getHeading() {
    return gyro.getRotation2d().getDegrees();

    //this one might actually be right who knows
    //return Rotation2d.fromDegrees(-gyro.getAngle());
  }

  public double getTurnRate() {
    return -gyro.getRate();
  }

  @Override
  public void periodic() {
    //odometry.update(gyro.getRotation2d(), leftEncoder.getPosition(), rightEncoder.getDistance());
    odometry.update(gyro.getRotation2d(), leftEncoder.getPosition(), rightEncoder.getPosition());
  }
}
