package es.uned.sisdist.common;

public class SourcePath {
	public static final String CODEBASE = "java.rmi.server.codebase";
	
	public static void setCodebase(Class<?>c) {
		String source = c.getProtectionDomain().getCodeSource()
							 .getLocation().toString();
		
		String path = System.getProperty(CODEBASE);
		
		if(path != null && !path.isEmpty()) {
			source = path + " " + source;
		}
		
		System.setProperty(CODEBASE, source);
	}
}
