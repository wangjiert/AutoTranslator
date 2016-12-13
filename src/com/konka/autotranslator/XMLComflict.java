package com.konka.autotranslator;

public class XMLComflict {
	private String tagName;
	private String english;
	private String language;
	private String oldValue;
	private String newValue;
	public XMLComflict(String tagName, String english, String language, String oldValue, String newValue){
		this.tagName = tagName;
		this.english = english;
		this.language = language;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getName(int index) {
		String result = "";
		switch (index) {
		case 1:
			result = tagName;
			break;
		case 2:
			result = english;
			break;
		case 3:
			result = language;
			break;
		case 4:
			result = oldValue;
			break;
		case 5:
			result = newValue;
			break;

		}
		return result;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
