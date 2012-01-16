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
	public static String updateSourceALT = "http://dl.dropbox.com/u/25586491/CanaryPlugins/SearchIds/search-ids-data.xml";
	public static boolean autoUpdate = true;
	public static String searchCommand = "search";
	public static String consoleCommand = "search";
	public static String base = "decimal";
	public static String baseId = "decimal";
	public static int nameWidth = 25;
	public static int numWidth = 4;
	public static String delimiter = "-";
	public static int autoUpdateInterval = 600000;
	public static DataParser parser;
	private UpdateThread updateThread;

	public void enable(){
		etc.getInstance().addCommand("/" + searchCommand, " - Search for a block/item id");
		if (!initProps()) {
			log.severe("[SearchIds] Could not initialize " + propFile);
			etc.getLoader().getPlugin(this.getName()).disable();
			return;
		}

		if (parser == null) {
			parser = new DataParser();
		}
		if (!initData()) {
			log.severe("[SearchIds] Could not init the search data from: " + dataXml + ". Please check that the file exists and is not corrupt.");
			if (!autoUpdate) {
				log.severe("[SearchIds] Set auto-update-data=true in " + propFile + " to automatically download the search data file " + dataXml);
			}
			etc.getLoader().getPlugin(this.getName()).disable();
			return;
		}

		if (autoUpdate) {
			if (updateThread == null){
				updateThread = new UpdateThread(this);
			}
			updateThread.start();
		}
		log.info(name + " " + version + " enabled");
	}

	public void disable() {
		etc.getInstance().removeCommand("/" + searchCommand);
		if (updateThread != null) {
			updateThread.stop();
			updateThread = null;
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

		if (autoUpdateInterval < 60000) {
			autoUpdateInterval = 60000;
			log.warning("[SearchIds] auto-update-interval cannot be less than 60000! auto-update-interval set to 60000");
		}

		File file = new File(propFile);
		return file.exists();
	}

	public boolean initData(){
		if ((dataXml == null) || (dataXml.equals(""))) {
			return false;
		}

		File f = new File(dataXml);
		if ((!updateData(updateSource)) && (!f.exists())) {
			return false;
		}

		return parser.search("test") != null;
	}

	public boolean updateData(String Source){
		if (autoUpdate) {
			try {
				URL url = new URL(Source);
				log.info(name + ": Updating data from " + Source + "...");
				InputStream is = url.openStream();
				FileOutputStream fos = null;
				fos = new FileOutputStream(dataXml);
				int oneChar;
				while ((oneChar = is.read()) != -1){
					fos.write(oneChar);
				}
				is.close();
				fos.close();
				log.info(name + ": Update Successful!");
				return true;
			} catch (MalformedURLException e) {
				if(Source.equals(updateSource)){
					log.warning("[SearchIds] Update from "+ Source+" Failed. Attempting Alternate Source...");
					return updateData(updateSourceALT);
				}
				else{
					log.warning("[SearchIds] Update from "+ Source +" Failed.");
					return false;
				}
			} catch (IOException e) {
				log.warning("[SearchIds] Could not update search data.");
				return false;
			}
		}
		return true;
	}

	public void printSearchResults(Player player, ArrayList<Result> results, String query) {
		if (results != null && !results.isEmpty()) {
			player.sendMessage("§bSearch results for \"" + query + "\":");
			Iterator<Result> itr = results.iterator();
			String line = "";
			int num = 0;
			while (itr.hasNext()) {
				num++;
				Result result = itr.next();
				line += (rightPad(result.getFullValue(), result.getValuePad()) + " " + delimiter + " " +rightPad(result.getName(), nameWidth));
				if (num % 2 == 0 || !itr.hasNext()) {
					player.sendMessage("§6" + line.trim());
					line = "";
				}
				if (num > 16) {
					player.sendMessage("§6Not all results are displayed. Make your term more specific!");
					break;
				}
			}
		}else{
			player.sendMessage("§cNo results found.");
		}
	}
	
	public void printConsoleSearchResults(ArrayList<Result> results, String query) {
		if (results != null && !results.isEmpty()) {
			System.out.println("Search results for \"" + query + "\":");
			Iterator<Result> itr = results.iterator();
			String line = "";
			int num = 0;
			while (itr.hasNext()) {
				num++;
				Result result = itr.next();
				line += (rightPad(result.getFullValue(), result.getValuePad()) + " " + delimiter + " " +rightPad(result.getName(), nameWidth));
				if (num % 2 == 0 || !itr.hasNext()) {
					System.out.println(line.trim());
					line = "";
				}
				if (num > 16) {
					System.out.println("Not all results are displayed. Make your term more specific!");
					break;
				}
			}
		}else{
			System.out.println("No results found.");
		}
	}

	public static String leftPad(String s, int width){
		return String.format("%" + width + "s", s);
	}

	public static String rightPad(String s, int width) {
		return String.format("%-" + width + "s", s);
	}
	public class SearchListener extends PluginListener {
		SearchIds p;

		public SearchListener(SearchIds plugin) { this.p = plugin; }

		public boolean onCommand(Player player, String[] cmd){
			if ((cmd[0].equalsIgnoreCase("/" + SearchIds.searchCommand)) && (player.canUseCommand("/" + SearchIds.searchCommand))) {
				if (cmd.length > 1) {
					String query = "";
					for (int i = 1; i < cmd.length; i++) {
						query = query + cmd[i] + " ";
					}
					query = query.trim();
					SearchIds.this.printSearchResults(player, SearchIds.parser.search(query, SearchIds.base), query);
				} else {
					player.sendMessage("§cCorrect usage is: /" + SearchIds.searchCommand + " [item to search for]");
				}
				return true;
			}
			return false;
		}
		
		public boolean onConsoleCommand(String[] cmd){
			if (cmd[0].equalsIgnoreCase(SearchIds.consoleCommand)){
				if (cmd.length > 1) {
					String query = "";
					for (int i = 1; i < cmd.length; i++) {
						query = query + cmd[i] + " ";
					}
					query = query.trim();
					SearchIds.this.printConsoleSearchResults(SearchIds.parser.search(query, SearchIds.base), query);
				} else {
					System.out.println("Correct usage is: " + SearchIds.consoleCommand + " [item to search for]");
				}
				return true;
			}
			return false;
		}
	}
}