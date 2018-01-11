package ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TradeNetworkFileFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
	    if (f.isDirectory()) {
	        return true;
	    }
	    
	    String extension = null;
	    int i = f.getName().lastIndexOf('.');
	    if (i > 0) {
	        extension = f.getName().substring(i+1);
	    }
	    
	    if (extension != null) {
	        return (extension.equals("tns"));
	    }

	    return false;
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}