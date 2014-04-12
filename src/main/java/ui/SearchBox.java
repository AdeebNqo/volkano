package ui;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class SearchBox extends JPanel{
	private JTextField  searchbox;
	public SearchBox(ImageIcon searchIcon){
		setLayout(new FlowLayout());
		JLabel lbl = new JLabel();
		lbl.setIcon(searchIcon);
		add(lbl);
		searchbox = new JTextField();
		Border roundedBorder = new LineBorder(Color.green, 5, true);
		searchbox.setBorder(roundedBorder);
		add(searchbox);
	}
}
