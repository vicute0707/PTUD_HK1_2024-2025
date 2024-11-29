package style;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateFormField {
	
    public JTextField createFormField(String label, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        // TextField
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        panel.add(field, gbc);
        return field;
    }

}
