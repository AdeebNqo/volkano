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
		if (self.hubinfo['$Lock'].startswith('ending_key_isn\'t_neccessary,_key_won\'t_be_checked')==False):
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
			key = self.xor(self.xor(self.xor(lock[0],lock[lenlock-1]),lock[lenlock-2]), 5)
			for i in range(1,lenlock):
				key = key+(lock[i] ^ lock[i-1])
			for i in range(len(key)):
				key[i] = ((key[i]<<4) & 240) | ((key[i]>>4) & 15)
			
			
				

#
# Utill method for retrieving ip from domain
#
def getip(domain):
	return socket.gethostbyname_ex(domain)[2][0]
if __name__=='__main__':
	controller = Controller()
	controller.connect('127.0.0.1',1200)
