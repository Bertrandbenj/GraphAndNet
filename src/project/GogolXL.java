package project;

public class GogolXL implements Gogol{

	public GogolXL(){
		
	}
	
	@Override
	public void driveThrough(City c, Square startingPoint) {
		System.out.println(this.getClass().getName()+" driveThrough");
		
	}
}