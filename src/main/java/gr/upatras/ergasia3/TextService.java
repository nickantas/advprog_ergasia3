package gr.upatras.ergasia3;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
/**
* @author ctranoris
*
*/
@Service
public class TextService implements ITextService {
    // creating an object of ArrayList
    List<Text> Texts = new ArrayList<Text>();
    int ix = 0;
    /**
    * adding Texts to the List
    */
    public TextService() {
        super();
        Texts.add(new Text(100, "Test text"));
        
    }
    /**
    * returns a list of Text
    */
    @Override
    public List<Text> findAll() {
        return Texts;
    }
    @Override
    public Text addText(Text p) {
        ix = ix +1; //increase Text index
        p.setId( ix );
        Texts.add( p );
        return p;
    }
    
}