/*

Driver class for testing

*/
package control;
import ui.SearchBox;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;

import org.apache.lucene.document.Document;

public class Driver{
	public static void main(String[] args){
		try{
			final Controller con = new Controller("127.0.0.1",1200);
			(new Thread(){
				public void run(){
					try{
						con.connect();
						Document[] docs = con.search(".avi");
						int i =0;
						for (Document doc:docs){
							System.err.println(i+" "+doc.get("Name")+" "+doc.get("TTH"));
							++i;
						}
					}catch(Exception e){
						e.printStackTrace();
						System.exit(0);
					}
				}
			}).start();
		}catch(IOException e){
			e.printStackTrace();
		}
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
	public static void streamFile(InputStream in, JLabel videostage) throws Exception{
		//streaming video
		IContainer container = IContainer.make();
		IContainerFormat containerFormat_live = IContainerFormat.make();
		if (container.open(in, containerFormat_live)>0){
			int numstreams = container.getNumStreams();
			boolean video = false;
			int streamnum = -1;
			IStreamCoder decoder = null;
			for (int i=0; i<numstreams; ++i){
				IStream stream = container.getStream(i);
				//get decoder
				decoder= stream.getStreamCoder();
				//checking if we've found the video stream				
				if (decoder.getCodecType()== ICodec.Type.CODEC_TYPE_VIDEO){
					video = true;
					streamnum = i;
					break;
				}
			}
			//trying to open and read video from stream
			if (decoder.open() >= 0){
				IVideoResampler resampler = null;
				IStreamCoder videoCoder = decoder; //too tired to clean this up
				if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24){
					// if this stream is not in BGR24, we're going to need to
					// convert it.
					resampler = IVideoResampler.make(videoCoder.getWidth(),
					videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());

					//could not create resampler
					if (resampler==null){
						throw new RuntimeException("could not create color space resampler.");
					}
				}
			
				//inspective packets in container
				IPacket packet = IPacket.make();
				long firstTimestampInStream = Global.NO_PTS;
				long systemClockStartTime = 0;
				for (;container.readNextPacket(packet) >= 0;){
					//making sure packet belongs to video stream
					if (packet.getStreamIndex()==streamnum){
						IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
						videoCoder.getWidth(), videoCoder.getHeight());

						int offset = 0;
						while(offset < packet.getSize()){
							int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
							if (bytesDecoded < 0){
								throw new RuntimeException("error decoding video");
							}
							offset += bytesDecoded;
							if (picture.isComplete()){
								IVideoPicture newPic = picture;
								//resampling
								if (resampler != null){
									// we must resample
									newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
									picture.getWidth(), picture.getHeight());
									if (resampler.resample(newPic, picture) < 0){
										throw new RuntimeException("could not resample video.");
									}
									if (newPic.getPixelType() != IPixelFormat.Type.BGR24){
										throw new RuntimeException("could not decode video as BGR 24 bit data");
										}
									
									if (firstTimestampInStream == Global.NO_PTS){
										// This is our first time through
										firstTimestampInStream = picture.getTimeStamp();
										// get the starting clock time so we can hold up frames
										// until the right time.
										systemClockStartTime = System.currentTimeMillis();
									}
									else{
										long systemClockCurrentTime = System.currentTimeMillis();
										long millisecondsClockTimeSinceStartofVideo =
										systemClockCurrentTime - systemClockStartTime;
										// compute how long for this frame since the first frame in the
										// stream.
										// remember that IVideoPicture and IAudioSamples timestamps are
										// always in MICROSECONDS,
										// so we divide by 1000 to get milliseconds.
										long millisecondsStreamTimeSinceStartOfVideo =
										(picture.getTimeStamp() - firstTimestampInStream)/1000;
										final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
										final long millisecondsToSleep =
										(millisecondsStreamTimeSinceStartOfVideo -
										(millisecondsClockTimeSinceStartofVideo +
										millisecondsTolerance));
										if (millisecondsToSleep > 0){
											try
											{
												Thread.sleep(millisecondsToSleep);
											}
											catch (InterruptedException e)
											{
												// we might get this when the user closes the dialog box, so
												// just return from the method.
												return;
											}

											// And finally, convert the BGR24 to an Java buffered image
											BufferedImage javaImage = Utils.videoPictureToImage(newPic);
											videostage.setIcon(new ImageIcon(javaImage));
										}

									}
								}
							}
						}
					}
				}
			}
			else{
				//decoding video failed.
			}
		}
		else{
			//could not process stream
		}
	}
}
