package com.konka.autotranslator;

public class Comflict {
	private String sheetName;
	private String tagName;
	private String english;
	private String language;
	private String oldTranslator;
	private String newTranslator;

	public String getName(int index) {
		String result = "";
		switch (index) {
		case 1:
			result = sheetName;
			break;
		case 2:
			result = tagName;
			break;
		case 3:
			result = english;
			break;
		case 4:
			result = language;
			break;
		case 5:
			result = oldTranslator;
			break;
		case 6:
			result = newTranslator;
			break;

		}
		return result;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getOldTranslator() {
		return oldTranslator;
	}

	public void setOldTranslator(String oldTranslator) {
		this.oldTranslator = oldTranslator;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getNewTranslator() {
		return newTranslator;
	}

	public void setNewTranslator(String newTranslator) {
		this.newTranslator = newTranslator;
	}

}
