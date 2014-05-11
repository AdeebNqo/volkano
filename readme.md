#Volkano

Application for streaming videos from a direct connect hub. Most direct connect clients are centered around users, that is, they provide you with a list of users. Volkano is different, this application is about files. I am trying to provide a client for people who do not care about socialising with other connected users, the focus is on the shared content. It is about the exploration of video; watching other people's videos
without waiting for slow downloads. This application is an attempt to provide a youtube-like platform on top
of the direct connect protocol. It currently does not support the adc protocol.

Currently testing it on

    - opendchub running on port 1200 (Ubuntu 12.10)
    - PtokaX running on port 411 (Windows 7)
    
Please note that there is a 30 second delay in retrieving response from hub so it might be a little slow

##TODO

- [x] Connect to a nmdc hub
- [x] Setup client-client handshake
- [x] Get list of files (saving bz2 files sent in binary)
- [ ] Cleanup src code -- remove debug print statements, etc
- [x] design ui
- [ ] Implement UI
- [ ] retrieve metadata
- [ ] Connect UI and streaming
- [x] index file lists
- [x] add search support
- [ ] Make streaming totally independent of UI, should be easy to change UI
- [ ] Remove wait()'s, improvide responsiveness

##Notes

1. source of subtitles http://imdbpy.sourceforge.net/

2. themoviedb https://github.com/doganaydin/themoviedb/

3. Question on adc handshake http://archive.dcbase.org/dcpp_forums/viewtopic.php?f=6&t=17172

4. Might need to identify type of file http://stackoverflow.com/questions/13044562/python-mechanism-to-identify-compressed-file-type-and-uncompress

5. To download a file which is listed in the FileListing, one can use $ADCGET. However, do not use the filename. Rather use `TTH/<file tth here>`

6. Cannot play in-memory bytes, might have to consider using RTP http://www.cs.odu.edu/~cs778/spring04/lectures/jmfsolutions/examplesindex.html#swing

7. Discussion on playing data from inputstream, xuggle. https://groups.google.com/forum/#!topic/xuggler-users/fOOjmIMK4FY

8. Xuggle demos https://github.com/artclarke/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos

9. Apache lucene  tutorial http://www.lucenetutorial.com/lucene-in-5-minutes.html

10. gstreamer-java playing from inputstream https://code.google.com/p/gstreamer-java/source/browse/trunk/gstreamer-java/src/org/gstreamer/example/InputStreamSrcTest.java?r=315

##Depends

	- xuggle (http://www.xuggle.com)
	- apache lucene (http://lucene.apache.org/)

##Updates

09 Tshazimpuzi 2014: Abandoned python, cannot recieve files using python. Moving to use java
