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
class Controller(object):
	sockt = None
	#
	# Method for connecting to a dc hub
	# uses the nmdc protocol as outlined in http://nmdc.sourceforge.net/NMDC.html
	#
	def connect(self,addr):
		sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		sockt.connect(addr)
		#executing the steps of the nmdc protocol to connect
		lock = sockt.recv(1024)
		print('lock is {}'.format(lock))
if __name__=='__main__':
	controller = Controller()
	addr = ('127.0.0.1',1200)
	controller.connect(addr)
