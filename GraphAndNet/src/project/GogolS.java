package project;

public class GogolS implements Gogol{
	public GogolS(){
	}

	@Override
	public void driveThrough(City c) {
		System.out.println(this.getClass().getName()+" driveThrough");
	}
}
