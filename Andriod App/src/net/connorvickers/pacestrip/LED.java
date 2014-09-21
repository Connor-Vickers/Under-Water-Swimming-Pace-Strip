package net.connorvickers.pacestrip;

public class LED {

	private float location;
	private int color;
	public static final int DEFAULTCOLOR = R.color.Off;
	
	public LED(float location, int color){
		this.color = color;
		this.location = location;
	}
	
	public LED(float location){
		this.color = DEFAULTCOLOR;
		this.location = location;
	}
	
	public float getLocation(){return location;}
	public int getColor(){return color;}
	public void setLocation(float location){this.location = location;}
	public void setColor(int color){this.color = color;}
}
