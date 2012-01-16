import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SearchIds extends Plugin{
	private SearchIds.SearchListener l = new SearchIds.SearchListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static String name = "SearchIds";
	public static String version = "2.3";

	private static String propFile = "plugins/config/SearchIds/search-ids.properties";
	private static PropertiesFile props;
	public static String searchType = "all";
	public static String dataXml = "plugins/config/SearchIds/search-ids-data.xml";
	public static String updateSource = "http://www.visualillusionsent.net/SearchIds/search-ids-data.xml";
	public static String updateSourceALT = "";
	public static boolean autoUpdate = true;
	public static String searchCommand = "search";
	public static String consoleCommand = "search";
	public static String base = "decimal";
	public static String baseId = "decimal";
	public static int nameWidth = 24;
	public static int numWidth = 4;
	public static String delimiter = "-";
	public static int autoUpdateInterval = 86400;
	public static DataParser parser;
	private UpdateThread updateThread;

	public void enable(){
		log.info(name + " " + version + " enabled");
		if (!initProps()) {
			log.severe(name + ": Could not initialise " + propFile);
			disable();
			return;
		}

		if (parser == null) {
			parser = new DataParser();
		}
		if (!initData()) {
			log.severe(name + ": Could not init the search data from: " + dataXml + ". Please check that the file exists and is not corrupt.");
			if (!autoUpdate) {
				log.severe(name + ": Set auto-update-data=true in " + propFile + " to automatically download the search data file " + dataXml);
			}
			disable();
			return;
		}

		if (autoUpdate) {
			if (this.updateThread == null)
				this.updateThread = new UpdateThread(this);
			this.updateThread.start();
		}
		etc.getInstance().addCommand("/" + searchCommand, " - Search for a block id");
	}

	public void disable() {
		etc.getInstance().removeCommand("/" + searchCommand);
		if (this.updateThread != null) {
			this.updateThread.stop();
			this.updateThread = null;
		}
		parser = null;
		log.info(name + " " + version + " disabled");
	}

	public void initialize() {
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, this.l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, this.l, this, PluginListener.Priority.MEDIUM);
	}

	public boolean initProps() {
		File dir = new File("plugins/config/SearchIds");
		if(!dir.exists()){
			dir.mkdirs();
		}
		props = new PropertiesFile(propFile);

		searchType = props.getString("search-type", searchType);
		base = props.getString("base", base);
		searchCommand = props.getString("command", searchCommand);
		consoleCommand = props.getString("console-command", consoleCommand);
		dataXml = props.getString("data-xml", dataXml);
		updateSource = props.getString("update-source", updateSource);
		updateSourceALT = props.getString("update-source-Alternate", updateSourceALT);
		autoUpdate = props.getBoolean("auto-update-data", autoUpdate);
		autoUpdateInterval = props.getInt("auto-update-interval", autoUpdateInterval);
		nameWidth = props.getInt("width-blockname", nameWidth);
		numWidth = props.getInt("width-number", numWidth);
		delimiter = props.getString("delimiter", delimiter);

		if (autoUpdateInterval < 600) {
			autoUpdateInterval = 600;
			log.info(name + ": auto-update-interval cannot be less than 600");
		}

		File file = new File(propFile);
		return file.exists();
	}

	public boolean initData(){
		if ((dataXml == null) || (dataXml.equals(""))) {
			return false;
		}

		File f = new File(dataXml);
		if ((!updateData()) && (!f.exists())) {
			return false;
		}

		return parser.search("test") != null;
	}

	public boolean updateData(){
		if (autoUpdate) {
			try {
				URL url = new URL(updateSource);
				log.info(name + ": Updating data from " + updateSource + "...");
				InputStream is = url.openStream();
				FileOutputStream fos = null;
				fos = new FileOutputStream(dataXml);
				int oneChar;
				while ((oneChar = is.read()) != -1){
					fos.write(oneChar);
				}
				is.close();
				fos.close();
				log.info(name + ": Update complete!");
				return true;
			} catch (MalformedURLException e) {
				log.warning("Update from "+ updateSource+" Failed. Attempting Alternate Source...");
				return updateDataAlt();
			} catch (IOException e) {
				log.severe(e.toString());
			}
			log.warning(name + ": Could not update search data.");
			return false;
		}
		return true;
	}
	
	public boolean updateDataAlt(){ //Alternate Update Source
		if (autoUpdate) {
			try {
				URL url = new URL(updateSourceALT);
				log.info(name + ": Updating data from " + updateSourceALT + "...");
				InputStream is = url.openStream();
				FileOutputStream fos = null;
				fos = new FileOutputStream(dataXml);
				int oneChar;
				while ((oneChar = is.read()) != -1){
					fos.write(oneChar);
				}
				is.close();
				fos.close();
				log.info(name + ": Update complete!");
				return true;
			} catch (MalformedURLException e) {
				log.warning(e.toString());
			} catch (IOException e) {
				log.severe(e.toString());
			}
			log.warning(name + ": Could not update search data.");
			return false;
		}
		return true;
	}

	public void printSearchResults(Player player, ArrayList<Result> results, String query) {
		if (results.size() > 0) {
			player.sendMessage("�bSearch results for \"" + query + "\":");
			Iterator<Result> itr = results.iterator();
			String line = "";
			int num = 0;
			while (itr.hasNext()) {
				num++;
				Result result = itr.next();
				line += (rightPad(result.getFullValue(), result.getValuePad()) + " " + delimiter + " " +rightPad(result.getName(), nameWidth));
				if (num % 2 == 0 || !itr.hasNext()) {
					player.sendMessage("�6" + line.trim());
					line = "";
				}
				if (num > 16) {
					player.sendMessage("�6Not all results are displayed. Make your term more specific!");
					break;
				}
			}
		}else{
			player.sendMessage("�cNo results found");
		}
	}

	public static String leftPad(String s, int width){
		return String.format("%" + width + "s", new Object[] { s });
	}

	public static String rightPad(String s, int width) {
		return String.format("%-" + width + "s", new Object[] { s });
	}
	public class SearchListener extends PluginListener {
		SearchIds p;

		public SearchListener(SearchIds plugin) { this.p = plugin; }

		public boolean onCommand(Player player, String[] split){
			if ((split[0].equalsIgnoreCase("/" + SearchIds.searchCommand)) && (player.canUseCommand("/" + SearchIds.searchCommand))) {
				if (split.length > 1) {
					String query = "";
					for (int i = 1; i < split.length; i++) {
						query = query + split[i] + " ";
					}
					query = query.trim();
					SearchIds.this.printSearchResults(player, SearchIds.parser.search(query, SearchIds.base), query);
				} else {
					player.sendMessage("�cCorrect usage is: /" + SearchIds.searchCommand + " [item to search for]");
				}
				return true;
			}
			return false;
		}
	}
}