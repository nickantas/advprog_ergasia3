package gr.upatras.ergasia3;

import java.util.ArrayList;
import java.util.List;

public class Text {
	private int id;
	private String text;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public Text(int id, String text) {
		super();
		this.text = text;
	}
}
