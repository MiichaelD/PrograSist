package compilador;
import java.io.File;
import javax.swing.*;
public class Syntax {

	/**
	 *Palabras Reservadas
	0	PROGRAMA	1	VARIABLES
	2	ENTERO	3	CARACTER
	4	INICIO	5	FIN
	6	SI	7	ENTONCES
	8	SINO	9	MIENTRAS
	10	HAZ	11	DESDE
	12	REALIZA	13	HASTA
	14	LEE	15	ESCRIBE
	16	CADENA	17	DOBLE
	18	LEEC	19	ESCRIBEC


	Caracteres Especiales
	20	;	21	:
	22	=	23	.
	24	+	25	>=
	26	<=	27	<>
	28	-	29	*
	30	/	31	:=
	32	(	33	)
	34	>	35	<
	36	"	37	,
	 	*/
	public static void main(String[] args) {
		if(args.length>0){
			for(int i=0;i<args.length;i++){
				PreproSensillo.abrir(new File(args[i]));
				ScanLex.mostrarPrepro();
				if(ScanLex.error.equals(""))
				Inicializar();
				}
			}
		else{
			int p=PreproSensillo.fc.showOpenDialog(null);
	   		if(p==1)return;
	   		PreproSensillo.abrir(PreproSensillo.fc.getSelectedFile());
			ScanLex.mostrarPrepro();
			Inicializar();
			}

	}

	static String tiraTokens,LastEx;
	static boolean masVari,masAsigna,masEscri,comilla,contin,Id0;

	public static void Inicializar(){
		ScanLex.ta=new JTextArea(30,51);
		ScanLex.sp=new JScrollPane(ScanLex.ta);
		tiraTokens=ScanLex.tiraTokens;
		Id0=false;
		Mostrar();
	}

	public static void Mostrar(){
		if(!ScanLex.error.equals("")){
			ScanLex.error="Lexico\n"+ScanLex.error;
			ScanLex.error+="\n\nSintactico\n";
		}

			Programa();
		if(!ScanLex.error.equals("")){  				//Si hay errores
			if(!ScanLex.error.startsWith("ERROR(ES)")&&!ScanLex.error.startsWith("Lexico"))
				ScanLex.error="ERROR(ES) .- \n"+ScanLex.error;

			ScanLex.ta.append(ScanLex.error+"\n\n");
		}

		ScanLex.ta.append("Tira de Tokens\n"+ScanLex.tiraTokens+"\n\nDonde\n"+ScanLex.donde);
		System.out.println("\n\nSYNTACTICO\n");
		System.out.println(ScanLex.error+"\n");
		System.out.println(ScanLex.tiraTokens+"\n");
		System.out.println(ScanLex.donde);
		JOptionPane.showMessageDialog(null,ScanLex.sp,"Analizador Sintactico",JOptionPane.PLAIN_MESSAGE);
	}

	public static boolean Programa(){
		boolean var1=DecProg(),var2=Variables(),var3=DecInicioFin(),var4;
		try{var4=tiraTokens.equals(""+(3+ScanLex.reservadas.length)+" ");//tiraTokens.endsWith((3+ScanLex.reservadas.length)+" ")
		}catch(Exception e){var4=false;}
		if(!var4&&var1&&var2&&var3)ScanLex.error+="Error en finalizacion de Programa";
		if(var1&&var2&&var3&&var4){
			return true;}
		//else JOptionPane.showMessageDialog(null,"DecProg  "+var1+"\nVar            "+var2+"\nDecInIf     "+var3+"\nFinal        "+var4);
		return false;
	}

	public static boolean DecProg(){
		if(tiraTokens.startsWith("00")){//"PROGRAMA"
			tiraTokens=tiraTokens.substring(3);
			if(ID()){					//<ID>
				if(tiraTokens.startsWith(""+(ScanLex.reservadas.length))){//";"
					tiraTokens=tiraTokens.substring(3);
					return true;
				}else{ ScanLex.error+="Se esperaba: delimitador ';' en <DecProg>\n"; contin=false;}
			}else{ ScanLex.error+=" en <DecProg> \n"; contin=true;}
		}else {ScanLex.error+="Programa debe empezar con \"PROGRAMA\" en <DecProg>\n"; contin=true;}
		if(contin){
			for(int i=0;i<tiraTokens.length();i=i+3){
				if(tiraTokens.substring(i,i+2).equals(""+ScanLex.reservadas.length)){
					tiraTokens=tiraTokens.substring(i+3);break;
				}
			}
		}
		return false;
	}

	public static boolean ID(){
		if(tiraTokens.startsWith("88")){
			tiraTokens=tiraTokens.substring(3);
			if(Id0&&tiraTokens.startsWith("00")){
				ScanLex.error+="ID no permitido \""+ScanLex.palabras.get(1)+"\"";
				return false;
			}
			if(!Id0)Id0=true;
			tiraTokens=tiraTokens.substring(3);
			return true;}
		else {ScanLex.error+="Se esperaba: Identificador";}
		return false;
	}

	public static boolean IdCons(){
		if(tiraTokens.startsWith("88"))
			return ID();
		else if(tiraTokens.startsWith("80")||tiraTokens.startsWith(""+(16+ScanLex.reservadas.length))){
			return Constante();}
		else ScanLex.error+=("Se esperaba: ID/Constante ");
	return false;
	}

	public static boolean Constante(){
		if(tiraTokens.startsWith("80")){
			tiraTokens=tiraTokens.substring(6);
			return true;
		}
		else if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length)+" 80")){
			tiraTokens=tiraTokens.substring(9);
			if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length))){
				tiraTokens=tiraTokens.substring(3);
				return true;
			}else ScanLex.error+="Se esperaba: Cierre Comillas de CONSTANTE";
		}else ScanLex.error+="Error de Constante";
		return false;
	}

	public static boolean Variables(){
		masVari=false;
		return Variables(false);
	}

	public static boolean Variables(boolean rec){
		if(tiraTokens.startsWith("01 04")&&!rec){
			tiraTokens=tiraTokens.substring(3);
			return true;}

		else if(tiraTokens.startsWith("01")||rec){
			if(!rec)
				tiraTokens=tiraTokens.substring(3);
			if(masVari&&tiraTokens.startsWith("88"))
				masVari=false;
			if(!masVari){
				if(ID()){
					if(tiraTokens.startsWith(""+(1+ScanLex.reservadas.length))){// ':'
						tiraTokens=tiraTokens.substring(3);
						if(TipoDato()){
							if(tiraTokens.startsWith(""+ScanLex.reservadas.length)){
								tiraTokens=tiraTokens.substring(3);
								masVari=true;
								return Variables(true);}
							else{ ScanLex.error+="Se esperaba: Delimitador en Variables\n"; contin=false;}
						}else{ ScanLex.error+=" En Variables\n"; contin=true;}

					}else if(tiraTokens.startsWith(""+(17+ScanLex.reservadas.length))){//','
						tiraTokens=tiraTokens.substring(3);
						return Variables(true);//recursividad

						}else{ScanLex.error+="Se esperaba: delimitador ( ',' | ':' ) en <Variables>\n"; contin=true;}
				}else{ ScanLex.error+=" en <Variables>\n"; contin=true;}
			}else return true;
		}else {ScanLex.error+="Se esperaba: \"VARIABLES\" en <Variables>\n"; contin=true;}

		if(contin)continuar(""+ScanLex.reservadas.length);
		return false;
	}

	public static boolean TipoDato(){
		if(tiraTokens.startsWith("02")||tiraTokens.startsWith("03")||tiraTokens.startsWith("16")||tiraTokens.startsWith("17")){
			tiraTokens=tiraTokens.substring(3);
			return true;}
		else ScanLex.error+="tipo dato Incorrecto";
		return false;
	}

	public static boolean OpLog(){
		if(tiraTokens.startsWith(""+(2+ScanLex.reservadas.length))||
				tiraTokens.startsWith(""+(5+ScanLex.reservadas.length))||
				tiraTokens.startsWith(""+(6+ScanLex.reservadas.length))||
				tiraTokens.startsWith(""+(7+ScanLex.reservadas.length))||
				tiraTokens.startsWith(""+(14+ScanLex.reservadas.length))||
				tiraTokens.startsWith(""+(15+ScanLex.reservadas.length))){
			tiraTokens=tiraTokens.substring(3);
			return true;
		}
		else ScanLex.error+="Operador Logico Incorrecto";
		return false;
	}

	public static boolean Asigna(){
		return Asigna(false);
	}

	public static boolean Asigna(boolean rec){
			if((tiraTokens.startsWith("88")&&!tiraTokens.startsWith("88 00"))||rec){	//checa qe haya ID
				if(!rec)ID();														//no checa si recursivo
				if(tiraTokens.startsWith(""+(11+ScanLex.reservadas.length))||rec){	// Checa Asignacion
					if(!rec) tiraTokens=tiraTokens.substring(3);					//no checa si recursivo
					if(tiraTokens.startsWith(""+(8+ScanLex.reservadas.length))||		//si empieza con - o (
							tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))){
						tiraTokens=tiraTokens.substring(3);			return Asigna(true);}
					if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){
						ScanLex.error+="Parentesis ')' no esperado en <Asigna>\n";
						continuar(""+(ScanLex.reservadas.length));
						return false;
						}
					if(IdCons()){															//checar constante o ID
						if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length)))
							tiraTokens=tiraTokens.substring(3);
						if(tiraTokens.startsWith(""+(ScanLex.reservadas.length))){	//';'
							tiraTokens=tiraTokens.substring(3);
							return true;
						}else if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))){
							ScanLex.error+="Parentesis '(' no esperado  en <Asigna>\n";
							contin=true;
						}else if(tiraTokens.startsWith(""+(4+ScanLex.reservadas.length))||
								tiraTokens.startsWith(""+(8+ScanLex.reservadas.length))||
								tiraTokens.startsWith(""+(9+ScanLex.reservadas.length))||
								tiraTokens.startsWith(""+(10+ScanLex.reservadas.length))||
								tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){//'+' '-' '*' '/' ')'
							tiraTokens=tiraTokens.substring(3);
							return Asigna(true);
						}else {	ScanLex.error+="Se esperaba: (;) en <Asigna>\n"; contin=false;}
					}else {ScanLex.error+=" en <Asigna>\n"; contin=true;}
				}else {ScanLex.error+="Se esperaba: Delimitador de Asignación (':=') en <Asigna>\n"; contin=true;}
			}else {ScanLex.error+="ID no permitido \""+ScanLex.palabras.get(1)+"\" en <Asigna>\n"; contin=true;}

			if(contin)continuar(""+(ScanLex.reservadas.length));
		return false;
	}

	public static boolean DecInicioFin(){
		if(tiraTokens.startsWith("04")){
			tiraTokens=tiraTokens.substring(3);
			 if(Instrucciones()){
				if(tiraTokens.startsWith("05")){
					tiraTokens=tiraTokens.substring(3);
					return true;
				}else {ScanLex.error+="Se esperaba: \"Fin\" en <DecInicioFin> "; contin=false;}
			}else {ScanLex.error+=" en <DecInicioFin> "; contin=true;}
		}else {ScanLex.error+="Se esperaba: \"Inicio\" en <DecInicioFin> "; contin=true;}

		if(contin)continuar("05");
		return false;
	}

	public static boolean Comparacion(){
		if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))){				// Checa "("
			tiraTokens=tiraTokens.substring(3);
			if(IdCons()){															//checa ID/Cons
				if(OpLog()){														//checa Op.Logico
					if(IdCons()){ 												  //checa ID/Cons
						if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){ // Checa ")"
							tiraTokens=tiraTokens.substring(3);
							return true;
						}else {ScanLex.error+="Se esperaba: \")\" en <Comparacion>"; contin=false;}
					}else {ScanLex.error+=" en <Comparacion>"; contin=true;}
				}else {ScanLex.error+=" en <Comparacion>"; contin=true;}
			} else {ScanLex.error+=" en <Comparacion>"; contin=true;}
		} else{ScanLex.error+="Se esperaba: \"(\" en <Comparacion>"; contin=true;}

		if(contin)continuar(""+(13+ScanLex.reservadas.length));
		return false;
	}

	public static boolean DecSi(){
		if(tiraTokens.startsWith("06")){										// checa "SI"
			tiraTokens=tiraTokens.substring(3);
				if(Comparacion()){										// Checa "(" ID/Cons Op.Logico ID/Cons ")"
					if(tiraTokens.startsWith("07")){ 				//checa "ENTONCES"
						tiraTokens=tiraTokens.substring(3);
						if(DecInicioFin()){							//Checa Declaracio de INicio Fin
							return true;
						}else {ScanLex.error+=" en <DecSi>\n"; contin=false;}
					}else {ScanLex.error+="Se esperaba: \"ENTONCES\" en <DecSi>\n"; contin=true;}
				} else {ScanLex.error+=" en <DecSi>\n"; contin=true;}
		}else {ScanLex.error+="Se esperaba: \"SI\" en <DecSi>\n"; contin=true;}

		if(contin)continuar("05");
		return false;
	}

	public static boolean SINO(){
		if(tiraTokens.startsWith("08")){
			tiraTokens=tiraTokens.substring(3);
			if(DecInicioFin()){
				return true;
			}else {ScanLex.error+=" en <SiNo>\n"; contin=false;}
		}else {ScanLex.error+="Se esperaba: \"SINO\" en <SiNo>\n"; contin=true;}

		if(contin)continuar("05");
		return false;
	}

	public static boolean Escribe(){
		return Escribe(false);
	}

	public static boolean Escribe(boolean rec){
		if(tiraTokens.startsWith("15")||rec){										//Checa "ESCRIBE"
			if(!rec)tiraTokens=tiraTokens.substring(3);
			if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))||rec){		// Checa "("
				if(!rec)tiraTokens=tiraTokens.substring(3);
				if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length))){	//checa  COMILLA DOBLE
					tiraTokens=tiraTokens.substring(3);}
				if(IdCons()){													//Checa si es Constante/ID
					if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length))){	//checa  COMILLA DOBLE
						tiraTokens=tiraTokens.substring(3);}
					if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){   // checa  ")"
						tiraTokens=tiraTokens.substring(3);
						if(tiraTokens.startsWith(""+ScanLex.reservadas.length)){    //checa ";"
							tiraTokens=tiraTokens.substring(3);
							return true;
						}else {ScanLex.error+="Se esperaba: (;) en <ESCRIBE>\n"; contin=false;}
					}else if(tiraTokens.startsWith(""+(17+ScanLex.reservadas.length))){		//checa ,
						tiraTokens=tiraTokens.substring(3);
						return Escribe(true);
					}else {ScanLex.error+="Se esperaba: \")\" o \",\" en <ESCRIBE>\n";contin=true;}
				}else {ScanLex.error+=" en <ESCRIBE>\n";contin=true;}
			}else {ScanLex.error+="Se esperaba: \"(\" en <ESCRIBE>\n";contin=true;}
		}else {ScanLex.error+="Se esperaba: \"ESCRIBE\"en <ESCRIBE>\n"; contin=true;}

		if(contin)continuar(""+ScanLex.reservadas.length);
		return false;
	}

	public static boolean EscribeC(){
		return EscribeC(false);
	}

	public static boolean EscribeC(boolean rec){
			if(tiraTokens.startsWith("19")||rec){										//Checa "ESCRIBEC"
				if(!rec)tiraTokens=tiraTokens.substring(3);
				if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))||rec){		// Checa "("
					if(!rec)tiraTokens=tiraTokens.substring(3);
					if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length))){		//checa  COMILLA DOBLE
						tiraTokens=tiraTokens.substring(3);}
					if(IdCons()){														//Checa si es Constante/ID
						if(tiraTokens.startsWith(""+(16+ScanLex.reservadas.length)))	//checa  COMILLA DOBLE
							tiraTokens=tiraTokens.substring(3);
						if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){   // checa  ")"
							tiraTokens=tiraTokens.substring(3);
							if(tiraTokens.startsWith(""+ScanLex.reservadas.length)){    //checa ";"
								tiraTokens=tiraTokens.substring(3);
								return true;
							}else{ ScanLex.error+="Se esperaba: (;) en <ESCRIBEC>\n"; contin=false;}
						}else if(tiraTokens.startsWith(""+(17+ScanLex.reservadas.length))){		//checa ,
							tiraTokens=tiraTokens.substring(3);
							return EscribeC(true);
						}else {ScanLex.error+="Se esperaba: \")\" o \",\" en <ESCRIBEC>\n";contin=true;}
					}else {ScanLex.error+=" en <ESCRIBEC>\n"; contin=true;}
				}else {ScanLex.error+="Se esperaba: \"(\" en <ESCRIBEC>\n"; contin=true;}
			}else {ScanLex.error+="Se esperaba: \"ESCRIBE\"en <ESCRIBEC>\n";contin=true;}

			if(contin)continuar(""+ScanLex.reservadas.length);
			return false;

	}

	public static boolean DesdeHH(){
		if(tiraTokens.startsWith("11")){								//checa DESDE
			tiraTokens=tiraTokens.substring(3);
			if(IdCons()){												//checa valor
				if(tiraTokens.startsWith("13")){						//checa HASTA
					tiraTokens=tiraTokens.substring(3);
					if(IdCons()){										//checa tope
						if(tiraTokens.startsWith("10")){				//checa HAZ
							tiraTokens=tiraTokens.substring(3);
							if(DecInicioFin()){							//checa inicio FIN
								return true;
							}else {ScanLex.error+=" en <DesdeHH>\n"; contin=false;}
						}else {ScanLex.error+="Se esperaba: \"Haz\" en <DesdeHH>\n"; contin=true;}
					}else {ScanLex.error+=" en <DesdeHH>\n"; contin=true;}
				}else {ScanLex.error+="Se esperaba: \"HASTA\" en <DesdeHH>\n"; contin=true;}
			}else {ScanLex.error+=" en <DesdeHH>\n"; contin=true;}
		}else {contin=true; ScanLex.error+="Se esperaba: \"DESDE\" en <DesdeHH>\n";}

		if(contin)	continuar("05");
		return false;
	}

	public static boolean RealizaHasta(){
		if(tiraTokens.startsWith("12")){					//REALIZA
			tiraTokens=tiraTokens.substring(3);
			if(DecInicioFin()){
				if(tiraTokens.startsWith("13")){			// HASTA
					tiraTokens=tiraTokens.substring(3);
					if(Comparacion()){
						return true;
					}else {ScanLex.error+=" en <Realiza-Hasta>\n"; contin=false;}
				}else {contin=true;ScanLex.error+="Se esperaba: \"Hasta\"en <Realiza-Hasta>\n";}
			}else {contin=true;ScanLex.error+=" en <Realiza-Hasta>\n";}
		}else {contin=true;ScanLex.error+="Se esperaba: \"Realiza\"en <Realiza-Hasta>\n";}

		if(contin){continuar(""+(13+ScanLex.reservadas.length));}
		return false;
	}

	public static boolean MientrasHaz(){
		if(tiraTokens.startsWith("09")){							//checa Mientras
			tiraTokens=tiraTokens.substring(3);
			if(Comparacion()){										//checa comparacion
				if(tiraTokens.startsWith("10")){					//checa Haz
					tiraTokens=tiraTokens.substring(3);
					if(DecInicioFin()){								//checa InicioFin
						return true;
					}else {ScanLex.error+=" en <Mientras-Haz>\n"; contin=false;}
				}else {contin=true;ScanLex.error+="Se esperaba: \"HAZ\" en <Mientras-Haz>\n";}
			}else {contin=true;ScanLex.error+=" en <Mientras-Haz>\n";}
		}else {contin=true;ScanLex.error+="Se esperaba: \"MIENTRAS\" en <Mientras-Haz>\n";}

		if(contin){continuar("05");}
		return false;
		}

	public static boolean Lee(){
		return Lee(false);
	}

	public static boolean Lee(boolean rec){
		if(tiraTokens.startsWith("14")||rec){														//checar Lee
			if(!rec)tiraTokens=tiraTokens.substring(3);
			if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))||rec){						// Checar (
				if(!rec)tiraTokens=tiraTokens.substring(3);
				if(ID()){																			// Checar ID
					if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){					//Checar )
						tiraTokens=tiraTokens.substring(3);
						if(tiraTokens.startsWith(""+(ScanLex.reservadas.length))){					//Checa ;
							tiraTokens=tiraTokens.substring(3);
							return true;
						}else {contin=false; ScanLex.error+="Se esperaba: (;) en <LEE>\n";}
					}else if(tiraTokens.startsWith(""+(17+ScanLex.reservadas.length))){				//checar ,
						tiraTokens=tiraTokens.substring(3);
						return Lee(true);
					}else {contin=true; ScanLex.error+="Se esperaba: ) en <LEE>\n";}
				}else {contin=true; ScanLex.error+="Se esperaba: ID/Cons  en <LEE>\n";}
			}else {contin=true; ScanLex.error+="Se esperaba: \"(\"  en <LEE>\n";}
		}else {contin=true;ScanLex.error+="Se esperaba: Lee  en <LEE>\n";}

		if(contin){continuar(""+ScanLex.reservadas.length);}
		return false;
	}

	public static boolean LeeC(){
		return LeeC(false);
	}

	public static boolean LeeC(boolean rec){
		if(tiraTokens.startsWith("18")||rec){														//checar LeeC
			if(!rec)tiraTokens=tiraTokens.substring(3);
			if(tiraTokens.startsWith(""+(12+ScanLex.reservadas.length))||rec){						// Checar (
				if(!rec)tiraTokens=tiraTokens.substring(3);
				if(ID()){																		// Checar ID
					if(tiraTokens.startsWith(""+(13+ScanLex.reservadas.length))){					//Checar )
						tiraTokens=tiraTokens.substring(3);
						if(tiraTokens.startsWith(""+(ScanLex.reservadas.length))){					//Checa ;
							tiraTokens=tiraTokens.substring(3);
							return true;
						}else {contin=false;ScanLex.error+="Se esperaba: (;) en <LEEC>\n";}
					}else if(tiraTokens.startsWith(""+(17+ScanLex.reservadas.length))){				//checar ,
						tiraTokens=tiraTokens.substring(3);
						return LeeC(true);
					}else {contin=true;ScanLex.error+="Se esperaba: ) en <LEEC>\n";}
				}else {contin=true; ScanLex.error+="Se esperaba: ID/Cons  en <LEEC>\n";}
			}else {contin=true; ScanLex.error+="Se esperaba: (  en <LEEC>\n";}
		}else {contin=true; ScanLex.error+="Se esperaba: Lee  en <LEEC>\n";}

		if(contin){continuar(""+ScanLex.reservadas.length);}
		return false;
	}

	public static void continuar(String cad){
		String temp;
		for(int i=0;i<tiraTokens.length();i=i+3){
			temp=tiraTokens.substring(i,i+2);
			if(temp.equals(cad)){
				tiraTokens=tiraTokens.substring(i+3); break;}
		}
	}








	public static boolean Instrucciones(){
		boolean var=true;
		if(tiraTokens.startsWith("05"))	return var;

		else if(tiraTokens.startsWith("88")){LastEx="Asignacion";
			var=Asigna(); if(var)var=Instrucciones();
			else Instrucciones(); return var;}

		else if(tiraTokens.startsWith("80")){
			tiraTokens=tiraTokens.substring(6);
			ScanLex.error+="No se esperaba Constante\n";
			Instrucciones();return false;}

		else if(tiraTokens.startsWith("09")){
			var=MientrasHaz();
			if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("11")){
			var=DesdeHH(); if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("12")){
			var=RealizaHasta();if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("14")){ //Lee
			var=Lee();  if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("15")){ //Escribe
			var=Escribe(); if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("18")){ // Lee C
			var=LeeC(); if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("19")){// Escribe C
			var=EscribeC(); if(var)var=Instrucciones();
			else Instrucciones();
			return var;}

		else if(tiraTokens.startsWith("06")){//si
			var=DecSi();
			if(tiraTokens.startsWith("08"))
				if(var)var=SINO(); 		else SINO();
			if(var)var=Instrucciones();
			else Instrucciones();
			return var;
			}

		else if(tiraTokens.startsWith("08")){
			ScanLex.error+="No se Esperaba SINO";
			continuar("05");
		}

		else {if(tiraTokens.length()>3){
				ScanLex.error+="Caracter NO esperado ("+tiraTokens.substring(0,2)+")\n";
				tiraTokens=tiraTokens.substring(3);
				String temp=null;
				var=false;
			//	JOptionPane.showMessageDialog(null,tiraTokens+"\nAntes");
				for(int i=0;i<tiraTokens.length();i=i+3){				temp=tiraTokens.substring(i,i+2);
					if(temp.equals("05")||temp.equals("06")||temp.equals("08")||temp.equals("19")||temp.equals("18")||temp.equals("15")||
						temp.equals("14")||temp.equals("12")||temp.equals("11")||temp.equals("09")||temp.equals("88")){
						tiraTokens=tiraTokens.substring(i);	break;}
				}
			//	JOptionPane.showMessageDialog(null,tiraTokens+"\nDespues");
				if(tiraTokens.startsWith("08")) SINO();
				Instrucciones();
				}
			else ScanLex.error+="Se esperaba: FIN";
			}

		return false;
	}


}
