
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Symbol {

	public static void main(String[] args) {

		try {
			FileReader fr = new FileReader("C:\\logs\\schwab.txt");
			FileWriter fw = new FileWriter("C:\\logs\\file1.txt");
			
			BufferedReader br = new BufferedReader(fr);
			
			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				if (sCurrentLine.length() < 6) {
					String dest = sCurrentLine.trim();
					fw.write("\"" + dest + "\", "); 
				}
			}
			fr.close();
			fw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
