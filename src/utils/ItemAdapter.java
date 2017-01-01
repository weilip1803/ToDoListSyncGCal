package utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * This class helps write UserItemList into json string and read Json string to
 * create UserItemList object
 * 
 * @@author A0121628L
 */

public class ItemAdapter extends TypeAdapter<UserItemList> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
	 */
	@Override
	public UserItemList read(JsonReader in) throws IOException {
		final UserItemList userTaskList = new UserItemList();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss z yyyy");
		in.beginObject();
		while (in.hasNext()) {
			switch (in.nextName()) {
			case "Username":
				userTaskList.setUserName(in.nextString());
				break;
			case "IdCounter":
				userTaskList.setIdCounter(in.nextLong());
				break;
			case "TaskList":
				in.beginArray();
				ArrayList<Item> taskArrayList = new ArrayList<Item>();
				while (in.hasNext()) {
					in.beginObject();
					final Item task = new Item();
					while (in.hasNext()) {
						switch (in.nextName()) {
						case "Id":
							task.setId(in.nextLong());
							break;
						case "Type":
							task.setType(in.nextString());
							break;
						case "Title":
							task.setTitle(in.nextString());
							break;
						case "Priority":
							task.setPriority(in.nextString());
							break;
						case "Description":
							task.setDescription(in.nextString());
							break;
						case "Label":
							task.setLabel(in.nextString());
							break;
						case "Status":
							task.setStatus(in.nextString());
							break;
						case "IsRecurring":
							task.setRecurring(in.nextBoolean());
							break;
						case "PrevId":
							task.setPrevId(in.nextLong());
							break;
						case "NextId":
							task.setNextId(in.nextLong());
							break;

						case "StartDate":
							try {
								task.setStartDate(formatter.parse(in
										.nextString()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							break;
						case "EndDate":
							try {
								task.setEndDate(formatter.parse(in.nextString()));
							} catch (ParseException e) {
								e.printStackTrace();
							}

							break;

						}
					}
					taskArrayList.add(task);
					in.endObject();
				}
				userTaskList.setTaskArray(taskArrayList);
				in.endArray();
				break;
			}
		}
		in.endObject();

		return userTaskList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter,
	 * java.lang.Object)
	 */
	@Override
	public void write(JsonWriter out, UserItemList userTaskList)
			throws IOException {
		out.beginObject();
		out.name("Username").value(userTaskList.getUserName());
		out.name("IdCounter").value(userTaskList.getIdCounter());
		out.name("TaskList").beginArray();

		if (userTaskList.getTaskArray() != null) {
			ArrayList<Item> taskList = userTaskList.getTaskArray();

			for (final Item task : taskList) {
				out.beginObject();
				out.name("Id").value(task.getId());
				out.name("Type").value(task.getType());
				out.name("Title").value(task.getTitle());
				out.name("Priority").value(task.getPriority());
				out.name("Description").value(task.getDescription());
				out.name("Label").value(task.getLabel());
				out.name("Status").value(task.getStatus());

				if (task.getStartDate() != null) {
					out.name("StartDate").value(task.getStartDate().toString());
				}
				if (task.getEndDate() != null) {
					out.name("EndDate").value(task.getEndDate().toString());
				}
				out.name("IsRecurring").value(task.isRecurring());
				out.name("PrevId").value(task.getPrevId());
				out.name("NextId").value(task.getNextId());
				out.endObject();

			}
			out.endArray();
			out.endObject();

		}
	}
}
