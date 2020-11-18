package es.uned.sisdist.common;

public class CustomExceptions extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CustomExceptions () {
		
    }

    public CustomExceptions (String message) {
        super (message);
    }

    public CustomExceptions (Throwable cause) {
        super (cause);
    }

    public CustomExceptions (String message, Throwable cause) {
        super (message, cause);
    }
    
	public static class NoHayRepositoriosLibres extends CustomExceptions{
		private static final long serialVersionUID = 1L;

		public NoHayRepositoriosLibres () {
			
	    }

	    public NoHayRepositoriosLibres (String message) {
	        super (message);
	    }

	    public NoHayRepositoriosLibres (Throwable cause) {
	        super (cause);
	    }

	    public NoHayRepositoriosLibres (String message, Throwable cause) {
	        super (message, cause);
	    }
	}
	
	public static class ObjetoNoRegistrado extends CustomExceptions{
		private static final long serialVersionUID = 1L;

		public ObjetoNoRegistrado () {
			
	    }

	    public ObjetoNoRegistrado (String message) {
	        super (message);
	    }

	    public ObjetoNoRegistrado (Throwable cause) {
	        super (cause);
	    }

	    public ObjetoNoRegistrado (String message, Throwable cause) {
	        super (message, cause);
	    }
	}
	
	public static class NoHayRepositoriosRegistrados extends CustomExceptions{
		private static final long serialVersionUID = 1L;

		public NoHayRepositoriosRegistrados () {
			
	    }

	    public NoHayRepositoriosRegistrados (String message) {
	        super (message);
	    }

	    public NoHayRepositoriosRegistrados (Throwable cause) {
	        super (cause);
	    }

	    public NoHayRepositoriosRegistrados (String message, Throwable cause) {
	        super (message, cause);
	    }
	}

	public static class RepositorioTodaviaNoUtilizado extends CustomExceptions{
		private static final long serialVersionUID = 1L;

		public RepositorioTodaviaNoUtilizado () {
			
	    }

	    public RepositorioTodaviaNoUtilizado (String message) {
	        super (message);
	    }

	    public RepositorioTodaviaNoUtilizado (Throwable cause) {
	        super (cause);
	    }

	    public RepositorioTodaviaNoUtilizado (String message, Throwable cause) {
	        super (message, cause);
	    }
	}
}
