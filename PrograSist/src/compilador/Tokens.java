package compilador;

public class Tokens {
	String token,tipo,valor,nombre;

	public Tokens(String tk,String n, String t, String v){
		tipo=t;
		token=tk;
		nombre=n;
		valor=v;
	}
}
