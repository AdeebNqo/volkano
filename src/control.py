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
	# Hub information
	hubinfo = {}
	#
	# User credentials
	nick = 'sumarairiver'
	password = 'default'
	description = 'here lies dragons'
	email = 'sumarai@testword.ru'

	def xor(self,s1,s2):
		return ''.join(chr(ord(a) ^ ord(b)) for a,b in zip(s1,s2))
	def xor(self,s1,num1):
		return ''.join(chr(ord(s1) ^ num1))

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
		#Let hub speak first
		data = self.sockt.recv(9000)
		for item in data.split('|'):
			if (item!=''):
				whitespacepos = item.find(' ')
				self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
		#		
		#Checking if hub requires key to be sent
		#		
		if (self.hubinfo['$Lock'].startswith('Sending_key_isn\'t_neccessary,_key_won\'t_be_checked')==False):
			#Hub requires key
			#
			# Key will be computed using the info provided here http://nmdc.sourceforge.net/NMDC.html#_key
			#
			lockval = self.hubinfo['$Lock']
			dot = lockval.find('.')
			if (True if dot==-1 else False):
				lock = lockval[lockval.find('$Lock')+1:]
			else:
				lock = lockval[lockval.find('$Lock')+1:dot]
			lenlock = len(lock)
			key = chr(ord(lock[0]) ^ ord(lock[lenlock-1]) ^ ord(lock[lenlock-2]) ^ 5)
			for i in range(1,lenlock):
				key = key+chr(ord(lock[i]) ^ ord(lock[i-1]))
			finalkey = ''			
			for i in range(len(key)):
				finalkey = finalkey+chr(((ord(key[i])<<4) & 240) | ((ord(key[i])>>4) & 15))
			self.sockt.sendall('$Key {}|'.format(finalkey))
		self.sockt.sendall('$ValidateNick {}|'.format(self.nick))
		#
		#Getting response from hub
		response = self.sockt.recv(9000)
		for item in response.split('|'):
			if (item!=''):
				whitespacepos = item.find(' ')
				self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
		if ('$GetPass' in self.hubinfo):
			#
			# if the hub requires authentication
			self.sockt.sendall('$MyPass {}|'.format(self.password))
		#Gained access to the hub
		self.sockt.sendall('$Version 1,0091|')
		self.sockt.sendall('$MyINFO {0} {1}$ $33.6kbps1${2}$5000$|'.format(self.nick,self.description,self.email))
		response = self.sockt.recv(9000)
		for item in response.split('|'):
			if (item!=''):
				whitespacepos = item.find(' ')
				self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
		print(self.hubinfo)
	#
	#  
	#

#
# Utill method for retrieving ip from domain
#
def getip(domain):
	return socket.gethostbyname_ex(domain)[2][0]
if __name__=='__main__':
	controller = Controller()
	controller.connect('127.0.0.1',1200)
