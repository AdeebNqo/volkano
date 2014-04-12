/*

Driver class for testing

*/
package control;
import ui.SearchBox;
public class Driver{
	public static void main(String[] args){
		try{
			Controller con = new Controller("127.0.0.1",1200);
			con.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
