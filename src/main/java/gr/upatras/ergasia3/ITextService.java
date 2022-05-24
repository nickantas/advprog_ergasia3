package gr.upatras.ergasia3;

import java.util.List;

public interface ITextService {
	/**
	* @return all texts
	*/
	List<Text> findAll();
	/**
	* @param p
	* @return the @Text added
	*/
	Text addText(Text p);
}
