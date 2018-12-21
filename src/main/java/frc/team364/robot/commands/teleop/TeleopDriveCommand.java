/*
 *  George and Keanu:
 *  This is the TeleopDriveCommand. This runs whenever there isn't another command
 *  running that requries the DriveSystem class. In Execute, the drive motors are
 *  given an output from the joysticks using a variable in the OI (Operator Interface) class.
 *  The shifters are also set using the triggers from each joystick.
 */

package frc.team364.robot.commands.teleop;

import edu.wpi.first.wpilibj.HLUsageReporting.Null;
import edu.wpi.first.wpilibj.command.Command;
import frc.team364.robot.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import frc.team364.robot.PIDCalc;

public class TeleopDriveCommand extends Command {

    public double leftControllerInput;
    public double rightControllerInput;
    public static Command RampDown;
    public boolean rampDownSequence;
    public boolean forward;
    public double leftVelocity;
    public double rightVelocity;
    static enum DriveStates {STATE_NOT_MOVING, STATE_DIRECT_DRIVE, STATE_RAMP_DOWN, FOLLOW_CUBE, FIND_CUBE, FOLLOW_CUBE2}
    public DriveStates driveState;
    public double tankLeft;
    public double tankRight;
    public boolean CancelRamp;
    public NetworkTableEntry centerX;
    public NetworkTableEntry area;
    public PIDCalc pid;
    public double pidOutput;
    public PIDCalc pida;
    public double pidOutputa;
    public double[] x;
    public double[] a;

    /**
     * Command used for teleop control specific to the drive system
     */
    public TeleopDriveCommand() {
        requires(Robot.driveSystem);
       // RampDown = new RampDown();
        CancelRamp = false;
    }

    @Override
    protected void initialize() {
        driveState = DriveStates.STATE_NOT_MOVING;
        rampDownSequence = false;
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("GRIP/contours");
        centerX = table.getEntry("centerX");
        area = table.getEntry("area");
        pid = new PIDCalc(0.003, 0.001, 0.0, 0.0, "follow");
        pida = new PIDCalc(0.0001, 0.0, 0.0, 0.0, "area");
    }

    @Override
    protected void end() {
        // This will probably never be called.
        Robot.driveSystem.stop();
    }

    @Override
    protected void execute() {
        rightControllerInput = -Robot.oi.controller.getRawAxis(5);
        leftControllerInput = -Robot.oi.controller.getRawAxis(1);
        if(Robot.visionSystem.lockedOn(Robot.visionSystem.x)){
            System.out.println("DriveSystem confirms that the vision system reports being locked on");
        }

        // normal tank drive control
        if (driveState == DriveStates.STATE_NOT_MOVING) {
            tankLeft = 0;
            tankRight = 0;
            if ((Math.abs(leftControllerInput) >= 0.25) || (Math.abs(rightControllerInput) >= 0.25)) {
                System.out.println("STATE_NOT_MOVING->STATE_DIRECT_DRIVE");
                driveState = DriveStates.STATE_DIRECT_DRIVE;
            }
            if(Robot.oi.controller.getRawButton(10)) {
                System.out.println("STATE_NOT_MOVING->FOLLOW_CUBE");
                driveState = DriveStates.FOLLOW_CUBE;
            }
            if((Robot.oi.controller.getPOV() == 270)||(Robot.oi.controller.getPOV() == 90)){
                System.out.println("STATE_NOT_MOVING->FIND_CUBE");
                driveState = DriveStates.FIND_CUBE;
            }

        } else if (driveState == DriveStates.STATE_DIRECT_DRIVE) {
            tankLeft = leftControllerInput;
            tankRight = rightControllerInput;
            if ((Math.abs(leftControllerInput) < 0.2) && (Math.abs(rightControllerInput) < 0.2)) {
                System.out.println("STATE_DIRECT_DRIVE->STATE_RAMP_DOWN");
                driveState = DriveStates.STATE_RAMP_DOWN;
            }

        } else if (driveState == DriveStates.FOLLOW_CUBE) {
            if(!Robot.oi.controller.getRawButton(10)) {
                System.out.println("FOLLOW_CUBE->STATE_NOT_MOVING");
                driveState = DriveStates.STATE_NOT_MOVING;
            }
            double[] defaultValue = {160.0};
            double[] defaultValuea = {7000};
            x = centerX.getDoubleArray(defaultValue);
            a = area.getDoubleArray(defaultValuea);
            SmartDashboard.putNumberArray("xarray", x);
            if(x.length >= 1 && a.length >= 1) {
                pidOutput = pid.calculateOutput(160, x[0]);
                pidOutputa = pida.calculateOutput(9000, a[0]);
                tankLeft = pidOutput + pidOutputa;
                tankRight = -pidOutput + pidOutputa;
            }
            
        } else if (driveState == DriveStates.FIND_CUBE) {
                if(!((Robot.oi.controller.getPOV() == 270)||(Robot.oi.controller.getPOV() == 90))) {
                    System.out.println("FIND_CUBE->STATE_NOT_MOVING");
                    driveState = DriveStates.STATE_NOT_MOVING;
                }
                double[] defaultValue = {160.0};
                x = centerX.getDoubleArray(defaultValue);
                SmartDashboard.putNumberArray("xarray", x);
                if(x.length >= 1) {
                    System.out.println("Found Cube, Locking On");
                    pidOutput = pid.calculateOutput(160, x[0]);
                    tankLeft = pidOutput;
                    tankRight = -pidOutput;
                    if(pidOutput < 0.2){
                        System.out.println("Locked On!");
                    }
                } else{
                    System.out.println("Looking For Cube");
                    if(Robot.oi.controller.getPOV() == 270){
                        tankLeft = 0.4;
                        tankRight = -0.4;
                    }else if(Robot.oi.controller.getPOV() == 90){
                        tankLeft = -0.4;
                        tankRight = 0.4;
                    }

                }
                } else if (driveState == DriveStates.STATE_RAMP_DOWN) {
           driveState = DriveStates.STATE_NOT_MOVING;
            
        } else {
            // This condition should never happen!
            driveState = DriveStates.STATE_NOT_MOVING;
        }

        Robot.driveSystem.tankDrive(tankLeft, tankRight);
    
        if (Robot.oi.shiftHigh.get()) {
            Robot.driveSystem.shiftHigh();
        } else if (Robot.oi.shiftLow.get()) {
            Robot.driveSystem.shiftLow();
        } else {
            Robot.driveSystem.noShiftInput();
        }

        SmartDashboard.putNumber("GetLeftContr: ", leftControllerInput);
        SmartDashboard.putNumber("GetRightContr: ", rightControllerInput);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
