package com.gahyunsuh.simplechat.server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class UniqueIdentifier {
	
	private static List<Integer> ids = new ArrayList<Integer>();
	private static final int RANGE = 1000; // allow ~ 1000 clients
	
	private static int index = 0;
	
	static { 
		for (int i = 0; i < RANGE; i++) {
			ids.add(i);
		}
		Collections.shuffle(ids);
	}
	
	private UniqueIdentifier() {
		
	}
	
	public static int getIdentifier() {
		if (index > ids.size() - 1)
			index = 0; 
		int returnVal = ids.get(index);
		index ++;
		return returnVal;
	}

}
