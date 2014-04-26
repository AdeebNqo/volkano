/*

Driver class for testing

*/
package control;
import ui.SearchBox;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;

public class Driver{
	public static void main(String[] args){
		final Controller con = new Controller("127.0.0.1",1200);
		(new Thread(){
			public void run(){
				try{
					con.connect();
				}catch(Exception e){
					e.printStackTrace();
					System.exit(0);
				}
			}
		}).start();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				frame.setSize(500,500);
				ImageIcon img = new ImageIcon("/home/adeeb/Documents/programming/volkano/res/icons/search.png");
				SearchBox box = new SearchBox(img);
				frame.setLayout(new BorderLayout());
				frame.add(box, BorderLayout.NORTH);
			}
		});
	}
}
