package compilador;
//este preprocesador es mas sensillo que el del main, ya que solo
//borra los enters, lineas en blanco, tabs, espacios multiples y es todo.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.swing.*;

public class PreproSensillo {
	static public JFileChooser fc=new JFileChooser();
	static File Archivo;
	static public DataInputStream leer=null;
	static public DataOutputStream escribir=null;
	static public String preprocesado,temporal;


	public static void main(String[] args) {
		if(args.length>0){
			for(int i=0;i<args.length;i++)
				PreproSensillo.abrir(new File(args[i]));
			}
		else{
			int p=PreproSensillo.fc.showOpenDialog(null);
			if(p==1)return;
			PreproSensillo.abrir(PreproSensillo.fc.getSelectedFile());
			}
		}

	public static void abrir(File archivo){
			Archivo=archivo;
			try	{
			if(Archivo!=null){
			leer=new DataInputStream(new FileInputStream(Archivo));
				limpiar();
			}leer.close();

		}catch(Exception ioe){ JOptionPane.showMessageDialog(null,ioe+" en Abrir\n");}
	}

	@SuppressWarnings("deprecation")
	public static void limpiar(){try{
		preprocesado=temporal="";
		while((temporal=leer.readLine())!=null){


			while(temporal.contains("\t")||temporal.contains("  ")){//para eliminar tabs y dobles espacios
				temporal=temporal.replace("\t"," ");
				temporal=temporal.replace("  "," ");}

			while(temporal.startsWith(" "))temporal=temporal.substring(1,temporal.length());
			while(temporal.endsWith(" "))temporal=temporal.substring(0,temporal.length()-1);

			if((!temporal.equals("\n"))&&(!temporal.equals("\r"))&&(!temporal.equals(""))&&(!temporal.equals(" "))){
				preprocesado=preprocesado+temporal+"\r";}
			}
		}catch(Exception ie){JOptionPane.showMessageDialog(null,ie+" en Limpiar\n");}
		//escribir();
	}

	public static void escribir(){try{
		Archivo=new File(Archivo.getAbsolutePath().substring(0,(Archivo.getAbsolutePath().length()))+"-pp.txt");
		escribir=new DataOutputStream(new FileOutputStream(Archivo));
		escribir.writeBytes(preprocesado);
		escribir.close();
		}catch(Exception e){JOptionPane.showMessageDialog(null,e+" en escribir");}}
	}