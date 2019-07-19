package me.zap.arcade.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtil {
	public static String returnContents(File file) {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String cur;
			while ((cur = buffer.readLine()) != null) 
				builder.append(cur).append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
}
