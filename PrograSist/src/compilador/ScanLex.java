package compilador;
import java.io.File;
import java.util.*;
import javax.swing.*;

public class ScanLex{

	public static void main(String[] args) {
		if(args.length>0){
			for(int i=0;i<args.length;i++){
				PreproSensillo.abrir(new File(args[i]));
				mostrarPrepro();
				}
			}
		else{
			int p=PreproSensillo.fc.showOpenDialog(null);
	   		if(p==1)return;
	   		PreproSensillo.abrir(PreproSensillo.fc.getSelectedFile());
			mostrarPrepro();
			JOptionPane.showMessageDialog(null,sp);
			}
		}

	public static void mostrarPrepro(){
		Inicializar();
		System.out.println(PreproSensillo.preprocesado);
		scanear(PreproSensillo.preprocesado);
		printIds();
		}

	public static void Inicializar(){
		ca=0;
		identificadores="";
		constantes="";
		lexema=null;
		menora10="0";
		donde="";
		tiraTokens="";
		error="";
		tipo=null;
		Ids=new LinkedList<String>();
		palabras=new LinkedList<String>();
		Cns=new LinkedList<String>();
		asignas=new LinkedList<String>();
		reserved=false;
		caracespe=false;
		dospuntos=true;
		tipodato=true;
		lexemaEncontrado=false;
		esConstanteComilla=false;
		fueConstanteComilla=false;
		InicioCod=false;
		esConstante2=false;
		esConstante3=false;
		comentario=false;
		ta=new JTextArea(30,50);
		sp=new JScrollPane(ta);
	}

	static String[] reservadas={"PROGRAMA","VARIABLES","ENTERO","CARACTER","INICIO","FIN","SI","ENTONCES",
			"SINO","MIENTRAS","HAZ","DESDE","REALIZA","HASTA","LEE","ESCRIBE","CADENA","DOBLE","LEEC","ESCRIBEC"},// 18 palabras reservadas
			caracValidos={";",":","=",".","+",">=","<=","<>","-","*","/",":=","(",")",">","<","\"",","};//18especiales
					     //0,  1,  2,  3,  4,   5,   6,   7,  8,  9,  10,  11, 12, 13, 14, 15, 16, 17

	static final int IDENTIFICADOR=88,CONSTANTE=80;
	static int ca=0;
	static char ca2;
	static String identificadores,constantes,lexema,letra,registro,menora10,donde,tiraTokens,error,tipo;
	static LinkedList<String> Ids=null, palabras=null, Cns=null,asignas=null;


	static boolean reserved,caracespe,lexemaEncontrado,esConstanteComilla,fueConstanteComilla,InicioCod,esConstante2,
		esConstante3,comentario,dospuntos,tipodato;

	static JTextArea ta=null;
	static JScrollPane sp=null;

	static public void scanear(String preprocesado){
		Scanner tokenEB=new Scanner(preprocesado);//agarrar tokens separados por espacio

		while(tokenEB.hasNextLine()){
			registro=tokenEB.nextLine();
			String helper="";
			if(registro.contains("/*")&&registro.contains("*/")&&!comentario){// bloque de comentarios en una sola linea
						boolean ayudar=false;
						for(int i=0;i<registro.length()-1;i++){
								if((registro.substring(i,2+i)).equals("/*"))
									{helper=registro.substring(0,i);ayudar=false;i=i+2;
									if(helper.contains("\"")||helper.endsWith("/")){helper=registro;break;}}
								else if((registro.substring(i,2+i)).equals("*/"))ayudar=true;
								if(ayudar){helper+=registro.substring(2+i,registro.length()); ayudar=false; break;}
						}registro=helper;

			}else{
				if(registro.contains("/*")&&!comentario){
					comentario=true;
					for(int i=0;i<registro.length()-1;i++)
						if((registro.substring(i,2+i)).equals("/*"))
							if(i!=0){
								helper=registro.substring(0,i);
								if(!helper.contains("\"")&&!helper.contains("//")&&!helper.endsWith("/"))registro=helper;
								else comentario=false;
							}else {try{registro=tokenEB.nextLine();}catch(Exception e){JOptionPane.showMessageDialog(null,e);}}
				}else if(registro.contains("*/")&&comentario){
					comentario=false;
					for(int i=0;i<registro.length()-1;i++)
						if((registro.substring(i,2+i)).equals("*/")) registro=registro.substring(2+i,registro.length());
					if(registro==""){try{registro=tokenEB.nextLine();}catch(Exception e){JOptionPane.showMessageDialog(null,e);}}
					}
			}
			if(!tokenEB.hasNext()&&comentario){error+="Comentario INFINITO\n";}


			if(!comentario)
			for(int re=0;re<registro.length();re++){// contra cada letra del lexema
				lexemaEncontrado=false;caracespe=false;
				letra=registro.substring(re,re+1);
				System.out.println(letra);

				if(letra.equals("_")||Character.isLetterOrDigit(letra.charAt(0))){
					esConstante3=true;
				}
				else if(letra.equals(" ")){ // compara si la letra es un espacio
					System.out.println("encontramos espacio");
					lexema=registro.substring(0,re);
					lexemaEncontrado=true;
					registro=registro.substring(re+1,registro.length());
				}

				else for(ca=0;ca<caracValidos.length;ca++){// comparar contra caracter especial
					 if(caracValidos[ca].equals(letra)){// si coinsiden:
						System.out.println("encontramos caracterEspecial");
						lexema=registro.substring(0,re);
						lexemaEncontrado=true;
						caracespe=true;
						try{ca2=registro.charAt(re+1);}catch(Exception e){ca2=' ';}
						if(ca==10&&ca2=='/'){					// comentario simple
							registro=""; lexemaEncontrado=false;
							}
						else if(ca==16||esConstanteComilla){//DOBLE COMILLA para encontrar CONSTANTE
							if(!esConstanteComilla){
								int v=0;
								for (v=re+1;v<registro.length();v++){
									if(registro.charAt(v)=='\"'){
										lexema=registro.substring(re+1,v);
										fueConstanteComilla=true;
										esConstanteComilla=true; break;}
									if(v==registro.length()-1){
										error+="Falta cierre de Comillas(\")\n";
										lexema=registro.substring(re+1,registro.length());
										esConstanteComilla=true;}
								}
								registro=registro.substring(v,registro.length());
							}else{esConstanteComilla=false; registro=registro.substring(re+1,registro.length());}
						}
						else if (ca==17||ca==3){							// COMA o PUNTO para delcaracion de Variables
							boolean intentoDoble=false;
							if(ca==3){
								try{if((re==0||Character.isDigit(registro.charAt(re-1)))&&Character.isDigit(registro.charAt(re+1))){
										intentoDoble=true;
										lexemaEncontrado=false;}

									}catch(Exception e){intentoDoble=false;}
							}
							if(!intentoDoble)
							{
							int v=0;
							for (v=re+1;v<registro.length();v++){
								if(registro.charAt(v)==':'){
									Scanner tp=new Scanner (registro.substring(v+1,registro.length()));
									tp.useDelimiter("[ ;:]");
									if(tp.hasNext())
										try{tipo=tp.next();}catch(Exception e){
											if(tipodato)error+="Falta TipoDeDato en Variables\n";
											tipodato=false;
											}
										break;
								}else if(!registro.contains(":")&&v==registro.length()-1&&registro.length()>1&&dospuntos){
									dospuntos=false;}
							}
							registro=registro.substring(re+1,registro.length());
							}
						}
						else if(ca==15){														// < operando logico
							if(registro.substring(re+1,re+2).equals("=")){
								ca=6;	registro=registro.substring(re+2,registro.length());
								letra="<=";}
							else if(registro.substring(re+1,re+2).equals(">")){
								ca=7;	registro=registro.substring(re+2,registro.length());
								letra="<>";}
							else registro=registro.substring(re+1,registro.length());

						}
						else if(ca==14&&registro.substring(re+1,re+2).equals("=")){				// > operando logico
							ca=5;	registro=registro.substring(re+2,registro.length());
							letra=">=";
						}
						else if(ca==1){															// comprobaciones de DOS PUNTOS
							try{
								if(registro.substring(re+1,re+2).equals("=")){
									ca=11;	registro=registro.substring(re+2,registro.length());
									letra=":="; esConstante2=true;}
								else{
									registro=registro.substring(re+1,registro.length());
									Scanner tp=new Scanner (registro);
									tp.useDelimiter("[ ;:]");
									if(tp.hasNext())
									try{tipo=tp.next();}catch(Exception e){}
								}
							}catch(StringIndexOutOfBoundsException e){
								if(tipodato)error+="Falta TipoDeDato en Variables\n";
								tipodato=false;
								registro=registro.substring(re+1,registro.length());}}

						else {registro=registro.substring(re+1,registro.length());}
						break;
					}
				}																//no es caracter especial y se acabo registro
				if((!caracespe&&registro.length()==1)||(!lexemaEncontrado&&re==registro.length()-1&&
						!registro.substring(0,re).equals(""))){
					System.out.println("encontramos findeLinea");

					try{
					if((re!=0&&' '!=(registro.charAt(re-1)))){
						Scanner check=new Scanner(registro);
						while (check.hasNext()){
							lexema=check.next();
							letra=" EN ";
							lexemaAtratar();
							tipo=null;
							if(palabras.getLast().equals("INICIO"))InicioCod=true;
						}
						registro="";
						letra=" "; re=registro.length();
						}

					}catch(Exception e){}

				}


				else if(!caracespe&&!lexemaEncontrado&&!Character.isLetterOrDigit(letra.charAt(0))
						&&!letra.equals("_")&&!letra.equals("/")){
					error+="Caracter Invalido \""+letra+"\"\n";
					registro=registro.substring(0,re)+registro.substring(re+1,registro.length());
					re--;
					}

				if(lexemaEncontrado){
					if(esConstanteComilla){									// imprimir el delimitador
						tiraTokens+=(ca+reservadas.length)+" ";	donde+=(ca+reservadas.length)+"\t"+letra+"\n";	}
					if(!lexema.equals(""))
							//&&!lexema.equals(" "))
						lexemaAtratar();
					if(caracespe&&!esConstanteComilla){						// imprimir el delimitador
						tiraTokens+=(ca+reservadas.length)+" ";	donde+=(ca+reservadas.length)+"\t"+letra+"\n";	}
					re=-1;
					tipo=null;
					if(palabras.getLast().equals("INICIO"))InicioCod=true;
					if(fueConstanteComilla)fueConstanteComilla=false;
				}
			}
		}
	}

	public static String veriIdent(String ident){
		if(Character.isLetter(ident.charAt(0))){
			for(int i=0;i<ident.length();i++)
				if(!Character.isLetterOrDigit(ident.charAt(i))&&ident.charAt(i)!='_'){
					return (ident+"\t Caracter no valido de ID"); }
			{return ident;}
		}else return(ident+"\t Debe empezar con letra");
		}

	public static  void printIds(){
		new Thread(new Runnable() {
		        public void run(){
		        	JTextArea jta=new JTextArea();
		    		JFrame jf=new JFrame();
		    		jf.setTitle("Palabras Reservadas & Caracteres Especiales");
		    		jf.setVisible(true);
		    		jf.getContentPane().setLayout(new java.awt.FlowLayout());
		    		jf.add(jta);
		    		jf.setSize(68,46);
		    		jf.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		    		jta.append("Palabras Reservadas\n");
		    		for(int i=0;i<caracValidos.length+reservadas.length;i++){
		    			if(i==reservadas.length)jta.append("\n\nCaracteres Especiales\n");
		    			if(i<reservadas.length){
		    				if((1+i)%2==0) jta.append((i)+"\t"+reservadas[i]+"\n");
		    				else jta.append((i)+"\t"+reservadas[i]+"\t");
		    			}else{
		    				if((1+i)%2==0)jta.append((i)+"\t"+caracValidos[i-reservadas.length]+"\n");
		    				else jta.append((i)+"\t"+caracValidos[i-reservadas.length]+"\t");
		    			}
		    		}jf.pack();
		    		jf.setLocation(30, 50);
		        	} }).start();

		if(!error.equals("")){
			error="ERROR(ES) .- \n"+error;		System.out.println(error);		}//	ta.append(error+"\n\n");		}

		ta.append("TOKENS:\n"+tiraTokens+"\n\nDONDE\n"+donde+"\n\nIDENTIFICADORES (88..)+" +
				"\tTipo\n"+identificadores+"\n\nCONSTANTES (80..) + \n"+constantes);
	    System.out.println("TOKENS:\n"+tiraTokens);
		System.out.println("\n\nDONDE\n"+donde);
		System.out.println("\n\nIDENTIFICADORES (88..)+\tTipo\n"+identificadores);
		System.out.println("\n\nCONSTANTES (80..)+\n"+constantes);
	//	JOptionPane.showMessageDialog(null,sp);

	}

	public static void lexemaAtratar(){
		int m=0;
		palabras.add(lexema);
		reserved=false;
		for(int i=0;i<reservadas.length;i++){
			if(lexema.equals(reservadas[i])){
				reserved=true;
				if(i<10){tiraTokens+="0"+i+" "; donde+=("0"+i+"\t"+lexema)+"\n";}
				else{tiraTokens+=i+" ";  donde+=(i+"\t"+lexema)+"\n";}
				break;
			}
		}
		if(!reserved){//Verificar si es un IDENTIFICADOR
			if(!esConstanteComilla&&veriIdent(lexema).equals(lexema)){//si encuentra valido el Identificador:
				if(Ids.size()==0){try{//se agrega a identificadores
					identificadores+=("00"+"\t"+lexema+"\t"+palabras.get(palabras.size()-2)+"\n");
					}catch(Exception e){
						error+="PRIMERA PALABRA DEBE SER \"PROGRAMA\"\n";
						}
					Ids.add(lexema);
					tiraTokens+=IDENTIFICADOR+" 00 ";		donde+=(IDENTIFICADOR+"00"+"\t"+lexema)+"\n";
					}
				else for(m=0;m<Ids.size();m++){
					if(lexema.equals(Ids.get(m))){// si ya estaba registrado el ID
						if(!InicioCod){error+="ID REPETIDO!!!\n\""+lexema+"\"\n";}
							if(m<10)menora10="0";	else menora10="";
							tiraTokens+=IDENTIFICADOR+" "+menora10+(m)+" ";		donde+=(IDENTIFICADOR+menora10+(m)+"\t"+lexema)+"\n";
						break;}
					else if(m==Ids.size()-1){
						if(InicioCod){
							error+="\""+lexema+"\" "+letra+"\" "+registro+"\"\tDeclaracion FUERA DE LUGAR/ID NO DECLARADO\n";
							}
						Ids.add(lexema);
						if(Ids.size()-1<10)menora10="0";	else menora10="";
						identificadores+=menora10+(Ids.size()-1)+"\t"+lexema+"\t"+tipo+"\n";
						tiraTokens+=IDENTIFICADOR+" "+menora10+(Ids.size()-1)+" ";
						donde+=(IDENTIFICADOR+menora10+(Ids.size()-1)+"\t"+lexema)+"\n";
						break;
					}
				}
			}
			else if((fueConstanteComilla||esConstante2||(esConstante3&&InicioCod))){
				boolean esConstante=true;
				if(esConstante3&&!esConstante2&&!fueConstanteComilla){
					try{
						Double.parseDouble(lexema);
					}catch(Exception e){esConstante=false; error+=veriIdent(lexema)+"\n";}
				}

				esConstante3=false;
				esConstante2=false;
				if(esConstante){
					//el siguiente codigo lo
					//cancele para qe hayan Constantes iguales
					// pero dif. tipo ej 1 - Caracter, 1 - entero
/*				if(Cns.size()==0){//se agrega a constantes si es la priemra constante
					try{
						Double.parseDouble(lexema);
						if(lexema.contains("."))
							constantes+=("0"+(Cns.size())+"\t"+lexema+"\tDOBLE\n");
						else
							constantes+=("0"+(Cns.size())+"\t"+lexema+"\tENTERO\n");
					}catch(Exception e){
						if(lexema.length()>1)
							constantes+=("0"+(Cns.size())+"\t"+lexema+"\tCADENA\n");
						else
							constantes+=("0"+(Cns.size())+"\t"+lexema+"\tCARACTER\n");
					};
					Cns.add(lexema);
					tiraTokens+=CONSTANTE+" 0"+(Cns.size()-1)+" ";
					donde+=(CONSTANTE+"0"+(Cns.size()-1)+"\t"+lexema)+"\n";
				}
				else

					for(m=0;m<Cns.size();m++){
						if(lexema.equals(Cns.get(m))){//verificar si se encontro constante
							if(Cns.size()<10)menora10="0";	else menora10="";
							tiraTokens+=CONSTANTE+" "+menora10+(m)+" ";
							donde+=(CONSTANTE+menora10+(m)+"\t"+lexema)+"\n";
							break;
						}
						else if(m==Cns.size()-1){
Este codigo lo cansele por si hay dos consantes iguales, pero dif tipo
		*/
							Cns.add(lexema);//no se encontro constante, se agrega

							if(Cns.size()<=10)menora10="0";	else menora10="";
							tiraTokens+=CONSTANTE+" "+menora10+(Cns.size()-1)+" ";
							donde+=(CONSTANTE+menora10+(Cns.size()-1)+"\t"+lexema)+"\n";
							try{
								//double prueba=
									Double.parseDouble(lexema);
								if(lexema.contains(".")){
									if(fueConstanteComilla)
										if(lexema.length()>1)
											constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCADENA\n";
										else
											constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCARACTER\n";

									else constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tDOBLE\n";

								}else{
									if(fueConstanteComilla){
										if(lexema.length()>1)
											constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCADENA\n";
										else constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCARACTER\n";}
									else constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tENTERO\n";
								}
							}catch(Exception e){
								if(lexema.length()>1)
									constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCADENA\n";
								else
									constantes+=menora10+(Cns.size()-1)+"\t"+lexema+"\tCARACTER\n";
							};

//						}
//					}
				}
			}else{error+=veriIdent(lexema)+"\n";}
		}
	}
}
