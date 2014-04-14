package ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class SearchBox extends JPanel implements ActionListener, KeyListener{
	private JTextField  searchbox;
	public SearchBox(ImageIcon searchIcon){
		JTextField searchbox = new JTextField();
		searchbox.setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout(1,2));

		JButton searchbutton = new JButton(searchIcon);
		searchbutton.setBorder(BorderFactory.createEmptyBorder());

		add(searchbox, BorderLayout.CENTER);
		add(searchbutton, BorderLayout.EAST);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		searchbox.addKeyListener(this);
		searchbutton.addActionListener(this);
	}
	/*
	Handling button presses
	*/
	public void actionPerformed(ActionEvent e){

	}
	/*
	Handling input from the searchbox
	*/
	public void keyReleased(KeyEvent e){}
	public void keyPressed(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	
}
