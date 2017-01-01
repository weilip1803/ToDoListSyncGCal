package utils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.ocpsoft.prettytime.shade.org.apache.commons.lang.WordUtils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

/**
 * This is a holder object to contain the event or task details contains getters
 * and setters and constructor only
 * 
 * @@author A0121628L
 */
// implements Comparator<Item>
public class Item {
	private Long id;
	private String title;
	private String type;
	private String priority;
	private String description;
	private String status;
	private String label;
	private Date startDate;
	private Date endDate;
	private String sd;

	private String ed;
	private Boolean checkBox;

	private boolean isRecurring;
	private Long prevId;
	private Long nextId;

	public Item(Long id, String type, String title, String priority,
			String description, String status, String label, Date startDate,
			Date endDate) {
		super();
		this.id = id;
		this.type = type;
		this.title = title; 
		this.priority = priority;
		this.description = description;
		this.status = status;
		this.label = label;
		this.startDate = startDate;
		this.endDate = endDate;
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		if (this.startDate != null) {
			setSd(sdf.format(startDate));
		}
		if (this.endDate != null) {
			setEd(sdf.format(endDate));
		}

	}

	public Item() {

	}

	private SimpleBooleanProperty checked = new SimpleBooleanProperty(false);

	public SimpleBooleanProperty checkedProperty() {
		return this.checked;
	}

	public Boolean getChecked() {
		return this.checkedProperty().get();
	}

	public void setChecked(final Boolean checked) {
		this.checkedProperty().set(checked);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long i) {
		this.id = i;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = WordUtils.capitalize(priority);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		if (startDate == null) {
			setSd(null);
			this.startDate = startDate;
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		setSd(sdf.format(startDate));
		this.startDate = startDate;
	}

	public String getSd() {
		return sd;
	}

	public void setSd(String sd) {
		this.sd = sd;
	}

	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		if (endDate == null) {
			setEd(null);
			this.endDate = endDate;
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		setEd(sdf.format(endDate));
		this.endDate = endDate;
	}

	public Boolean getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(Boolean checkBox) {
		this.checkBox = checkBox;
	}

	public boolean isRecurring() {
		return isRecurring;
	}

	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public Long getPrevId() {
		return prevId;
	}

	public void setPrevId(Long prevId) {
		this.prevId = prevId;
	}

	public Long getNextId() {
		return nextId;
	}

	public void setNextId(Long nextId) {
		this.nextId = nextId;
	}

	// Debugging Method
	// public void printInfo() {
	// System.out.println("");
	// System.out.println("Task ID: " + getId());
	// System.out.println("Type: " + getType());
	// System.out.println("Title: " + getTitle());
	// System.out.println("Priority: " + getPriority());
	// System.out.println("Description: " + getDescription());
	// System.out.println("Status: " + getStatus());
	// System.out.println("Label: " + getLabel());
	// System.out.println("StartDate: " + getStartDate());
	// System.out.println("EndDate: " + getEndDate());
	// System.out.println("");
	// }

}
