package component;

import javax.swing.table.*;
import javax.swing.*;

class CenterAlignRenderer extends DefaultTableCellRenderer {
    public CenterAlignRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }
}