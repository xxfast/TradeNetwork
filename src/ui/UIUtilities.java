package ui;

public class UIUtilities {
	
	/**
	 * Helps to process the variables names through reflections 
	 * @param name
	 * @return
	 */
	public static String ProcessVariableName(String name) {
		String newName = ""; 
		for(int i=0; i<name.length(); i++) {
	        if(Character.isUpperCase(name.charAt(i)) && i!=0) {
		        newName+=' ';
	        }
	        newName+=(i==0)?Character.toUpperCase(name.charAt(i)):name.charAt(i);
	    }
		return newName;
	} 
}
