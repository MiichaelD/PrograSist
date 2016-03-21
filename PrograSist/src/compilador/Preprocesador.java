package compilador;
//preprocesador que qita TODO hasta comentarios.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.swing.*;

public class Preprocesador {
	public JFileChooser fc=new JFileChooser();
	File Archivo;
	public DataInputStream leer=null;
	public DataOutputStream escribir=null;
	public String prueba,temporal;
	public boolean comentario=false,continuar=false;


	public static void main(String[] args) {
		Preprocesador m=new Preprocesador();
		if(args.length>0){
			for(int i=0;i<args.length;i++)
				m.abrir(new File(args[i]));
			}
		else{
			int p=m.fc.showOpenDialog(null);
	   		if(p==1)return;
			m.abrir(m.fc.getSelectedFile());
			}
		}

	public void abrir(File archivo){
			Archivo=archivo;
			try	{
			if(Archivo!=null){
			leer=new DataInputStream(new FileInputStream(Archivo));
				limpiar();
			}leer.close();

		}catch(Exception ioe){ JOptionPane.showMessageDialog(null,ioe+" en Abrir\n");}
	}

	@SuppressWarnings("deprecation")
	public void limpiar(){try{
		prueba=temporal="";
		while((temporal=leer.readLine())!=null){

			if(temporal.contains("/*")&&temporal.contains("*/")&&!comentario)// bloque de comentarios en una sola linea
				while(temporal.contains("/*")&&temporal.contains("*/")){
						String helper="";
						boolean ayudar=false;
						for(int i=0;i<temporal.length()-1;i++){
								if((temporal.substring(i,2+i)).equals("/*"))
									{helper=temporal.substring(0,i);ayudar=false;i=i+2;}
								else if((temporal.substring(i,2+i)).equals("*/"))ayudar=true;
								if(ayudar){helper+=temporal.substring(2+i,temporal.length()); ayudar=false; break;}
						}temporal=helper;
					}

			else{//bloque de comentarios en varias lineeas
					if(temporal.contains("/*")){
						comentario=true;
						for(int i=0;i<temporal.length()-1;i++)
							if((temporal.substring(i,2+i)).equals("/*"))
								if(i!=0)temporal=temporal.substring(0,i);
								else temporal=" ";
					}
					else if(comentario&&temporal.contains("*/")){
						comentario=false; continuar=false;
						for(int i=0;i<temporal.length()-1;i++)
							if((temporal.substring(i,2+i)).equals("*/"))
								temporal=temporal.substring(2+i,temporal.length());
					}
			}

			if(temporal.contains("//"))//Este IF elimina los comentarios de doble slash
				for(int i=0;i<temporal.length()-1;i++){
					if((temporal.substring(0+i,2+i)).equals("//"))
						temporal=temporal.substring(0,i);
				}



			while(temporal.contains("\t")||temporal.contains("  ")){//para eliminar tabs y dobles espacios
				temporal=temporal.replace("\t"," ");
				temporal=temporal.replace("  "," ");}


			while(temporal.startsWith(" "))temporal=temporal.substring(1,temporal.length());
			while(temporal.endsWith(" "))temporal=temporal.substring(0,temporal.length()-1);

			if((!continuar)&&(!temporal.equals("\n"))&&(!temporal.equals("\r"))//concatenador  de lineas
					&&(!temporal.equals(""))&&(!temporal.equals(" "))){
				prueba=prueba+temporal+"\r";}
			if (comentario==true)continuar=true;
			}
		}catch(Exception ie){JOptionPane.showMessageDialog(null,ie+" en Limpiar\n");}
		escribir();
	}

	public void escribir(){try{
		Archivo=new File(Archivo.getAbsolutePath().substring(0,Archivo.getAbsolutePath().length()-4)+"-limpio.txt");
		escribir=new DataOutputStream(new FileOutputStream(Archivo));
	/*	if(prueba.contains("  ")||prueba.contains("\t"))
			for(int i=0;i<prueba.length()-1;i++){
				if((prueba.substring(0+i,2+i)).equals("  ")){
					prueba=prueba.substring(0,i)+prueba.substring(i+1,prueba.length());--i;}
				if((prueba.substring(0+i,1+i)).equals("\t")){
					prueba=prueba.substring(0,i)+prueba.substring(i+1,prueba.length());--i;}
		}
	*/	escribir.writeBytes(prueba);
		escribir.close();
		JOptionPane.showMessageDialog(null,"Terminado\n");
		}catch(Exception e){JOptionPane.showMessageDialog(null,e+" en escribir");}}

	}