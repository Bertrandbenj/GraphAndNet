package project;

public class GogolL implements Gogol{
	
	public GogolL(){
		
	}

	@Override
	public void driveThrough(City c, Square startingPoint, String file) {
		System.out.println(this.getClass().getName()+" driveThrough");
	}
}
