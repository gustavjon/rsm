package gustav.rsm;

public class Topic implements RsmDestination{
	
	private final String name;
	
	public Topic(String name){
		this.name = name;
	}

	public String name(){
		return name;
	}
}
