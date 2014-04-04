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
	# uses the nmdc protocol as outlined in http://nmdc.sourceforge.net/NMDC.html
	#
	def connect(self,addr):
		self.sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.sockt.connect(addr)
		#executing the steps of the nmdc protocol to connect
		lock = self.sockt.recv(1024)
		print('lock response is {}'.format(lock))
		lockarray = lock.split('|')
		lock = lockarray[0]
		if (len(lockarray)>1):		
			# Hub does not need authentication--kinda
			# Size of lockarray should be two
			hubnamevar = lockarray[1]
			self.hubname = hubnamevar.replace('$HubName ','')
			#
			#ToDo: Assume that key is not required for now, fix that later
			self.sockt.sendall('$Supports UserIP2 TTHSearch ZPipe0 GetZBlock|')
			self.sockt.sendall('$Key 011010110110010101111001|')
			# Identifying username
			self.sockt.sendall('$ValidateNick {}|'.format(self.nick))
			print('sent username...')
			hubresponse = self.sockt.recv(1024)
			hubresponse = hubresponse.split('|')
			length = len(hubresponse)
			decidingVal = hubresponse[length-2] #decides whether hub requires password or it said hello
			if (decidingVal.startswith('$GetPass')):
				print('requires passwpord')
				self.sockt.sendall('$MyPass {}|'.format(self.passwrd))
				hubresponse = self.sockt.recv(1024)
				hubresponse = hubresponse.split('|')
				length = len(hubresponse)
				decidingVal = hubresponse[length-2]
			if (decidingVal.startswith('$Hello')):
				#Hub says hello
				print('hub says hello')
				self.sockt.sendall('$Version 1,0091|$GetNickList|$MyINFO $ALL {} <++ V:0.673,M:P,H:0/1/0,S:2>$$LAN(T3)0x31$example@example.com$1234$|'.format(self.nick))
				hubresponse = self.sockt.recv(9000)
				hubresponse = hubresponse.split('|')
				i=0
				for item in hubresponse:
					if (i==0):
						self.hubmsg = item
						i = i+1
					elif item.startswith('$NickList'):
						self.nicklist = item.replace('$NickList ','').split('$$')
				self.getavailablevids()
	#
	# Method for retrieving all available files for streaming
	#	
	def getavailablevids(self):
		if (len(self.nicklist)==0):
			#query all connected users
			self.sockt.sendall('$GetNickList|')
			users = self.sockt.recv(9000)
			users = users.split('|')
			if (len(users)<=2):
				print('no connected users')
			else:
				print(users)
if __name__=='__main__':
	controller = Controller()
	addr = ('127.0.0.1',1200)
	controller.connect(addr)
