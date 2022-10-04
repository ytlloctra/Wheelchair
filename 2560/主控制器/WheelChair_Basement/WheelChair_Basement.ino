#include <Wire.h> 

String inputString;
int PA = 8;
int PB = 2;
int Motor_A1 = 9;
int Motor_A2 = 10;
int Motor_B1 = 3;
int Motor_B2 = 4;
int Speed_Helmet = 40;

int MotorSpeeds[] = {0,0,1,0};
int NextMotor = 0;
char inChar = 'x';

void setup() 
{
  Serial.begin(9600);
  Serial1.begin(9600);
  pinMode(PA, OUTPUT);
  pinMode(PB, OUTPUT);
  pinMode(Motor_A1, OUTPUT);
  pinMode(Motor_A2, OUTPUT);
  pinMode(Motor_B1, OUTPUT);
  pinMode(Motor_B2, OUTPUT);
  
}
 
void loop() 
{
  
//  if(Serial.available())
//  {
//    int MotorSpeed = Serial.read();
//    MotorSpeeds[NextMotor] = MotorSpeed;
//    NextMotor++;
//    if(NextMotor > 3)     // SpeedL    SpeedR    Direction    Gyroscope    from Processing Serial
//      NextMotor = 0;
//    //MotorSpeeds[3] = 0;

//    if(MotorSpeeds[3] == 0)   //  Gyroscope == 0
//    {
//      Forward(MotorSpeeds[0], MotorSpeeds[1], MotorSpeeds[2]);
//    }
//    else if (MotorSpeeds[3] == 1)   // Gyroscope == 1
//    {
      if (Serial1.available()) 
      {
        inChar = Serial1.read();
        Serial.println(inChar);
      }
      if(inChar == 'w')
        Forward(Speed_Helmet, Speed_Helmet , 1);
      if(inChar == 's')
        Forward(Speed_Helmet, Speed_Helmet, 2);
      if(inChar == 'a')
        Forward(Speed_Helmet, Speed_Helmet, 3);
      if(inChar == 'd')
        Forward(Speed_Helmet, Speed_Helmet, 4);
      if(inChar == 'x')
        Forward(0,0,1);
//     }
//    Serial.println(MotorSpeeds[0] + " " + MotorSpeeds[1]);
//  } 
}


void Forward(int SpeedL, int SpeedR, int Direction)
{
  if(Direction == 1)
  {
    digitalWrite(Motor_A1, HIGH);
    digitalWrite(Motor_A2, LOW);  
    digitalWrite(Motor_B1, HIGH);
    digitalWrite(Motor_B2, LOW);
    analogWrite(PA, SpeedR);
    analogWrite(PB, SpeedL);
  }
  else if(Direction == 2)
  {
    digitalWrite(Motor_A1, LOW);
    digitalWrite(Motor_A2, HIGH);  
    digitalWrite(Motor_B1, LOW);
    digitalWrite(Motor_B2, HIGH);
    analogWrite(PA, SpeedR);
    analogWrite(PB, SpeedL);
  }
  else if(Direction == 3)
  {
    digitalWrite(Motor_A1, LOW);
    digitalWrite(Motor_A2, HIGH);  
    digitalWrite(Motor_B1, HIGH);
    digitalWrite(Motor_B2, LOW);
    analogWrite(PA, SpeedR);
    analogWrite(PB, SpeedL);
  }
  else if(Direction == 4)
  {
    digitalWrite(Motor_A1, HIGH);
    digitalWrite(Motor_A2, LOW);  
    digitalWrite(Motor_B1, LOW);
    digitalWrite(Motor_B2, HIGH);
    analogWrite(PA, SpeedR);
    analogWrite(PB, SpeedL);
  }
}
