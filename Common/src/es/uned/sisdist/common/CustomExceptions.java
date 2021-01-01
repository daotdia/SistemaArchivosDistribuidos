/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que encapsula a distintas clases de excepciones para controlar la interacción del usuario con el sistema de archivos distribuido.
 * 
 * */
package es.uned.sisdist.common;

public class CustomExceptions extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	//Clase excepción de la que heredán todas las clases de excepciones cutomizadas para el sistema de archivos remoto. 
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
    
    //Clase interna excepción para indicar que no hay repositorios libres.
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
	
	//Clase interna excepción para indicar que el objeto no está registrado.
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
	//Clase interna excepción para indicar que no hay repositorios registrados.
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
	//Clase interna excepción para indicar el repositorio todavía no ha sido utilizado.
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
	//Clase interna excepción para indicar que no se tiene permiso para realizar la operación..
	public static class PermisoDenegado extends CustomExceptions{
		private static final long serialVersionUID = 1L;

		public PermisoDenegado () {
			
	    }

	    public PermisoDenegado (String message) {
	        super (message);
	    }

	    public PermisoDenegado (Throwable cause) {
	        super (cause);
	    }

	    public PermisoDenegado (String message, Throwable cause) {
	        super (message, cause);
	    }
	}
	
	//Clase interna excepción para indicar que se está acediendo a un elemento duplicado.
		public static class ElementoDuplicado extends CustomExceptions{
			private static final long serialVersionUID = 1L;

			public ElementoDuplicado () {
				
		    }

		    public ElementoDuplicado (String message) {
		        super (message);
		    }

		    public ElementoDuplicado (Throwable cause) {
		        super (cause);
		    }

		    public ElementoDuplicado (String message, Throwable cause) {
		        super (message, cause);
		    }
		}
}
