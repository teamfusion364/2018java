package frc.team364.robot.subsystems;

public class PixyPacket {
	public int Signature;
	public int X;
	public int Y;
	public int Width;
	public int Height;
	
	//public int checksumError;
	
	public String toString() {
		return "" +
	" S:" + Signature +
	" X:" + X + 
	" Y:" + Y +
	" W:" + Width + 
	" H:" + Height;
	}
}
