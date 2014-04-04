#
# Zola Mahlaza (AdeebNqo)
# http:github.com/AdeebNqo
#
#  "Code is not like other how-computers-work books. It doesn't have big color illustrations
#   of disk drives with arrows showing how the data sweeps into the computer. Code has no
#   drawings of trains carrying a cargo of zeros and ones. Metaphors and similes are wonderful
#   literary devices but they do nothing but obscure the beauty of technology."
#
#   - Charles Petzold
#
#
# Controller for the volkano, the nmdc protocol video stream application
#
import socket

class NoConnectedUsersException(Exception):
	def __init__(self,msg):
		self.msg = msg
	def __str__(self):
		return self.msg
class Controller(object):
	sockt = None
	hubname = None
	nick = 'johndoe'
	passwrd = 'password'

	#
	#Information that will be provided by hub
	#
	nicklist = []
	oplist = []
	hubmsg= None

	#
	# Method for connecting to a dc hub
	# uses the adc protocol as outlined in http://adc.sourceforge.net/ADC.html
	#
	retries = 0
	def connect(self,host,port):
		try:
			self.sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sockt.settimeout(60)
			self.sockt.connect((host,port))
		except socket.timeout:
			retries = retries+1
			if retries<3:
				self.connect(host,port+1)
		print('socket has been established!')
		

if __name__=='__main__':
	controller = Controller()
	controller.connect(socket.gethostbyname_ex('sniper.redirectme.net')[2][0],411)
