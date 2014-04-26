import javax.media.Player;
import java.awt.Component;
import java.io.InputStream;
import java.net.URLConnection;
import javax.media.Manager;
import java.io.IOException;
import javax.media.protocol.DataSource;
import java.io.OutputStream;
import java.net.URL;

public class VideoComponent{
	
	Player mediaPlayer;
	Component video;
	Component controls;

	public VideoComponent(InputStream input, String ip, int port){
		try{
			//String mime = URLConnection.guessContentTypeFromStream(input);
			mediaPlayer = Manager.createRealizedPlayer(Manager.createDataSource(new URL("http://"+ip+":"+port)));
			video = mediaPlayer.getVisualComponent();
			controls = mediaPlayer.getControlPanelComponent();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Component getComponent(){
		return video;
	}
	public Component getControls(){
		return controls;
	}
}
