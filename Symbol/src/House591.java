import java.io.IOException;

import org.jsoup.Jsoup;

public class House591 extends SinYi {

	public static void main(String[] args) {		
		House591 house = new House591();
		house.get591House();
		
		house.readSinYi();

		house.getSinYi();
	}
	
	protected void get591House() {
		String doc = "";
		String url = "";
			
		try {
			url = "https://m.591.com.tw/mobile-list.html?version=1&type=sale&regionid=3&sectionidStr=37&kind=9&price=4";
			doc = Jsoup.connect(url).get().html();
			get591(doc, 1);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void get591(String doc, int count) {
		int nameInd = doc.indexOf("data-house-id");
		
		System.out.println(doc.substring(nameInd, nameInd+100));
	}

}
