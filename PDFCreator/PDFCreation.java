package pdf;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCreation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		
		String directory = null;
		String wildcard = "*";
		String orientation = "portrait";		
		boolean landscape = false;
		int fontSize = 10;	
		String fontFamily = "arial"; 
		
		// Obtain parameters
		for (int i=0; i<args.length; i++) {			
			if ( args[i].startsWith("-d") ) {
				directory = args[i].substring(2, args[i].length());
			} else if ( args[i].startsWith("-w") ) {
				wildcard = args[i].substring(2, args[i].length());
				if ( wildcard.equals("*.*") ) wildcard = "*";				
			} else if ( args[i].startsWith("-o") ) {
				orientation = args[i].substring(2, args[i].length());
				if ( orientation.equalsIgnoreCase("landscape") ) landscape = true;				
			} else if ( args[i].startsWith("-f") ) {
				fontFamily = args[i].substring(2, args[i].length());
			} else if ( args[i].startsWith("-s") ) {
				fontSize = Integer.parseInt(args[i].substring(2, args[i].length()));
			}
		}
			
		if ( directory != null ) { 
			
			System.out.println("Converting " + directory + File.separatorChar + wildcard + " to PDF files (landscape=" + landscape + ", fontfamily=" + fontFamily + ", fontsize=" + fontSize + ")...");
						
			// Read and convert files
			List<File> files = list(directory, wildcard);				
			for (int i = 0; i < files.size(); i++) {
			    File file = (File)files.get(i);		
			    String type = getContentType(file);
			    if ( (type != null) && type.startsWith("text") ) {			   
			    	if (file.isFile()) {			    	
						if (create(file,landscape,fontFamily,fontSize)) {
							System.out.println("File " + file.getName() + " - PDF created.");
						}		
				    }   	
			    } else {
			    	System.out.println("File " + file.getName() + " - skipped, it's not text (" + type + ")");
			    }
			    
			}			

		} else {
			System.out.println("Usage: java -jar itextpdf.jar -d<directory> -w[wildcard] -o[orientation] -s[fontsize].\nExample: java -jar itextpdf.jar -dC:\\users\\files -w*.txt -oLandscape -fCourier -s10");
		}

	}

	public static String getContentType(File f) {
		try {
			return Files.probeContentType(f.toPath());
		} catch (Exception e) {
			System.out.println("Error reading file " + f.getName() + " - " + e.getMessage());
			return null;		
		}        
       
    }

	public static List<File> list(String directory, String filePattern) {
		  File dir = new File(directory);
		  FileFilter fileFilter = new WildcardFileFilter(filePattern);
		  File[] files = dir.listFiles(fileFilter);
		  List<File> selectedFiles = new ArrayList<File>();
		  for (File file : files) {
		    selectedFiles.add(file);
		  }
		  return selectedFiles;
	}
		
	public static boolean create(File file, boolean landscape, String fontFamily, int fontSize) {
		FileInputStream iStream = null;
		DataInputStream in = null;
		InputStreamReader is = null;
		BufferedReader br = null;
		try {	
			Document pdfDoc = null;
			if ( !landscape ) {
				pdfDoc = new Document();
			} else {
				pdfDoc = new Document(PageSize.LETTER.rotate());
			}
			
			String text_file_name = file.getParent() + File.separatorChar + file.getName().substring(0,file.getName().length()-4) + ".pdf";			
			//PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(text_file_name));
			PdfWriter.getInstance(pdfDoc, new FileOutputStream(text_file_name));
			pdfDoc.open();
			pdfDoc.setMarginMirroring(true);
			pdfDoc.setMargins(36, 72, 108, 180);
			pdfDoc.topMargin();	
			Font normal_font = new Font();
			Font bold_font = new Font();
			bold_font.setStyle(Font.BOLD);
			bold_font.setSize(fontSize);
			bold_font.setFamily(fontFamily);
			normal_font.setStyle(Font.NORMAL);
			normal_font.setSize(fontSize);		
			normal_font.setFamily(fontFamily);
			pdfDoc.add(new Paragraph("\n"));
			if (file.exists()) {
				iStream = new FileInputStream(file);
				in = new DataInputStream(iStream);
				is = new InputStreamReader(in);
				br = new BufferedReader(is);
				String strLine;
				while ((strLine = br.readLine()) != null) {
					Paragraph para = new Paragraph(strLine + "\n", normal_font);
					para.setAlignment(Element.ALIGN_JUSTIFIED);
					pdfDoc.add(para);
				}
			} else {
				System.out.println("File does not exist");
				return false;
			}
			pdfDoc.close();
		}

		catch (Exception e) {
			System.out.println("PDFCreation.create(): exception = " + e.getMessage());
		} finally {

			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
				if (in != null) {
					in.close();
				}
				if (iStream != null) {
					iStream.close();
				}
			} catch (IOException e) {			
				e.printStackTrace();
			}

		}
		return true;
	}

}