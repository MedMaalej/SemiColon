package syntaxChecker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.ProLang;

public class SyntaxChecker {
	private ProLang language;
	
	public SyntaxChecker(ProLang language) {
		this.language = language;
	}

	public StringBuilder checkAndColor(String text) {
		StringBuilder sb = new StringBuilder();
		String words[] = text.split(" ");
		int i = 0;
		File htmlOutput = new File("code.html");
		FileWriter fw;

		try {
			fw = new FileWriter(htmlOutput.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(fw);
		
		
		while (i<words.length) {
			if (language.getPlKeyWords().contains(words[i])) {
				sb.append(words[i]);
			}
			i++;
		}
		writer.close();
		fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}
	
}
