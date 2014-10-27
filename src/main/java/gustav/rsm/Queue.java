package gustav.rsm;

public class Queue implements RsmDestination {
	
	private final String name;
	
	public Queue(String name){
		this.name = name;
	}

	public String name(){
		return name;
	}
}
