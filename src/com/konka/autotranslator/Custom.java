package com.konka.autotranslator;

import java.util.HashSet;

public class Custom {
	private String customName;
	private HashSet<String> language = new HashSet<>();
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getCustomName() {
		return customName;
	}
	public void setLanguage(String language) {
		this.language.add(language);
	}
	public String getLanguage() {
		String languages = "";
		for (String temp : language) {
			languages += temp + " ";
		}
		return languages;
	}
}
