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
	hubname = None
	nick = 'johndoe'
	passwrd = 'password'
	#
	# Method for connecting to a dc hub
	# uses the nmdc protocol as outlined in http://nmdc.sourceforge.net/NMDC.html
	#
	def connect(self,addr):
		self.sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.sockt.connect(addr)
		#executing the steps of the nmdc protocol to connect
		lock = self.sockt.recv(1024)
		lockarray = lock.split('|')
		lock = lockarray[0]
		if (len(lockarray)>1):		
			# Hub does not need authentication
			# Size of lockarray should be two
			hubnamevar = lockarray[1]
			self.hubname = hubnamevar.replace('$HubName ','')
			# Identifying username
			self.sockt.sendall('$ValidateNick {}'.format(self.nick))
			print('sent username...')
			hubresponse = self.sockt.recv(1024)
			print('received response...')
			hubresponse = hubresponse.split('|')
			print('hubresponse is {}'.format(hubresponse))
if __name__=='__main__':
	controller = Controller()
	addr = ('127.0.0.1',1200)
	controller.connect(addr)
