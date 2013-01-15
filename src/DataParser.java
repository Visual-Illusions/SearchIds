import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataParser {
	protected static final Logger log = Logger.getLogger("Minecraft");
	public DataParser() {
	}
	
	public ArrayList<Result> search(String query) {
		return search(query, "decimal");
	}

	public ArrayList<Result> search(String query, String base) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DataHandler handler = new DataHandler();
			handler.setPattern(Pattern.compile(".*?"+Pattern.quote(query)+".*", Pattern.CASE_INSENSITIVE));
			saxParser.parse(SearchIds.dataXml, handler);
			return handler.getResults();
		} catch (Exception e) {
			log.warning("[SearchIds] An error occurred while getting Results");
		}
		return null;
	}

	class DataHandler extends DefaultHandler {
		boolean data = false;
		boolean blocks = false;
		boolean items = false;
		boolean item = false;
		private Pattern pattern;
		private ArrayList<Result> results = new ArrayList<Result>();

		public void setPattern (Pattern pattern) {
			this.pattern = pattern;
		}

		public ArrayList<Result> getResults() {
			return results;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("DATA")) {
				data = true;
			}

			if (qName.equalsIgnoreCase("BLOCKS")) {
				blocks = true;
			}

			if (qName.equalsIgnoreCase("ITEMS")) {
				items = true;
			}

			if (qName.equalsIgnoreCase("ITEM")) {
				item = true;

				if (SearchIds.searchType.equalsIgnoreCase("all") ||
						(SearchIds.searchType.equalsIgnoreCase("blocks") && blocks == true) ||
						(SearchIds.searchType.equalsIgnoreCase("items") && items == true)){

					String name = attributes.getValue("name");
					String value = attributes.getValue("dec");
					String id = attributes.getValue("id");

					if (name != null && value != null) {
						if (pattern.matcher(name).matches()) {
							if (id != null) {
								results.add(new Result(Integer.valueOf(value), Integer.valueOf(id), name));
							} else {
								results.add(new Result(Integer.valueOf(value), name));
							}
						}
					} else {
						log.warning("[SearchIds] Name or value is null on an item");
					}
				}
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("DATA")) {
				data = false;
			}

			if (qName.equalsIgnoreCase("BLOCKS")) {
				blocks = false;
			}

			if (qName.equalsIgnoreCase("ITEMS")) {
				items = false;
			}

			if (qName.equalsIgnoreCase("ITEM")) {
				item = false;
			}
		}
	}
}