package model;

import jade.core.AID;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import FIPA.DateTime;

public class Schedule implements Serializable{
	
	private Map<Short, ArrayList<Integer>> time = new HashMap<Short, ArrayList<Integer>>();
	
	public Schedule(){
		this(24);
	}
	
	public Schedule(int hours){
		for(int i=0;i<hours;i++){
			DateTime toSet = new DateTime();
			toSet.hour += i;
			time.put(toSet.hour ,new ArrayList<Integer>());
		}
	}
	
	public Map<Short, ArrayList<Integer>> getTime() {
		return time;
	}
	
}
