package command;

/**
 * @@author wen hao
 *
 */
import gui.GUIModel;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import main.POMPOM;
import utils.Item;

public class SearchCommand extends Command {

	private static final String MESSAGE_SEARCH = "Search resulted in %s result(s).";
	private static final double PERCENT_TO_ACCEPT = 60.0;

	public ArrayList<Item> searchResults;
	private String keyword;
	private ArrayList<String> keywordTokens;

	public SearchCommand(String keyword) {
		this.searchResults = new ArrayList<Item>();
		this.keyword = keyword;
		this.keywordTokens = tokenize(keyword);
	}

	private ArrayList<Item> search() {

		ArrayList<Item> taskList = getTaskList();
		boolean toAdd = false;
		for (int i = 0; i < taskList.size(); i++) {

			Item currentTask = taskList.get(i);
			ArrayList<String> taskTitleTokens = tokenize(currentTask.getTitle());
			for (int j = 0; j < taskTitleTokens.size(); j++) {
				for (int k = 0; k < keywordTokens.size(); k++) {
					
					String titleToken = taskTitleTokens.get(j);
					String keyToken = keywordTokens.get(k);

					double percentSimilarity = computeStrSimilarity(titleToken, keyToken);
					if (titleToken.contains(keyToken) || percentSimilarity >= PERCENT_TO_ACCEPT) {
						toAdd = true;
						break;
					}

				}

				if (toAdd) {
					break;
				}
						
			}

			if (toAdd) {
				searchResults.add(currentTask);
				toAdd = false;
			}

		}

		return searchResults;

	}

	private static ArrayList<String> tokenize(String keyword) {

		StringTokenizer tokenizer = new StringTokenizer(keyword);
		ArrayList<String> strTokens = new ArrayList<String>();

		// Tokenizes the search String taken in
		while (tokenizer.hasMoreTokens()) {
			strTokens.add(tokenizer.nextToken());
		}
		return strTokens;
	}

	public static int computeEditDistance(String s1, String s2) {

		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		int[] costToChange = new int[s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costToChange[j] = j;
				} else {
					if (j > 0) {
						int newValue = costToChange[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
							newValue = Math.min(Math.min(newValue, lastValue), costToChange[j]) + 1;
						}
						costToChange[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0) {
				costToChange[s2.length()] = lastValue;
			}
		}
		return costToChange[s2.length()];
	}

	public static double computeStrSimilarity(String s1, String s2) {
		// s1 should always be bigger, for easy check thus the swapping.
		if (s2.length() > s1.length()) {
			String tempStr = s1;
			s1 = s2;
			s2 = tempStr;
		}

		int MAX_PERCENT = 100;
		int MAX_LENGTH = s1.length();

		if (MAX_LENGTH == 0) {
			return MAX_PERCENT;
		}
		return ((MAX_LENGTH - computeEditDistance(s1, s2)) / (double) MAX_LENGTH) * MAX_PERCENT;
	}

	public String execute() {

		POMPOM.setSearchList(search());
		POMPOM.setCurrentTab(POMPOM.LABEL_SEARCH);
		GUIModel.update();
		logger.log(Level.INFO, "SearchCommand has be executed");
		returnMsg = String.format(MESSAGE_SEARCH, searchResults.size());
		return returnMsg;

	}

}
