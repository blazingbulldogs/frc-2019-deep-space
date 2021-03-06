/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.controls;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.robot.Config;
import frc.robot.RobotMap;
import frc.robot.commands.JoystickPower;
import frc.robot.commands.cargo.CargoRoutine;
import frc.robot.commands.gyroscope.GyroToggle;
import frc.robot.commands.gyroscope.ZeroGyroYaw;
import frc.robot.commands.hatch.HatchPull;
import frc.robot.commands.hatch.HatchPush;
import frc.robot.commands.jetson.JetsonOn;
import frc.robot.commands.jetson.JetsonReset;
import frc.robot.util.Logger;

public class OperatorInput {
  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  // joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  // Joystick stick = new Joystick(port);
  // Button button = new JoystickButton(stick, buttonNumber);

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());

  public static final DriveJoystick driveJoystick = new DriveJoystick(RobotMap.joystick);
  public static double power = 1;

  /**
   * This class is the glue that binds the controls on the physical operator
   * interface to the commands and command groups that allow control of the robot.
   */
  public OperatorInput() {
    if (Config.driveSubsystemEnabled) {
      final JoystickButton xButton = new JoystickButton(driveJoystick, JoystickPorts.xButton);

      xButton.whenActive(new JoystickPower(true));
      xButton.whenInactive(new JoystickPower(false));
    }
    if (Config.hatchSubsystemEnabled) {
      final JoystickButton leftBmp = new JoystickButton(driveJoystick, JoystickPorts.leftBumper);

      // Hatch pusher plate
      leftBmp.whenPressed(new HatchPush());
      leftBmp.whenReleased(new HatchPull());
    }

    if (Config.cargoSubsystemEnabled) {
      final JoystickButton rightBmp = new JoystickButton(driveJoystick, JoystickPorts.rightBumper);

      // Cargo dumper
      rightBmp.whenPressed(new CargoRoutine());
    }

    if (Config.gyroSubsystemEnabled) {
      final JoystickButton bButton = new JoystickButton(driveJoystick, JoystickPorts.bButton);
      final JoystickButton yButton = new JoystickButton(driveJoystick, JoystickPorts.yButton);

      bButton.whenPressed(new ZeroGyroYaw());
      yButton.whenPressed(new GyroToggle());
    }

    if (Config.jetsonSubsystemEnabled) {
      Logger.tab.add("Jetson On", new JetsonOn())
      .withWidget(BuiltInWidgets.kCommand)
      .withPosition(1, 2)
      .withSize(1, 1);
      Logger.tab.add("Jetson Reset", new JetsonReset())
      .withWidget(BuiltInWidgets.kCommand)
      .withPosition(1, 3)
      .withSize(1, 1);
    }

  }

  /**
   * Get output from the left and right triggers that is in the ranges of the
   * z-axis from a controller.
   */
  public static final double triggerTurn() {
    final double leftTrigger = driveJoystick.getRawAxis(JoystickPorts.leftTriggerAxis);
    final double rightTrigger = driveJoystick.getRawAxis(JoystickPorts.rightTriggerAxis);

    if (leftTrigger != 0) {
      // Left trigger in use
      return leftTrigger * -1;
    } else {
      // Right trigger in use
      return rightTrigger;
    }
  }

  /**
   * Scales a value to be more precise at lower values. Used for driving. Cubes
   * the joystick value.
   * 
   * @return scaled value
   */
  public static double scale(double value) {
    // Cube the value
    return Math.pow(value, power);
  }
}
