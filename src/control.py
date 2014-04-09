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
import threading
import random
import string
import bz2
import time
import binascii

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
	version = '1,0091'

	#
	# Method for generating locks
	def getlock(self):
		return 'EXTENDEDPROTOCOL'+''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(18))
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
			print('Establising tcp connection...')
			self.sockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sockt.settimeout(60)
			self.sockt.connect((host,port))
		except (socket.timeout,socket.error) as err:
			self.retries = self.retries+1
			if self.retries<3:
				self.connect(host,port+1)
		if (self.retries<3):
			print('connection has been established!\nNow initiating handshake...')
			try:
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
					dot = lockval.find(' ')
					if (True if dot==-1 else False):
						lock = lockval[lockval.find('$Lock')+1:]
					else:
						lock = lockval[lockval.find('$Lock')+1:dot]
					lenlock = len(lock)
					lock = bytearray(lock)
					key = chr(lock[0] ^ lock[lenlock-1] ^ lock[lenlock-2] ^ 5)
					for i in range(1,lenlock):
						key = key+chr(lock[i] ^ lock[i-1])		
					finkey = ''
					for i in range(len(key)):
						finkey=finkey+chr(((ord(key[i]) & 0x0F) << 4) | ((ord(key[i]) & 0xF0) >> 4))
					self.sockt.sendall('$Key {}|'.format(finkey))
				self.sockt.sendall('$ValidateNick {}|'.format(self.nick))
				#
				#Getting response from hub
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
					self.sockt.sendall('$MyPass {}|'.format(self.password))
				print('hubinfo is {}'.format(self.hubinfo))
				#Gained access to the hub
				self.sockt.sendall('$Version 1,0091|')
				self.sockt.sendall('$MyINFO $ALL {0} <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31${1}$1234$|'.format(self.nick,self.email))
				response = self.sockt.recv(9000)
				for item in response.split('|'):
					if (item!=''):
						whitespacepos = item.find(' ')
						self.hubinfo[item[:whitespacepos]] = item[whitespacepos+1:]
				print('nmdc handshake complete.')
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
		runningthreads = [] #cache of all runing threads -- threads retrieve files from other connected clients

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
		
		print('data is {}'.format(data))
		#other logged in users
		users = []
		for item in data:
			if item.startswith('$NickList'):
				#getting all video files from the logged in users
				users = item.replace('$NickList ','').split('$$')
				print('logged in users are {}'.format(users))
				for i in range(len(users)):
					user = users[i]
					if (user!='' or user!=self.nick):
						users.append(user)
			elif item.startswith('$MyINFO $ALL'):
				tmp = item.split(' ')
				if (tmp[2] !=self.nick):
					users.append(tmp[2])
		#
		# Processing all the logged in users using seperate threads
		#
		# -get file list of user and index it
		#
		users = list(set(users)) # Removing duplicates	
		users.remove('')
		print('logged in users are {0}'.format(users))
		for user in users:
			user=user.strip()
			if (user!='PtokaX' or user!=self.nick): #remove this and simply check if user is not in OPlist or Botlist
				port = getport()
				revconnect = False
				if (revconnect):
					self.sockt.sendall('$RevConnectToMe {0} {1}|'.format(self.nick,user))
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
					print('hub response to revconnect is {}'.format(data))
				else:
					print('starting thread...')
					t = threading.Thread(target=self.handleClient, args=(port,user,))
					t.daemon = True
					t.start()
					runningthreads.append(t)

					print('sending $ConnectToMe {0} {1}:{2}|'.format(user,'127.0.0.1',port))
					self.sockt.sendall('$ConnectToMe {0} {1}:{2}|'.format(user,'127.0.0.1',port))
					data = '';
					try:
						while True:
							response = self.sockt.recv(1024)
							if not response:
								break
							else:
								data = data+response
					except socket.timeout:
						pass
					print('the reply was {}'.format(data))
		#
		# waiting for all threads to finish
		#time.sleep(30)		
		for thread in runningthreads:
			thread.join()
					
	#	
	#Method for accepting other client connections and caching it's files
	def handleClient(self,port,user):
		print('handleClient() called! for {}'.format(user))
		ssockt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		ssockt.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
		ssockt.bind(('127.0.0.1',port))
		ssockt.listen(1)
		print('waiting for user connection...')
		conn, addr = ssockt.accept()
		ssockt.close()
		print('connection established!')
		conn.setblocking(1)
		conn.settimeout(30)
		lockandnick = conn.recv(1024)
		print('lockandnick is {}'.format(lockandnick))
		print('sending mynick and lock, etc..')
		conn.sendall('$MyNick {0}|'.format(self.nick)+'$Lock {0} Pk=Volkano{1}|'.format(self.getlock(),self.version))
		conn.sendall('$Direction Download {0}|'.format(random.randint(1,32767)))
		#Computing the key from the lock and sending it to the other client
		lockandnick = lockandnick.split('|')
		for item in lockandnick:
			if item.startswith('$Lock'):
				spacepos = item.find(' ')
				if (True if spacepos==-1 else False):
					lock = item[item.find('$Lock')+1:]
				else:
					lock = item[item.find('$Lock')+1:spacepos]
				lenlock = len(lock)
				lock = bytearray(lock)
				key = chr(lock[0] ^ lock[lenlock-1] ^ lock[lenlock-2] ^ 5)
				for i in range(1,lenlock):
					key = key+chr(lock[i] ^ lock[i-1])		
				finkey = ''
				for i in range(len(key)):
					finkey=finkey+chr(((ord(key[i]) & 0x0F) << 4) | ((ord(key[i]) & 0xF0) >> 4))
				conn.sendall('$Key {}|'.format(finkey))
		#Retrieving key computation and more -- for simplicity, these will be ignored for the time being
		data = ''		
		try:
			while True:
				response = conn.recv(1024)
				if not response:
					break
				else:
					data = data+response
		except socket.timeout:
			pass
		print('sending adcget...')
		#Retrieving file with the other client's shared files
		conn.sendall('$ADCGET file files.xml.bz2 0 -1 ZL1|')
		conn.settimeout(60)	
		data = ''		
		try:
			while True:
				response = conn.recv(1024)
				if not response:
					break
				else:
					data = data+response
		except socket.timeout:
			pass
		barpos = data.find('|')		
		adcsend = data[:barpos]		
		bz2data = data[barpos+1:]
		f = open('files.xml.bz2','w')
		f.write(bz2data)
		f.close()
		print('\x42\x5a\x68' in binascii.hexlify(bz2data))
		print('done creating file')
		
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
# Utility method for retrieving ip from domain
#
def getip(domain):
	return socket.gethostbyname_ex(domain)[2][0]
#
# Method for getting an open port on host machine
# returns -1 of port is not found in range 5000-9999
def getport():
	soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	soc.bind(('127.0.0.1',0))
	port = soc.getsockname()[1]
	soc.close()
	return port
#
# Method for getting local ip
def getlocalip():
	return socket.gethostbyname(socket.gethostname())
if __name__=='__main__':
	controller = Controller()
	controller.connect('127.0.0.1',1200)
	x = input('quit?') # debug: for hanging onto the console
