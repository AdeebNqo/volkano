import javax.mail.util.ByteArrayDataSource;
import javax.media.Player;
import java.awt.Component;
import java.io.InputStream;
import java.net.URLConnection;
import javax.mail.util.ByteArrayDataSource;
import javax.media.Manager;
public class VideoComponent{
	
	Player mediaPlayer;
	Component video;
	Component controls;

	public VideoComponent(InputStream input){
		String mime = URLConnection.guessContentTypeFromStream(input);
		ByteArrayDataSource src = new ByteArrayDataSource(input,mime);
		mediaPlayer = Manager.createRealizedPlayer(src);
		video = mediaPlayer.getVisualComponent();
		controls = mediaPlayer.getControlPanelComponent();
	}
	public Component getComponent(){
		return video;
	}
	public Component getControls(){
		return controls;
	}
}
