package compilador;
import java.util.*;
public class AnalID {
	public static void main(String[] args) {
		AnalID a=new AnalID();
		System.out.println("Introduce la cadena aqui:\n");
		Scanner in=new Scanner(System.in);
		String cadena1=in.nextLine();
		a.AnIdentif(cadena1);
	}

	Scanner id,asd;
	public void AnIdentif(String cadena){
		String ident="";
		id=new Scanner(cadena);
		id.useDelimiter("[ ;\n]");
		while(id.hasNext()){
			ident=id.next();
			if(!ident.equals(""))
			if(Character.isLetter(ident.charAt(0))){
				for(int i=0;i<ident.length();i++){
					if(!Character.isLetterOrDigit(ident.charAt(i))&&ident.charAt(i)!='_'){
						System.out.println("\""+ident+"\"\t Caracter no valido"); break;}
					if(i==ident.length()-1)
						System.out.println("\""+ident+"\"\t IDENTIFICADOR CORRECTO");
				}
			}else System.out.println("\""+ident+"\"\t Debe empezar con letra");
		}
	}
}
