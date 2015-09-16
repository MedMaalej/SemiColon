package model;

import java.util.Set;

public class ProLang {
	private String plName;
	private Set<String> plKeyWords;

	public ProLang(String plName, Set<String> plKeyWords) {
		super();
		this.plName = plName;
		this.plKeyWords = plKeyWords;
	}

	public String getPlName() {
		return plName;
	}

	public void setPlName(String plName) {
		this.plName = plName;
	}

	public Set<String> getPlKeyWords() {
		return plKeyWords;
	}
	public void setPlKeyWords(Set<String> plKeyWords) {
		this.plKeyWords = plKeyWords;
	}

	public String toString() {
		return plName + " " + plKeyWords.toString();
	}
/* change */

}
