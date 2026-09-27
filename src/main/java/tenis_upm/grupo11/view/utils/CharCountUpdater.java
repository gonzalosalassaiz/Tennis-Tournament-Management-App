package tenis_upm.grupo11.view.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * CharCountUpdater keeps updating the character counter text fields
 */

public class CharCountUpdater implements DocumentListener {
    private JTextComponent textComponent;
    private JLabel label;
    private int maxCharacters;
    
    public CharCountUpdater(JTextComponent textComponent, JLabel label,int maxCharacters) {
    	this.textComponent=textComponent;
    	this.label=label;
    	this.maxCharacters=maxCharacters;
    }
    public void insertUpdate(DocumentEvent e) {
        updateCount();
    }

    public void removeUpdate(DocumentEvent e) {
        updateCount();
    }

    public void changedUpdate(DocumentEvent e) {
        updateCount();
    }

    private void updateCount() {
        label.setText(textComponent.getText().length() + "/" + maxCharacters);
    }
}