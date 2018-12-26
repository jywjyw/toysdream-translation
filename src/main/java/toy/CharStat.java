package toy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CharStat {
	private Set<String> appearLocation = new HashSet<>();
	private int count=1;
	private String char_;
	
	public CharStat plus(){
		count++;
		return this;
	}
	
	public CharStat addLocation(String location){
		if(appearLocation.size()<=3) 
			appearLocation.add(location);
		return this;
	}
	
	public int getCount(){
		return count;
	}
	
	public String getLocationsStr(){
		return Arrays.deepToString(appearLocation.toArray(new String[]{}));
	}

	public CharStat setChar(String char_) {
		this.char_ = char_;
		return this;
	}

	public String getChar() {
		return char_;
	}
	
	
}
