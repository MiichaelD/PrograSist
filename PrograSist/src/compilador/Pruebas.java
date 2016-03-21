package compilador;
import javax.swing.*;
import de.congrace.exp4j.*;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptEngine;
public class Pruebas {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		//metodo1();
		//metodo2();
		try{
		JOptionPane.showMessageDialog(null,PostfixExpression.fromInfix("x=5632/563").calculate());
//			double x=Math.sin(Math.PI/2),y=1;
	//	JOptionPane.showMessageDialog(null,PostfixExpression.fromInfix("f(x,y)=x^2 - 2 * y").calculate(x,y));

		}catch(Exception e){e.printStackTrace(); }


/*		  try{

		    ScriptEngineManager mgr = new ScriptEngineManager();
		    ScriptEngine engine = mgr.getEngineByName("JavaScript");
		    String foo = "40+2";
		    System.out.println(engine.eval(foo));
		    } catch(Exception e){e.printStackTrace();}

*/
	}

	static void metodo1(){
		char m,m1;
		int n;
		m=15;
		n=JOptionPane.showInputDialog("Caracter 1").charAt(0);
		//n=
		m1=64;//JOptionPane.showInputDialog("Caracter 2").charAt(0);
		System.out.println("car1: "+m+" val "+(int)m+"\ncar2: "+m1+" val "+(int)m1+"\nnum1: "+n+" val "+(char)n);

	}

	static void metodo2(){
		//double d=Double.valueOf(JOptionPane.showInputDialog("introduce el numero")).doubleValue();
		//Double.valueOf("-("+3+")");
		JOptionPane.showMessageDialog(null,(m(JOptionPane.showInputDialog("introduce el numero"))));
	}

	static double m(String cad){

		return 0;
	}
}
