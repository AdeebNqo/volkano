public class VideoItem{
	private ImageIcon cover;
	private String name;
	private description;
	private String type;
	public VideoItem(){

	}
	
	public void setCoverArt(ImageIcon cover){
		this.cover = cover;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public void setType(String type){
		this.type = type;
	}

	public String getName(){
		return name;
	}
	public ImageIcon getCoverArt(){
		return cover;
	}
	public String type(){
		return type;
	}
	public String getDescription(){
		return description;
	}
}
