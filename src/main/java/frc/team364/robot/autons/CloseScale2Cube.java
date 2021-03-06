package frc.team364.robot.autons;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team364.robot.commands.auto.claw.*;
import frc.team364.robot.commands.auto.drive.*;
import frc.team364.robot.commands.auto.intake.*;
import frc.team364.robot.commands.auto.lift.*;
import frc.team364.robot.commands.auto.misc.*;

public class CloseScale2Cube extends CommandGroup {

    public CloseScale2Cube() {
        
        addSequential(new DriveStraightForCounts(15000, false, true)); // Drive to scale
        addSequential(new TurnToHeading(-22)); // Turn towards scale
        addSequential(new DriveStraightForCounts(3500, false, false)); // Drive to scale dropoff point
        addSequential(new LiftBothStages(true)); // Lift cube
        addSequential(new FlipClawDown()); // Drop claw for cube placement
        addSequential(new WaitCommand(0.1));
        addSequential(new OuttakeCube()); // Outtake cube
        addParallel(new FlipClawUp()); // Lift claw back up 
        addParallel(new DropBothStages(true)); // Drop lift
        addSequential(new TurnToHeading(-120)); // Total -155 deg
        addSequential(new FlipClawDown()); // Drop claw for intake
        addParallel(new OpenPincher()); // Open picher for cube intake
        addParallel(new IntakeCube()); // Intake cube while driving forward
        addSequential(new DriveStraightForCounts(2700, false, false)); // Drive forward
        addSequential(new ClosePincher()); // Pinch cube
        addSequential(new WaitCommand(1));
        addParallel(new FlipClawUp()); // Flip cube up
        addSequential(new DriveStraightForCounts(2500, true, false)); // Back up to previous position
        addParallel(new LiftBothStages(true)); // Lift cube
        addSequential(new TurnToHeading(120)); // Now back at -35
        addSequential(new FlipClawDown()); // Flip claw back up
        addSequential(new OuttakeCube()); // Spit out cub
        addParallel(new FlipClawUp());
        addSequential(new DropBothStages(true)); // Drop lift
    }
}