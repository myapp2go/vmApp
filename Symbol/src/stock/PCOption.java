package stock;

import java.io.IOException;
import java.util.Vector;

import org.jsoup.Jsoup;

public class PCOption {

	private static int timeout = 5000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("PCOption");
		
		getStockList();
	}

	private static void getStockList() {
		// TODO Auto-generated method stub
		String doc = "";
		
		try {
			doc = Jsoup.connect("https://biz.yahoo.com/research/earncal/20161104.html").timeout(timeout).get().html();
			Vector list = getList(doc);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private static Vector getList(String doc) {
		// TODO Auto-generated method stub
		int ind = doc.indexOf("My<br>Calendar");
		
		ind = doc.indexOf("finance", ind);

		int count = 0;
		while (ind > 0 && count < 200) {
			int start = doc.indexOf(">", ind);
			int end = doc.indexOf("<", start);
			String name = doc.substring(start+1, end);
			System.out.println("PC " + name);
			
			ind = doc.indexOf("finance", end);
			count++;
		}
		
		return null;
	}

}
