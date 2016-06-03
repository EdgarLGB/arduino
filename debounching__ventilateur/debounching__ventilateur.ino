const int fanPin1 = 5;
const int fanPin2 = 7;
const int fanPin3 = 9;
const int buttonPin = 2;
const int fanSpeed = 1023;
int lastButtonState = LOW;
long lastDebounceTime = 0;
const long pressingPeriod = 2000;
const long debouncingTime = 50;
int pressingTimes = 0;

void setup()
{
  pinMode(buttonPin, INPUT);
  pinMode(fanPin1, OUTPUT);
  pinMode(fanPin2, OUTPUT);
  pinMode(fanPin3, OUTPUT);
  analogWrite(fanPin1, LOW); // By changing values from 0 to 255 you can control motor speed
  analogWrite(fanPin2, LOW); // By changing values from 0 to 255 you can control motor speed
  analogWrite(fanPin3, LOW); // By changing values from 0 to 255 you can control motor speed
  Serial.begin(9600);
}

void loop()
{
  int reading = digitalRead(buttonPin);
  if (reading != lastButtonState && reading == HIGH)
  {
     //reset counter
    lastButtonState = reading;
    lastDebounceTime = millis();
    pressingTimes = 1;
    while(millis() - lastDebounceTime <= pressingPeriod)
    {
      delay(debouncingTime);
      //count the times of pressing
      reading = digitalRead(buttonPin);
      if (reading != lastButtonState && reading == HIGH)
      {
        pressingTimes++;
      }
      lastButtonState = reading;

    }
  }
  Serial.println(pressingTimes);
  switch(pressingTimes)
  {
    case 1:
      analogWrite(fanPin1, fanSpeed);
      analogWrite(fanPin2, LOW);
      analogWrite(fanPin3, LOW);
      break;
    case 2:
      analogWrite(fanPin1, LOW);
      analogWrite(fanPin2, fanSpeed);
      analogWrite(fanPin3, LOW);
      break;
    case 3:
      analogWrite(fanPin1, LOW);
      analogWrite(fanPin2, LOW);
      analogWrite(fanPin3, fanSpeed);
      break;
  }

}
