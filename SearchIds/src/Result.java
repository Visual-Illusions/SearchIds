public class Result {
	private String name;
	private int value;
	private int id;

	public Result(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public Result(int value, int id, String name) {
		this.value = value;
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		if (SearchIds.base.equalsIgnoreCase("hex") || SearchIds.base.equalsIgnoreCase("hexadecimal")) {
			return Integer.toHexString(value).toUpperCase();
		} else {
			return String.valueOf(value);
		}
	}

	public String getId() {
		if (SearchIds.baseId.equalsIgnoreCase("hex") || SearchIds.baseId.equalsIgnoreCase("hexadecimal")) {
			return Integer.toHexString(id).toUpperCase();
		} else {
			return String.valueOf(id);
		}
	}
	
	public String getFullValue() {
		if (id == 0) {
			return getValue();
		} else {
			if (id>=10) {
				return getValue()+":"+getId();
			} else {
				return getValue()+":"+getId()+" ";
			}
		}
	}

	public int getValuePad() {
		if (id <= 0) {
			return SearchIds.numWidth;
		} else {
			return SearchIds.numWidth+3;
		}
	}
}