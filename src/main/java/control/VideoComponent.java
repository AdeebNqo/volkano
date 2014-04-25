import javax.mail.util.ByteArrayDataSource;
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
