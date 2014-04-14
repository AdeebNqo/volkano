/*

Driver class for testing

*/
package control;
import ui.SearchBox;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
public class Driver{
	public static void main(String[] args){
		try{
			Controller con = new Controller("127.0.0.1",1200);
			con.connect();
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
