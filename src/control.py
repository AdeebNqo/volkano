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
			print('trying to connect...')
			self.sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sockt.settimeout(60)
			self.sockt.connect((host,port))
		except (socket.timeout,socket.error) as err:
			self.retries = self.retries+1
			if self.retries<3:
				self.connect(host,port+1)
		if (self.retries<3):
			print('socket has been established!')
			try:
				#Let hub speak first
				data = self.sockt.recv(9000)
				for item in data.split('|'):
					if (item!=''):
						whitespacepos = item.find(' ')
						self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
				print('the hubinfo stored so far is {}'.format(self.hubinfo))
				#		
				#Checking if hub requires key to be sent
				#
				if (self.hubinfo['$Lock'].startswith('Sending_key_isn\'t_neccessary,_key_won\'t_be_checked')==False):
					#Hub requires key
					#
					# Key will be computed using the info provided here http://nmdc.sourceforge.net/NMDC.html#_key
					#
					lockval = self.hubinfo['$Lock']
					dot = lockval.find(' ')
					if (True if dot==-1 else False):
						lock = lockval[lockval.find('$Lock')+1:]
					else:
						lock = lockval[lockval.find('$Lock')+1:dot]
					print('actual lock val is {}'.format(lock))
					lenlock = len(lock)
					lock = bytearray(lock)
					print('lock is {0}, length:{1}'.format(lock,lenlock))
					key = chr(lock[0] ^ lock[lenlock-1] ^ lock[lenlock-2] ^ 5)
					for i in range(1,lenlock):
						key = key+chr(lock[i] ^ lock[i-1])		
					finkey = ''
					for i in range(len(key)):
						finkey=finkey+chr(((ord(key[i]) & 0x0F) << 4) | ((ord(key[i]) & 0xF0) >> 4))
					print('key is {0}, length:{1}'.format(finkey,len(finkey)))
					self.sockt.sendall('$Key {}|'.format(finkey))
				self.sockt.sendall('$ValidateNick {}|'.format(self.nick))
				#
				#Getting response from hub
				print('getting response from hub..')
				self.sockt.setblocking(1)
				self.sockt.settimeout(30)
				try:
					data =''
					while True:
						response = self.sockt.recv(1024)
						print('response is {}'.format(response))
						if (not response):
							break
						else:
							data = data+response
				except socket.timeout:
					pass
				for item in data.split('|'):
					if (item!=''):
						whitespacepos = item.find(' ')
						self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
				if ('$GetPass' in self.hubinfo):
					#
					# if the hub requires authentication
					print('sending password')
					self.sockt.sendall('$MyPass {}|'.format(self.password))
				print('hubinfo is {}'.format(self.hubinfo))
				#Gained access to the hub
				self.sockt.sendall('$Version 1,0091|')
				self.sockt.sendall('$MyINFO $ALL {0} <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31${1}$1234$|'.format(self.nick,self.email))
				print('getting response after sending myinfo..')
				response = self.sockt.recv(9000)
				for item in response.split('|'):
					if (item!=''):
						whitespacepos = item.find(' ')
						self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
				print(self.hubinfo)
				print('Now getting file lists...')
				self.getfiles()
			except socket.timeout:
				#
				# We have to use adc instead of nmdc
				print('hub does not support nmdc. Defaulting to adc...')
		else:
			raise Exception('Connection failed.')
	#
	# get files  
	#
	def getfiles(self):
		self.sockt.sendall('$GetNickList|')
		data = ''
		try:
			while True:
				response = self.sockt.recv(1024)
				print('response is {}'.format(response))
				if (not response):
					break
				else:
					data = data+response
		except socket.timeout:
			pass
		data = data.split('|')
		
		#other logged in users
		users = []
		for item in data:
			if item.startswith('$NickList'):
				#getting all video files from the logged in users
				users = item.replace('$NickList ','').split('$$')
				for user in users:
					if (user!='' or user!=self.nick):
						users.append(user)
			elif item.startswith('$MyINFO $ALL'):
				tmp = item.split(' ')
				if (tmp[2] !=self.nick):
					users.append(tmp[2])
		print('logged in users are {}'.format(users))
		#
		# Processing all the logged in users using seperate threads
		#
		# -get file list of user and index it
		
	#
	#Method for receiving data from specific socket
	def recv2(self,somesocket):
		data = '';
		try:
			while True:
				response = somesocket.recv(1024)
				print('response is {}'.format(response))
				if not response:
					break
				else:
					data = data+response
		except socket.timeout:
			pass
		return data
#
# Utill method for retrieving ip from domain
#
def getip(domain):
	return socket.gethostbyname_ex(domain)[2][0]
if __name__=='__main__':
	controller = Controller()
	controller.connect('127.0.0.1',411)
	x = input('quit?') # debug: for hanging onto the console