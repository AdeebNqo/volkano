package models;

import interfaces.IConfiguration;

public class DefaultConfiguration implements IConfiguration{

        private static final String configurationFile = "";

        private String username = "newUser";
        private String password = "somepassword";
        private int port = 9090;
        private String IPAddress = "localhost";

        public DefaultConfiguration(){

        }

        public void setUsername(String username){
                this.username = username;
        }
        public String getUsername(){
                return username;
        }

        public void setPassword(String password){
                this.password = password;
        }
        public String getPassword(){
                return password;
        }

        public String getIPAddress(){
                return IPAddress;
        }
        public int getPort(){
                return port;
        }
}
