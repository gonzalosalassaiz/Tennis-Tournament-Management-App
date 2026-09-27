package tenis_upm.grupo11.view.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * LimitedDocument limits the number of characters on a text field
 */

public class LimitedDocument extends PlainDocument {
    private int limit;
    
    public LimitedDocument(int limit) {
    	this.limit=limit;
    }
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str != null && (getLength() + str.length()) <= limit) super.insertString(offset, str, attr);
    }
}