package searchSears;

/**
 * SearsResult class represents a searsResult object
 * 
 * @author chongwen guo
 * @version 1.0
 */
public class SearsResult {
	private int index;
	private String title;
	private String price;
	private String vendor;

	/**
	 * Constructor 
	 * 
	 * instantiate a searsResult object
	 * 
	 * @param <String>title </String> title of of a product
	 * @param <String>price </String> price of of a product
	 * @param <String>vendor </String> vendor of of a product
	 * @param <int>index</int> index of item shown in a specific page
	 * 
	 */
	public SearsResult(int index, String title, String price, String vendor) {
		this.index = index;
		this.title = title;
		this.price = price;
		this.vendor = vendor;
	}

	/**
	 * getter
	 * 
	 * @return <String>title</String> title of of this product
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * getter
	 * 
	 * @return <String>price</String> price of of this product
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * getter
	 * 
	 * @return <String>vendor</String> vendor of this product
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * Mutator method
	 * @param <String>title</String> the title to be set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * Mutator method
	 * @param <String>price</String> the price to be set
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	/**
	 * Mutator method
	 * @param <String>vendor</String> the vendor to be set
	 */
	public void setVentors(String vendor) {
		this.vendor = vendor;
	}

	/**
	 * toString() method
	 * 
	 * @return formatted String result
	 */
	public String toString() {
		return "Item " + index + ":\nTitle: " + title + "\nPrice: " + price
				+ "\nVendor: " + vendor + "\n"
				+ "_______________________________\n";
	}

}