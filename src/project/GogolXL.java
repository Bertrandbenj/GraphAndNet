package project;

public class GogolXL implements Car{

	public GogolXL(){
		
	}
	
	@Override
	public void driveThrough(City c, Square startingPoint, String file) {
		System.out.println(this.getClass().getName()+" driveThrough");
		
	}
}
