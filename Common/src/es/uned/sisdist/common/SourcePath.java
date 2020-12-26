package es.uned.sisdist.common;

//Clase inspirada en el vídeo del compañero indicado en el enunciado de la práctica para establecer CODEBASE de java rmi.
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
