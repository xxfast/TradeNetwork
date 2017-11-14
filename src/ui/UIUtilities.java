package ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.filechooser.FileFilter;

import interfaces.ISavable;
import simulation.Simulation;

public class UIUtilities {

	public static final String DEFAULT_SAVE_LOCATION = "/data/";
	public static final String FILE_EXTENTION = ".tns";

	/**
	 * Helps to process the variables names through reflections
	 * 
	 * @param name
	 * @return
	 */
	public static String ProcessVariableName(String name) {
		String newName = "";
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i)) && i != 0) {
				newName += ' ';
			}
			newName += (i == 0) ? Character.toUpperCase(name.charAt(i)) : name.charAt(i);
		}
		return newName;
	}

	public static String processFileName(String nameToProcess) {
		return nameToProcess.replaceAll("\\s", "") + FILE_EXTENTION;
	}
	
	public static void Save(Object toSave) {
		Save(toSave, new File(DEFAULT_SAVE_LOCATION + processFileName(((ISavable)toSave).getName())));
	}

	public static void Save(Object toSave, File where) {
		say("Saving simulation to " + where.getAbsolutePath());
		try {
			where.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(where, true);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(toSave);
			out.close();
			fileOut.close();
			say(toSave.getClass().getSimpleName()+" saved in " + DEFAULT_SAVE_LOCATION);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static Object Load(String filePath) {
		Object toReturn = null;
		say("Loading simulation from " + filePath);
		try {
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			toReturn = ((Simulation) in.readObject());
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			say("File not found");
			c.printStackTrace();
			return null;
		}
		return toReturn;
	}

	public static void say(String toSay) {
		System.out.println("UI " + ":" + toSay);
	}
}
