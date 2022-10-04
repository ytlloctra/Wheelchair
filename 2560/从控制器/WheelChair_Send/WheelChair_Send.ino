#include <Arduino.h>

#define DEBUGSerial Serial
int Sensor_Left = A0;   
int Sensor_Right = A1;   

long offset = 0;

int flag = 0;


#define PRESS_MIN 20
#define PRESS_MAX 6000


#define VOLTAGE_MIN 100
#define VOLTAGE_MAX 3300

void setup()
{
  DEBUGSerial.begin(9600); // setup serial
//  Serial1.println("setup end!");
}

void loop()
{
  long Fdata_Left = getPressValue(Sensor_Left);
  long Fdata_Right = getPressValue(Sensor_Right);
//  DEBUGSerial.print("F = ");
//    DEBUGSerial.println(Fdata);
//  DEBUGSerial.println(" g,");
//  delay(300);
//   DEBUGSerial.println(flag);
//   delay(500);
   offset = Fdata_Left - Fdata_Right;
   DEBUGSerial.println(Fdata_Left);
   delay(500);
   DEBUGSerial.println(Fdata_Right);
   delay(500);
   if(offset > 6000)
   {
      offset = 6000;
   }
   else if(offset < -6000)
   {
      offset = -6000;
   }



//   if((offset >=  4000)&&(offset <=  6000))
//   {
//       DEBUGSerial.println('a');
//   }
//   else if((offset >=  -6000)&&(offset <= -4000))
//   {
//       DEBUGSerial.println('d');
//   }
//   else if((offset >=  -100)&&(offset <=  100))
//   {
//       DEBUGSerial.println('x');
//   }
//   else
//   {
//        DEBUGSerial.println('w');
//   }
}




//  A0 读取 Value(AD值)  映射成 VOLTAGE_AO（电压）  映射成  PRESS_AO（手指压力）
long getPressValue(int pin)
{
  long PRESS_AO = 0;
  int VOLTAGE_AO = 0;
  int value = analogRead(pin);

//  DEBUGSerial.print("AD = ");
//  DEBUGSerial.print(value);
//  DEBUGSerial.print(" ,");

  VOLTAGE_AO = map(value, 0, 1023, 0, 5000);//  0-1023 映射 0-5000

//  DEBUGSerial.print("V = ");
//  DEBUGSerial.print(VOLTAGE_AO);
//  DEBUGSerial.print(" mv,");

  if(VOLTAGE_AO < VOLTAGE_MIN) // 100  0.1v
  {
    PRESS_AO = 0;
  }
  else if(VOLTAGE_AO > VOLTAGE_MAX)  // 3300 3.3v
  {
    PRESS_AO = PRESS_MAX;   // PRESS_MAX 6000
  }
  else
  {
    PRESS_AO = map(VOLTAGE_AO, VOLTAGE_MIN, VOLTAGE_MAX, PRESS_MIN, PRESS_MAX);   // 100 3000          20   6000
  }
  
  return PRESS_AO;
}
