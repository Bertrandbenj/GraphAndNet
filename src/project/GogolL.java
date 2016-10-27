package project;

public class GogolL implements Gogol{
	
	public GogolL(){
		
	}

	@Override
	public void driveThrough(City c) {
		System.out.println(this.getClass().getName()+" driveThrough");
	}
}
