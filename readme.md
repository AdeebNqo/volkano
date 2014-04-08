#Volkano

Application for streaming videos from a nmdc hub. Does not support the adc protocol.

Currently testing it on

    - opendchub running on port 1200 (Ubuntu 12.10)
    - PtokaX running on port 411 (Windows 7)
    
Please note that there is a 30 second delay in retrieving response from hub so it might be a little slow

#TODO

- [X] Connect to a nmdc hub
- [X] Setup client-client handshake
- [ ] Get list of files (saving bz2 files sent in binary)
- [ ] Cleanup src code -- remove debug print statements, etc
- [ ] Index files in search server
- [ ] design ui
- [ ] retrieve metadata
- [ ] Connect UI and streaming

#Notes

source of subtitles http://imdbpy.sourceforge.net/

themoviedb https://github.com/doganaydin/themoviedb/

Question on adc handshake http://archive.dcbase.org/dcpp_forums/viewtopic.php?f=6&t=17172

Might need to identify type of file http://stackoverflow.com/questions/13044562/python-mechanism-to-identify-compressed-file-type-and-uncompress
