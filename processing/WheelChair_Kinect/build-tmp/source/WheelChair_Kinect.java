import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import SimpleOpenNI.*; 
import de.voidplus.leapmotion.*; 
import controlP5.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WheelChair_Kinect extends PApplet {

/*
Target: WheelChair with Kinect, Leap Motion, Arduino, MPU6050
Author: Leo Wang
Date:   2018.10.16
*/ 
/*---------------------------------------------------------------------------------------------------------*/
//Load Library//
        //Serial
    //Kinect
   //Leap Motion
   //ControlP5
         //Music





/*---------------------------------------------------------------------------------------------------------*/
//Kinect Definition
SimpleOpenNI kinect;   
//Leap Motion Definition
LeapMotion leap; 
//Port Definition
Serial port; 
//ControlIP5 Definition
ControlP5 cp5;
//Minim Definition
Minim minim;  
/*---------------------------------------------------------------------------------------------------------*/
PImage kinectDepth, kinectRGB;                     // image storage from kinect
PImage Menu, Following_Mode, Helmet_Mode, Manual_Mode, LeapMotion_Mode;  //background image
AudioPlayer Media_Menu, Media_Following, Media_Helmet, Media_Manual, Media_Leap,
	Media_Forward, Media_Backward, Media_Left, Media_Right, Media_Low, Media_Mid, Media_High;
PVector headPosition = new PVector();              // postion of head to draw circle
PVector hand_position, hand_stabilized;   //Position of Hand detected by Leap Motion

PFont MyFont;

float distanceScalar;                              // turn headPosition into scalar form
float headSize = 100;                              // Head Size Diameter
float hand_grab;                                   //Strength of Grab

int userID;
int hand_num;
int Mode_Flag = 0; //Mode Flag
int LeapMotion_Start_Falg = 0;
int Following_Start_Flag = 0;
int SpeedL = 0;
int SpeedR = 0;
int Direction = 1;
int SpeedButton = 20;
int SpeedLow = 30;
int SpeedMid = 55;
int SpeedHigh = 80;
int Gyroscope = 0;

byte out[] = new byte[4];                          // Define Port Data Array
/*---------------------------------------------------------------------------------------------------------*/
public void setup()
{
  // Setup the Kinect //
  kinect = new SimpleOpenNI(this);                 // start a new kinect object
  port = new Serial(this,"com5",9600);          // start a new Serial Port
  minim = new Minim(this);
  // Setup the Leap Motion //
	leap = new LeapMotion(this);

  kinect.enableDepth();                            // enable depth sensor
  //kinect.enableRGB();                              // enable RGB
  kinect.enableUser();                             // enable skeleton generation for all joints
  kinect.setMirror(true);
  
  // Load File //
  Menu           = loadImage("WheelChair_Menu.jpg");     //Picture  WheelChair_Menu2
  Following_Mode = loadImage("Following_Mode.jpg");
  Helmet_Mode    = loadImage("Helmet_Mode.jpg");
  Manual_Mode    = loadImage("Manual_Mode.jpg");
  LeapMotion_Mode= loadImage("LeapMotion_Mode.jpg");
  
  Media_Menu     = minim.loadFile("Media_Menu.mp3");      //Music
  Media_Following= minim.loadFile("Media_Following.mp3");
  Media_Helmet   = minim.loadFile("Media_Helmet.mp3");
  Media_Manual   = minim.loadFile("Media_Manual.mp3");
  Media_Leap     = minim.loadFile("Media_Leap.mp3");
  Media_Forward  = minim.loadFile("Forward.mp3");
  Media_Backward = minim.loadFile("Backward.mp3");
  Media_Left     = minim.loadFile("Left.mp3");
  Media_Right    = minim.loadFile("Right.mp3");
  Media_Low      = minim.loadFile("Low.mp3");
  Media_Mid      = minim.loadFile("Middle.mp3");
  Media_High     = minim.loadFile("High.mp3");

  MyFont = createFont("ChaparralPro-Italic-48", 15);
  textFont(MyFont);
  
  // Setup the window //
  size(1280,680);                                 
  smooth();
  strokeWeight(5);
  fill(255, 0, 0);
  textSize(20);

  // Setup ControlIP5 //
  cp5 = new ControlP5(this);
  
}
/*---------------------------------------------------------------------------------------------------------*/
public void draw()
{
  background(0);
  Display_Init();
  kinect.update(); // update the camera
  kinectDepth = kinect.depthImage();
  kinectRGB   = kinect.rgbImage();
  
  /*rect(30,300,320,100);   //For Button Test//
  rect(480,300,320,100);
  rect(930,300,320,100);
  rect(885,440,240,70);
  rect(480,420,320,100); //Leap Motion Mode
  rect(595,95,100,100);  //UP
  rect(595,355,100,100); //DOWN
  rect(755,220,100,100); //RIGHT
  rect(425,220,100,100); 
  rect(85,75,640,480);   //Kinect View
  rect(145,120,155,65);  //Speed Button Low
 	rect(145,250,155,65);  //Speed Middle
 	rect(145,380,155,65);  //Speed High
 	strokeWeight(5);noFill();ellipse(100,160,30,30); //Speed Low Circle 
  strokeWeight(5);noFill();ellipse(100,285,30,30);
  strokeWeight(5);noFill();ellipse(100,415,30,30);
  */

 /*-----------------------------------------------------------------------------*/
  //Mode Choose//
  if(Mode_Flag == 0)
  {
  	Media_Menu.play();
	  if((mouseX >= 30) && (mouseX <= 350) && (mouseY >= 300) && (mouseY <= 400))
	  {
	  	if(mousePressed)
	  		Mode_Flag = 1;
	  }
	  else if((mouseX >= 480) && (mouseX <= 800) && (mouseY >= 300) && (mouseY <= 400))
	  {
	  	if(mousePressed)
	  		Mode_Flag = 2;
	  }
	  else if((mouseX >= 930) && (mouseX <= 1250) && (mouseY >= 300) && (mouseY <= 400))
	  {
	  	if(mousePressed)
	  		Mode_Flag = 3;
	  }
	  else if((mouseX >= 480) && (mouseX <= 800) && (mouseY >= 420) && (mouseY <= 520))
	  {
	  	if(mousePressed)
	  		Mode_Flag = 4;
	  }
	  //println(Mode_Flag);  //For Test
	  Media_Following.rewind();   //Minim Rewind
	  Media_Helmet.rewind();
	  Media_Manual.rewind();
	  Media_Leap.rewind();
	  SpeedR = 0;
    SpeedL = 0;
    int Direction = 1;
    Gyroscope = 0;
	}
 /*-----------------------------------------------------------------------------*/
  //Following Mode//
  if(Mode_Flag == 1)
  {
  	Media_Menu.pause();
  	Media_Following.play();
  	image(Following_Mode,0,0,1280,680);
  	strokeWeight(5);
 		fill(255,0,0);
  	ellipse(1000,400,60,60);
  	Following_Start_Flag = 0;
  	int SpeedFollowing = 100;
  	int offset = 0;             //\u504f\u79fb\u91cf
  	SpeedL = 0;
  	SpeedR = 0;
  	Gyroscope = 0;
  	// BACK //
  	if((mouseX >= 885) && (mouseX <= 1120) && (mouseY >= 440) && (mouseY <= 510))
	  {
	  	if(mousePressed)
	  	{
	  		Mode_Flag = 0;
	  		Media_Following.pause();
	  	  Media_Menu.rewind();
	  	}
	  }

	  image(kinectDepth, 75, 75); 
	  IntVector userList = new IntVector();
    kinect.getUsers(userList);
    if (userList.size()>0)
    {
      userID = userList.get(0);
      if (kinect.isTrackingSkeleton(userID))
      {
      	stroke(255, 0, 0);
        fill(255, 0, 0);
        pushMatrix();         //Coordinate transform
        translate(85, 75);
        drawSkeleton(userID);                     //Draw the First Skeleton
        popMatrix();
 
        PVector torsoPosition = new PVector();
        PVector leftHand      = new PVector();
        PVector rightHand     = new PVector(); 
        PVector leftShoulder  = new PVector();
        PVector rightShoulder = new PVector();
        PVector leftElbow     = new PVector();
        PVector rightElbow    = new PVector();
        PVector leftHip       = new PVector();
        PVector rightHip      = new PVector();
        
        kinect.getJointPositionSkeleton(userID, SimpleOpenNI.SKEL_TORSO, torsoPosition); //Get Torso's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_LEFT_HAND, leftHand);    //Get LeftHand's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_RIGHT_HAND, rightHand);  //Get RightHand's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_LEFT_SHOULDER, leftShoulder);  //Get LeftShoulder's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_RIGHT_SHOULDER, rightShoulder);  //Get RightShoulder's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_LEFT_ELBOW, leftElbow);  //Get LeftElbow's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);  //Get RightElbow's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_LEFT_HIP, rightHip);  //Get LeftHip's PVector
        kinect.getJointPositionSkeleton(userID,SimpleOpenNI.SKEL_RIGHT_HIP, rightHip);  //Get RightHip's PVector
        
        PVector rightHand2D_XY     = new PVector(rightHand.x, rightHand.y);
        PVector rightElbow2D_XY    = new PVector(rightElbow.x, rightElbow.y);
        PVector rightShoulder2D_XY = new PVector(rightShoulder.x, rightShoulder.y);
        PVector rightHip2D_XY      = new PVector(rightHip.x, rightHip.y);
        
        PVector torsoOrientation_XY = PVector.sub(rightShoulder2D_XY, rightHip2D_XY);
        PVector upperArmOrientation_XY = PVector.sub(rightElbow2D_XY, rightShoulder2D_XY); 
        
        float shoulderAngle_XY = angleOf(rightElbow2D_XY, rightShoulder2D_XY, torsoOrientation_XY);
        float elbowAngle_XY    = angleOf(rightHand2D_XY, rightElbow2D_XY, upperArmOrientation_XY);
        float rightElbow_z    = rightElbow.z;
        
        PVector differenceVector  = PVector.sub(leftHand,rightHand);
        PVector differenceVector1 = PVector.sub(rightElbow, rightShoulder);    //Right Large Arm
        
        float magnitude = differenceVector.mag();    
        float magnitude1 = differenceVector1.mag();  
        
        differenceVector.normalize();
        differenceVector1.normalize();

        if(torsoPosition.x > 700)      //Horizontal direction, position limit
        	torsoPosition.x = 700;
        if(torsoPosition.x < -700)
        	torsoPosition.x = -700;
        if(torsoPosition.z > 3000)      //Distance limit
        	torsoPosition.z = 3000;
        if(torsoPosition.z < 1000)
        	torsoPosition.z = 1000;

        offset = PApplet.parseInt(map(torsoPosition.x, -700, 700, -15,15));    //Offset 
        SpeedFollowing = PApplet.parseInt(map(torsoPosition.z, 1800, 3000, 10, 70));   //Speed with Distance

        /*------------TEXT------------*/ 
        fill(255); 
        scale(1); 
        text("TorsoPositionX:",15,600);
        text(torsoPosition.x,180,600);
        text("TorsoPositionZ",15,620);
        text(torsoPosition.z,180,620);
        /*----------------------------*/
        if(rightHand.y > rightShoulder.y)
        {
        	text("START",15,570);
        	if(torsoPosition.z >= 1800)
        		Following_Start_Flag = 1;
      		else
      			Following_Start_Flag = 0;
        }
        if(Following_Start_Flag == 1)
        {
        	strokeWeight(5);fill(0,255,0);ellipse(1000,400,60,60);
        	SpeedL = SpeedFollowing - offset;   //Speed Left 
          SpeedR = SpeedFollowing + offset;   //Speed Right 
        }
        if(Following_Start_Flag == 0)
        {
        	strokeWeight(5);fill(255,0,0);ellipse(1000,400,60,60);
        	SpeedR = 0;
        	SpeedL = 0;
        }

      }
    }
    println(SpeedL + " " + SpeedR);
    Send_Data();
  }


 /*-----------------------------------------------------------------------------*/  
  //Helmet Mode//
  if(Mode_Flag == 2)
  {
  	Media_Menu.pause();
  	Media_Helmet.play();
  	image(Helmet_Mode,0,0,1280,680);
  	println(Gyroscope);   //For TEST
  	Gyroscope = 1;


  	if((mouseX >= 885) && (mouseX <= 1120) && (mouseY >= 440) && (mouseY <= 510))   //BACK
	  {
	  	if(mousePressed)
	  	{
	  		Mode_Flag = 0;
	  		Media_Helmet.pause();
	  	  Media_Menu.rewind();
	  	  Gyroscope = 0;
	  	}
	  }
	  Send_Data();
  }


 /*-----------------------------------------------------------------------------*/
  //Manual Mode//
  if(Mode_Flag == 3)
  {
  	Media_Menu.pause();
  	Media_Manual.play();
  	image(Manual_Mode,0,0,1280,680);
  	Gyroscope = 0;

  	if((mouseX >= 885) && (mouseX <= 1120) && (mouseY >= 440) && (mouseY <= 510))     //BACK
	  {
	  	if(mousePressed)
	  	{
	  		Mode_Flag = 0;
	  		Media_Manual.pause();
	  		Media_Menu.rewind();
	  	}
	  }
	  /*-------------------------------------------------------------------------------*/
	  else if((mouseX >= 145) && (mouseX <= 300) && (mouseY >= 120) && (mouseY <= 185))   //SPEED LOW
	  {
	  	if(mousePressed)
	  	{
	  		SpeedButton = SpeedLow;
	  		Media_Low.play();
	  		Media_Mid.pause();
	  		Media_High.pause();
	  		Media_Mid.rewind();
	  		Media_High.rewind();
	  	}
	  }
	  else if((mouseX >= 145) && (mouseX <= 300) && (mouseY >= 250) && (mouseY <= 315))   //SPEED MID
	  {
	  	if(mousePressed)
	  	{
	  		SpeedButton = SpeedMid;
	  		Media_Mid.play();
	  		Media_Low.pause();
	  		Media_High.pause();
	  		Media_Low.rewind();
	  		Media_High.rewind();
	  	}
	  }
	  else if((mouseX >= 145) && (mouseX <= 300) && (mouseY >= 380) && (mouseY <= 445))   //SPEED HIGH
	  {
	  	if(mousePressed)
	  	{
	  		SpeedButton = SpeedHigh;
	  		Media_High.play();
	  		Media_Low.pause();
	  		Media_Mid.pause();
	  		Media_Low.rewind();
	  		Media_Mid.rewind();
	  	}
	  }
	  /*-------------------------------------------------------------------------------*/
	  else if((mouseX >= 595) && (mouseX <= 695) && (mouseY >= 95) && (mouseY <= 195))    //UP Button
	  {
	  	if(mousePressed)
	  	{
	  		SpeedL = SpeedButton;
	  		SpeedR = SpeedButton;
	  		Direction = 1;
	  		println("w" + " " + SpeedButton); //For Test
	  		Media_Forward.play();
	  		Media_Backward.pause();Media_Left.pause();Media_Right.pause();
	  		Media_Backward.rewind();Media_Left.rewind();Media_Right.rewind();
	  	}
	  	else
	  	{
	  		SpeedL = 0;
	    	SpeedR = 0;
	    }
	  }
	  else if((mouseX >= 595) && (mouseX <= 695) && (mouseY >= 355) && (mouseY <= 455))    //DOWN Button
	  {
	  	if(mousePressed)
	  	{	
	  		SpeedL = SpeedButton;
	  		SpeedR = SpeedButton;
	  		Direction = 2;
	  		println("s" + " " + SpeedButton); //For Test
	  		Media_Backward.play();
	  		Media_Forward.pause();Media_Left.pause();Media_Right.pause();
	  		Media_Forward.rewind();Media_Left.rewind();Media_Right.rewind();
	  	}
	  	else
	  	{
	  		SpeedL = 0;
	    	SpeedR = 0;
	    }
	  }
	  else if((mouseX >= 755) && (mouseX <= 855) && (mouseY >= 220) && (mouseY <= 320))    //RIGHT Button
	  {
	  	if(mousePressed)
	  	{
	  		
	  		SpeedL = SpeedButton;
	  		SpeedR = SpeedButton;
	  		Direction = 3;
	  		println("d" + " " + SpeedButton); //For Test
	  		Media_Right.play();
	  		Media_Backward.pause();Media_Left.pause();Media_Forward.pause();
	  		Media_Backward.rewind();Media_Left.rewind();Media_Forward.rewind();
	  	}
	  	else
	  	{
	  		SpeedL = 0;
	    	SpeedR = 0;
	    }
	  }
	  else if((mouseX >= 425) && (mouseX <= 525) && (mouseY >= 220) && (mouseY <= 320))    //LEFT Button
	  {
	  	if(mousePressed)
	  	{
	  		SpeedL = SpeedButton;
	  		SpeedR = SpeedButton;
	  		Direction = 4;
	  		println("a" + " " + SpeedButton); //For Test
	  		Media_Left.play();
	  		Media_Backward.pause();Media_Forward.pause();Media_Right.pause();
	  		Media_Backward.rewind();Media_Forward.rewind();Media_Right.rewind();
	  	}
	  	else
	  	{
	  		SpeedL = 0;
	    	SpeedR = 0;
	    }
	  }
	  else
	  {
	  	SpeedL = 0;
	    SpeedR = 0;
	  }
	  Send_Data();
  }
 /*-----------------------------------------------------------------------------*/
 // Leap Motion Control Mode //
 if(Mode_Flag == 4)    
 {
 	Media_Menu.pause();
 	Media_Leap.play();
 	image(LeapMotion_Mode,0,0,1280,680);
 	strokeWeight(5);
 	fill(255,0,0);
  ellipse(1000,400,60,60);
  int Leap_Direction = 0;
  int Leap_Speed = 0;
  SpeedR = 0;
  SpeedL = 0;
  Gyroscope = 0;

 	for (Hand hand : leap.getHands ())
  {
  	//Hand
    hand_num = leap.countHands();
  	hand_position     = hand.getPosition();
    hand_stabilized   = hand.getStabilizedPosition();
    hand_grab         = hand.getGrabStrength();  

    if(hand_num == 1)
    {
    	textSize(0);
      strokeWeight(10);
      stroke(255);
      hand.draw();    //Draw The Hand

      if(hand_position.x < 150)    //Set the Range
      	hand_position.x = 150;
      if(hand_position.x > 1200)
      	hand_position.x = 1200;
      if(hand_position.z < 30)
      	hand_position.z = 30;
      if(hand_position.z > 70)
      	hand_position.z = 70;
      Leap_Direction = PApplet.parseInt(map(hand_position.x, 150,1200,-30, 30));  //Offset
      Leap_Speed = PApplet.parseInt(map(hand_position.z, 30, 80, -75, 75));    //Forward and Backward
      if((Leap_Direction >= -8) && (Leap_Direction <= 8))
      	Leap_Direction = 0;
      if((Leap_Speed >= -20) && (Leap_Speed <= 20))
      	Leap_Speed = 0;

			if(hand_grab >= 0.5f)           //Grab to Start Control
  			LeapMotion_Start_Falg = 1;
  		else
  			LeapMotion_Start_Falg = 0;
  		if(LeapMotion_Start_Falg == 0)
  		{
  			strokeWeight(5);fill(255,0,0);ellipse(1000,400,60,60);
  			SpeedR = 0;
    		SpeedL = 0;
  		}
  		else if (LeapMotion_Start_Falg == 1)   
  		{
  			strokeWeight(5);fill(0,255,0);ellipse(1000,400,60,60); 
  			//println(Leap_Direction);   //Print 
  			SpeedL = Leap_Speed + Leap_Direction;
  			SpeedR = Leap_Speed - Leap_Direction;
  		}	
    }
    else
    {
    	SpeedR = 0;
    	SpeedL = 0;
    }
    if((SpeedR < 0) || (SpeedL < 0))
    {
    	SpeedL = -Leap_Speed;
    	SpeedR = -Leap_Speed;
    	SpeedL = PApplet.parseInt(map(-Leap_Speed, 0, 60, 0, 40));
    	SpeedR = PApplet.parseInt(map(-Leap_Speed, 0, 60, 0, 40));
    	Direction = 2;
    }
    else if((SpeedR >= 0) && (SpeedL >= 0))
    	Direction = 1;
    println(SpeedL + " " + SpeedR + " " + Direction);
    Send_Data();
  }

  //BACK//
  if((mouseX >= 885) && (mouseX <= 1120) && (mouseY >= 440) && (mouseY <= 510))
	  {
	  	if(mousePressed)
	  	{
	  		Mode_Flag = 0;
	  		Media_Leap.pause();
	  	  Media_Menu.rewind();
	  	  LeapMotion_Start_Falg = 0;
	  	}
	  }
 }
 //println(LeapMotion_Start_Falg);  //For Test//
 /*-----------------------------------------------------------------------------*/
}




/*---------------------------------------------------------------------------------------------------------*/
public void drawSkeleton(int userID)
{
  // get 3D position of head
  kinect.getJointPositionSkeleton(userID, SimpleOpenNI.SKEL_HEAD, headPosition);
  // convert real world point to projective space
  kinect.convertRealWorldToProjective(headPosition, headPosition);
  // create a distance scalar related to the depth in z dimension
  distanceScalar = (525/headPosition.z);
  // draw the circle at the position of the head with the head size scaled by the distance scalar
  ellipse(headPosition.x, headPosition.y, distanceScalar*headSize, distanceScalar*headSize);

  strokeWeight(6); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);  
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE); 
  kinect.drawLimb(userID, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT); 
  //Draw the body joint//
  noStroke();
  fill(0,255,0);//GREEN
  drawJoint(userID, SimpleOpenNI.SKEL_HEAD); 
  drawJoint(userID, SimpleOpenNI.SKEL_NECK); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_SHOULDER); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_ELBOW); 
  drawJoint(userID, SimpleOpenNI.SKEL_NECK); 
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_SHOULDER);  
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_ELBOW);  
  drawJoint(userID, SimpleOpenNI.SKEL_TORSO); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_HIP); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_KNEE); 
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_HIP); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_FOOT); 
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_KNEE); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_HIP); 
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_FOOT); 
  drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_HAND); 
  drawJoint(userID, SimpleOpenNI.SKEL_LEFT_HAND); 
  //fill(0, 0, 255);//BLUE
  //drawJoint(userID, SimpleOpenNI.SKEL_LEFT_FINGERTIP);
  //drawJoint(userID, SimpleOpenNI.SKEL_RIGHT_FINGERTIP);
}
/*---------------------------------------------------------------------------------------------------------*/
public void drawJoint(int userID, int jointID)
{
  PVector joint = new PVector();
  float confidence = kinect.getJointPositionSkeleton(userID, jointID, joint);
  if(confidence < 0.5f)
  {
    return;
  }
  PVector convertedJoint = new PVector();
  kinect.convertRealWorldToProjective(joint, convertedJoint);
  ellipse(convertedJoint.x, convertedJoint.y, 10, 10);
}
/*---------------------------------------------------------------------------------------------------------*/
public void onNewUser(SimpleOpenNI kinect, int userID)
{
  println("start pose detection");   
  kinect.startTrackingSkeleton(userID);           // start tracking of user id
}
/*---------------------------------------------------------------------------------------------------------*/
public float angleOf(PVector one, PVector two, PVector axis)
{
  PVector limb = PVector.sub(two, one);
  return degrees(PVector.angleBetween(limb, axis));
}
/*---------------------------------------------------------------------------------------------------------*/

public void Display_Init()
{
	image(Menu,0,0,1280,680);
}
/*---------------------------------------------------------------------------------------------------------*/
public void keyPressed()
{
  if(keyCode == 32)   //Space Key
  {
    Mode_Flag = 0;
    SpeedR = 0;
    SpeedL = 0;
    Gyroscope = 0;
    Send_Data();   //Serial Send
  }
}
/*---------------------------------------------------------------------------------------------------------*/
public void Send_Data()
{
	out[0] = PApplet.parseByte(SpeedL);
  out[1] = PApplet.parseByte(SpeedR);
  out[2] = PApplet.parseByte(Direction);
  out[3] = PApplet.parseByte(Gyroscope);
  port.write(out);
  delay(8);
}
/*---------------------------------------------------------------------------------------------------------*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WheelChair_Kinect" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
