package addons.bots.trivial.controller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import addons.bots.trivial.model.Pregunta;

/*
 * Classe utilitzada per a gestionar i llegir els arxius utilitzats per a carregar les preguntes del trivial
 */

public class FileController {
	
	private static Path directoriActual = Paths.get("src/addons/bots/trivial/categories/");
	static List<String> linies = new ArrayList<String>();
	static Pregunta p;
	static List<String> files = new ArrayList<String>();
	public static final String SEPARATOR=";";
	public static final String QUOTE="\"";
	
	public FileController() {	
				
	}
	
	//Mètode utilitzat per recuperar els arxius que hi ha al directori categories
	public List<String> categoriaToList(){
		ArrayList<String> categoriaList = new ArrayList<String>();
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoriActual)) {
			for (Path fitxer : stream) {
				files.add(fitxer.getFileName() + (Files.isDirectory(fitxer) ? "/" : ""));
			}
		} catch (IOException | DirectoryIteratorException e) {
			System.err.println(e);
		}
		System.out.println();
		for (String arxiu : files) {
			categoriaList.add(arxiu.substring(0,arxiu.lastIndexOf(".")));
		}
		return categoriaList;
	}
	
	//Mètode utilitzat per llegir els arxius csv i crear les preguntes
	@SuppressWarnings("finally")
	public List<Pregunta> llegeixCSV(String file){
		String s;
		String fitxer = file+".csv";
		Path path = directoriActual.resolve(fitxer);
		ArrayList<Pregunta> preguntes = new ArrayList<Pregunta>();
		
		BufferedReader br = null;
	      
	      try {
	         br =new BufferedReader(new FileReader(path.toString()));
	         String line = br.readLine();
	         while (null!=line) {
	            String [] fields = line.split(SEPARATOR);
	            p = new Pregunta(fields[0], fields[1],fields[2],fields[3],fields[4],fields[5],fields[6]);
	            preguntes.add(p);
	            line = br.readLine();
	         }
	      } catch (Exception e) {
	    	  System.err.println(e.getMessage());
				return null;
	      } finally {
	         if (null!=br) {
	            try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	         }
	         return preguntes;
	    }
	}	
}

