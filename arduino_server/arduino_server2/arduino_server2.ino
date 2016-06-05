#include <SoftwareSerial.h>
 
#define DEBUG true
#define FAN1 1
#define FAN2 2
#define FAN3 3
#define FANPIN_1 5
#define FANPIN_2 7
#define FANPIN_3 9
#define BUTTONPIN 2
#define FANSPEED 1023

SoftwareSerial esp8266(2,3); // make RX Arduino line is pin 2, make TX Arduino line is pin 3.
                             // This means that you need to connect the TX line from the esp to the Arduino's pin 2
                             // and the RX line from the esp to the Arduino's pin 3
void setup()
{
  Serial.begin(9600);
  esp8266.begin(115200); // your esp's baud rate might be different
 
  pinMode(BUTTONPIN, INPUT);
  pinMode(FANPIN_1, OUTPUT);
  pinMode(FANPIN_2, OUTPUT);
  pinMode(FANPIN_3, OUTPUT);
  analogWrite(FANPIN_1, LOW); // By changing values from 0 to 255 you can control motor speed
  analogWrite(FANPIN_2, LOW); // By changing values from 0 to 255 you can control motor speed
  analogWrite(FANPIN_3, LOW); // By changing values from 0 to 255 you can control motor speed

  sendData("AT+RST\r\n",2000,DEBUG); // reset module
  sendData("AT+CWMODE=2\r\n",1000,DEBUG); // configure as access point
  sendData("AT+CIFSR\r\n",1000,DEBUG); // get ip address
  sendData("AT+CIPMUX=1\r\n",1000,DEBUG); // configure for multiple connections
  sendData("AT+CIPSERVER=1,1234\r\n",1000,DEBUG); // turn on server on port 1234
}
 
void loop()
{
  if(esp8266.available()) // check if the esp is sending a message 
  {
    
    if(esp8266.find("+IPD,"))
    {
      int fanId = esp8266.read() - 48;// subtract 48 because the read() function returns 
                                      // the ASCII decimal value and 0 (the first decimal number) starts at 48
      switch (fanId) {
        case FAN1:
          //start fan1
          analogWrite(FANPIN_1, FANSPEED);
          analogWrite(FANPIN_2, LOW);
          analogWrite(FANPIN_3, LOW);
          break;
        case FAN2:
          //start fan2
          analogWrite(FANPIN_1, LOW);
          analogWrite(FANPIN_2, FANSPEED);
          analogWrite(FANPIN_3, LOW);
          break;
        case FAN3:
          //start fan3
          analogWrite(FANPIN_1, LOW);
          analogWrite(FANPIN_2, LOW);
          analogWrite(FANPIN_3, FANSPEED);
          break;
      }


     // delay(1000);
 
     // int connectionId = esp8266.read()-48; // subtract 48 because the read() function returns 
                                           // the ASCII decimal value and 0 (the first decimal number) starts at 48
     
     // String webpage = "<h1>Hello</h1>&lth2>World!</h2><button>LED1</button>";

     // Serial.println(webpage);
 
     // String cipSend = "AT+CIPSEND=";
     // cipSend += connectionId;
     // cipSend += ",";
     // cipSend +=webpage.length();
     // cipSend +="\r\n";
     
     // sendData(cipSend,1000,DEBUG);
     // sendData(webpage,1000,DEBUG);
     
     // webpage="<button>LED2</button>";
     
     // cipSend = "AT+CIPSEND=";
     // cipSend += connectionId;
     // cipSend += ",";
     // cipSend +=webpage.length();
     // cipSend +="\r\n";
     
     // sendData(cipSend,1000,DEBUG);
     // sendData(webpage,1000,DEBUG);
 
     // String closeCommand = "AT+CIPCLOSE="; 
     // closeCommand+=connectionId; // append connection id
     // closeCommand+="\r\n";
     
     // sendData(closeCommand,3000,DEBUG);
    }
  }
}
 
 
String sendData(String command, const int timeout, boolean debug)
{
    String response = "";
    
    esp8266.print(command); // send the read character to the esp8266
    
    long int time = millis();
    
    while( (time+timeout) > millis())
    {
      while(esp8266.available())
      {
        
        // The esp has data so display its output to the serial window 
        char c = esp8266.read(); // read the next character.
        response+=c;
      }  
    }
    
    if(debug)
    {
      Serial.print(response);
    }
    
    return response;
}
