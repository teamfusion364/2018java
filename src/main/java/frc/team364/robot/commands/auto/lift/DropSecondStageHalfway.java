package frc.team364.robot.commands.auto.lift;

import edu.wpi.first.wpilibj.command.Command;
import frc.team364.robot.Robot;


public class DropSecondStageHalfway extends Command {

    /**
     * DropSecondStageHalfway()
     * Second Stage moves down at full power for 1.55 seconds
     */
    public DropSecondStageHalfway() {
        requires(Robot.liftSystem);
        setTimeout(1.55);
    }

    @Override
    protected void initialize() {
        Robot.liftSystem.stopBoth();
    }

    @Override
    protected void execute() {
        Robot.liftSystem.secondStageControl(-1);
    }

    @Override
    protected boolean isFinished() {
        return isTimedOut();
    }

    @Override
    protected void end() {
        Robot.liftSystem.stopBoth();
    }

    @Override
    protected void interrupted() {
        super.interrupted();
    }
}
