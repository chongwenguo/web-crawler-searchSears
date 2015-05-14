package searchSears;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * SearchSears class which deals with two queries base on number of inputs.
 * 
 * One argument: keyword
 * it will print out the total amount of products according to the keyword. 
 * 
 * Two arguments: keyword, page number
 * it will print out the all result objects found on the specific page
 * according to the keyword
 * 
 * @author chongwen guo
 * @version 1.0
 */
public class SearchSears {
	private final String HOME = "http://www.sears.com";
	private final String SEARCH = "/search=";
	private String htmlString;
	private ArrayList<SearsResult> results;

	/**
	 * Constructor Initialize the ArrayList<SearsResult> results
	 * 
	 */
	public SearchSears() {
		results = new ArrayList<SearsResult>();
	}

	/**
	 * Query1
	 * 
	 * Given keyword, find searchURL, (if the original searchURL does not
	 * contain the information we need, search for its redirect url) retrieve
	 * HTML as String then find productCOunt
	 * 
	 * @param <String>keyword</String> input as search keyword from user
	 * @return <int>productCount</int> total amount of products for the
	 *         search key
	 */
	public int query1(String keyword) {
		int productCount = 0;
		retrieveSearchResult(keyword);
		// find the index of "productCount = "
		int index1 = htmlString.indexOf("productCount = ");
		// find the " sign to extra the productCount
		int index2 = htmlString.indexOf("\"", index1 + 16);
		try {
			productCount = Integer.parseInt(htmlString.substring(index1 + 16,
					index2));
		} catch (NumberFormatException e) {
			System.out.println("try later");
		}
		return productCount;
	}

	/**
	 * Query2
	 * 
	 * Given keyword, find searchURL, (if the original searchURL does not
	 * contain the information we need, search for its redirect url and replace
	 * it), find url for specific page number, retrieve HTML Document, and
	 * search the Document to find all matched result objects and then add them
	 * into ArrayList<SearsResult> results
	 * 
	 * @param <String>keyword</String> input as search keyword from user
	 * @param <String>pageNum</String> input as page number from user
	 * @return ArrayList<SearsResult> the copy of the ArrayList containing all found
	 *         SearsResult objects
	 */


	public ArrayList<SearsResult> query2(String keyword, String pageNum) {

		Document doc = null;
		doc = retrieveSearchResult(keyword, pageNum);
		// if can't obtain doc based on given keyword, then print out no result found
		if (doc == null || doc.select("div.cardInner").first() == null) {
			System.out.println("No Result Found");
			System.exit(0);
		}
		Elements items = doc.select("div.cardInner");
		Iterator<Element> ite = items.iterator();
		int i = 1;
		while (ite.hasNext()) {
			String title = "";
			String price = "";
			String vendor = "";
			Element item = ite.next();
			// find title of the item
			title = item.getElementsByClass("cardProdTitle").first()
					.getElementsByTag("a").attr("title");
			// find price of the item
			if (item.getElementsByClass("price").first() != null) {
				price = item.getElementsByClass("price").first().html();
			}
			// find vendor of the item
			// default vendor is Sears
			if (item.select("div#mrkplc  p:contains(Sold by)").first() == null) {
				vendor = "Sears";
			} else {
				// String format "Sold by VENDOR"
				vendor = item.select("div#mrkplc  p:contains(Sold by)").first()
						.html().substring(8);
			}
			// create a new SearsResult and then add it to results
			results.add(new SearsResult(i, title, price, vendor));
			i++;
		}
		// print out the result objects stored in results
	
		return (ArrayList<SearsResult>) results.clone();
	}

	/**
	 * using Jsoup to get the document object from given url
	 * 
	 * @param <String>url</String> input URL
	 * @return Document object for the given URL
	 */
	private Document retrieveHTML(String url) {
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * helper method for query1 obtain the Document object for a given keyword
	 * 
	 * @param <String>keyword</String> the search keyword
	 * @return Document object for the given URL
	 */
	private Document retrieveSearchResult(String keyword) {
		String searchURL;
		Document doc = null;
		String kw = keyword.replace(" ", "%20");
		/* get the full URL for further use */
		searchURL = HOME + SEARCH + kw;
		htmlString = retrieveHTML(searchURL).html();
		// handle no result found
		if (htmlString.contains("No Result")) {
			System.out.println("No Result Found");
			System.exit(0);
		}

		// use the string "class=\"cardInner\"" to check if our URL contains
		// the information we need. If the original url does not, go to its
		// redirect url.
		while (!htmlString.contains("class=\"cardInner\"")) {
			searchURL = getRedirectURL(htmlString);
			doc = retrieveHTML(searchURL);
			htmlString = doc.html();
		}
		return doc;
	}

	/**
	 * helper method for query2 to obtain the Document object for given
	 * keyword and page number
	 * 
	 * @param <String>keyword</String> the search keyword
	 * @return Document object for the given URL
	 */
	private Document retrieveSearchResult(String keyword, String pageNum) {
		String searchURL;
		String kw = keyword.replace(" ", "%20");
		searchURL = HOME + SEARCH + kw;
		Document doc = retrieveHTML(searchURL);
		searchURL = doc.location();
		htmlString = doc.html();
		// handle no result found all invalid page number
		Integer pageN = Integer.parseInt(pageNum);
		if (htmlString.contains("No Result") || pageN <=0 || pageN >=21) {
			System.out.println("No Result Found or invalid page number");
			System.exit(0);
		}
		// find the url with the page number
		while (!searchURL.contains("pageNum=" + pageNum)) {
			Elements pages = doc.select("select#pagination option");
			for (Element pageURL : pages) {
				if (pageURL.text().substring(5).equals(pageNum)) {
					searchURL = HOME + pageURL.attr("value");
					// set the view Item to 25
					if(!searchURL.contains("viewItems=")) {
						searchURL = searchURL + "&viewItems=25";
					}
					searchURL = searchURL.replace("viewItems=50",
							"viewItems=25");
					doc = retrieveHTML(searchURL);
					return doc;
				}
			}
			// if the page does not contain the page information,
			// then go to its redirect url
			searchURL = getRedirectURL(htmlString);
			// set the view Item to 25
			if(!searchURL.contains("viewItems=")) {
				searchURL = searchURL + "&viewItems=25";
			}
			searchURL = searchURL.replace("viewItems=50", "viewItems=25");
			doc = retrieveHTML(searchURL);
		}
		return doc;
	}

	/**
	 * helper method for finding redirect url
	 * 
	 * @param <String>htmlString</String> the HTML as String
	 * @return <String>redirectURL</String> redirect URL
	 */
	private String getRedirectURL(String htmlString) {
		String redirectURL = "";
		String URLBegin = "var url";
		String URLEnd = "var path";

		// example redirect URL
		// "\/search=digital watch?catalogId=12605&storeId=10153&levels=
		// Jewelry&autoRedirect=true&viewItems=50&redirectType=CAT_REC_PRED";

		if (htmlString.contains(URLBegin) && htmlString.contains(URLEnd)) {
			
				String url = htmlString.substring(htmlString.indexOf(URLBegin)+12,
							htmlString.indexOf(URLEnd) - 5);
				url = url.replace("\\", "");
				redirectURL = HOME +url;
				if(!redirectURL.contains("viewItems=")) {
					redirectURL = redirectURL + "&viewItems=25";
				}
			redirectURL = redirectURL.replace("viewItems=50", "viewItems=25");
		} else {
			// can't find the redirect url
			System.out.println("Invalid page number or "
					+ "URL of searching page "
					+ "cannot be retrieved at this moment. "
					+ "\nPlese try later.");
			System.exit(-1);
		}
		return redirectURL;
	}

	/**
	 * Main method
	 * 
	 * call query1 or query2 function base on the number of args
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 */
	public static void main(String[] args) {
		if ((args == null) || (args.length < 1)) {
			System.out.println("Please give one or two inputs!");
			return;
		} else if (args.length > 2) {
			System.out.println("At most 2 inputs are allowed!");
		}
		SearchSears scraper = new SearchSears();
		// Query1
		if (args.length == 1) {
			int productCount = scraper.query1(args[0].trim());
			if (productCount == 0) {
				System.out.print("No result found in search.");
			} else {
				System.out.println("The total number of " + args[0].trim()
						+ " is: " + productCount + ".");
			}
		}
		// Query2
		if (args.length == 2) {
			ArrayList<SearsResult> paseResults = scraper.query2(args[0].trim(), args[1].trim());
			for (SearsResult result : paseResults) {
				System.out.println(result);
			}
		}

	}
}