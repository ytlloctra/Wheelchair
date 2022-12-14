# 基于人机体感交互的电控轮椅系统设计

/2560   文件夹为 下位机控制程序

​	/2560/WheelChair_Basement   下位机主控制器程序

​	/2560/WheelChair_Send    下位机从控制器程序

/processing  文件夹为 上位机程序

  	/processing/WheelChair_Kinect/WheelChair_Kinect.pde   为上位机Processing主程序

​	  /processing/WheelChair_Kinect/data    为上位机界面图片及音频文件

## 一、总体方案设计

该系统共设有三种控制模式。 **跟随模式**下， 通过抬手姿态来启动轮椅跟随功能， 通过获取 Kinect 摄像头与跟随者的深度距离实现自主跟随。 **手势模式**下， 采用握拳姿态作为启动姿态， 然后通过手部在空间上的位移控制轮椅行动。 **手动模式**下， 由其他人手动推轮椅， 轮椅就会自动将这个初速度设定为恒定的运行速度——推力大一点， 速度就快一点； 想要拐弯， 可以对一边进行轻轻地刹车来实现， 尤其在上坡环境时对其进行助力可以大大减少体力消耗。

电控轮椅：

![毕设轮椅](https://image-1312312327.cos.ap-shanghai.myqcloud.com/%E6%AF%95%E8%AE%BE%E8%BD%AE%E6%A4%85.png) 
上位机界面：

![人机交互](https://image-1312312327.cos.ap-shanghai.myqcloud.com/%E4%BA%BA%E6%9C%BA%E4%BA%A4%E4%BA%92.JPG)

## 二、下位机系统设计

### 主控制器设计

下位机采用了ATMega 2560作为主控制器。跟随模式和手势模式下，上位机处理完传感器数据后通过串口线将轮椅速度、转向等关键数据传送给主控制器，经过单片机分析后对轮椅进行控制。控制器发送PWM波给电机驱动电路来驱动电机，以此来控制轮椅转向。

### 电机驱动设计

轮椅机器人电机为250W的直流电机，单片机可以通过电机驱动电路输出PWM波对轮椅速度进行控制。其中，电机驱动电路为H桥驱动电路。

![image-20221004155352940](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004155352940.png)

H桥驱动电路主要包括了电机Motor和四个N沟道MOS晶体管。通过控制组MOS管导通可以控制电流的流向来控制电机的正反转，通过改变下位机的PWM输出可以控制发动机转速。如下图所示，采用半桥驱动芯片构成H桥电路。

![STSC[SU1T$SUQOCV6]LGY)P](https://image-1312312327.cos.ap-shanghai.myqcloud.com/clip_image002.jpg)

### Kinect电源电路设计

Kinect深度摄像头传感器通过USB线缆与PC端上位机相连，进行数据传输，电源采用12V供电，通过一块LM2596稳压模块对20V电池进行稳压，给Kinect供电。其中，Kinect摄像头硬件设计如图所示。

![image-20221004160101539](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004160101539.png)

## 三、上位机系统设计

该系统主要实现轮椅的跟随控制、手势控制、手动控制。首先要在上位机界面对三种模式进行选择，跟随模式和手势模式下都要先建立骨骼模型，然后再进行各自的姿态识别，对人体骨骼位置信息进行处理后通过串口发送到下位机对电机进行控制。手动模式下，从压力传感器获得轮椅推力数据存储在从控制器，经过无线蓝牙发送到主控制器对电机进行控制。

### 跟随模式

跟随 未识别骨架

![image-20221004160911203](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004160911203.png)

跟随 T形姿态识别

![image-20221004160850578](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004160850578.png)

跟随 识别成功

![image-20221004160949689](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004160949689.png)

### 手势模式

手势模式 骨架未识别

![image-20221004161021857](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004161021857.png)

手势模式 骨架识别

![image-20221004161055473](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004161055473.png)

手势模式 右转

![image-20221004161125202](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004161125202.png)

手势模式 左转

![image-20221004161138358](https://image-1312312327.cos.ap-shanghai.myqcloud.com/image-20221004161138358.png)

### 手动模式

手动模式下  上下坡时  串口输出压力传感器数据

![手动 坡上](https://image-1312312327.cos.ap-shanghai.myqcloud.com/%E6%89%8B%E5%8A%A8%20%E5%9D%A1%E4%B8%8A.png)

![手动 坡下](https://image-1312312327.cos.ap-shanghai.myqcloud.com/%E6%89%8B%E5%8A%A8%20%E5%9D%A1%E4%B8%8B.png)

## 四、补充说明

上位机：

- Kinect供电电源保证超过19V，否则对识别人体骨骼会有影响。

- 在运行程序之前，需要保证将Kinect摄像头和Leap 传感器的驱动打开，可以在服务里搜索打开。

- 跟随人体过程中，右手举起作为该模式的启动，放下即为停止，当摄像头和跟随对象之间的距离小于1 米时，也会自动停止。

- 手势控制轮椅时，手部握拳作为该模式的启动，松开即为停止，手部在摄像头正上方20cm处最适宜。

 

下位机：

- 轮椅电瓶定期充电，保持在19.5V以上。

- 轮椅的电源开关有两个，一个是Kinect摄像头电源开关，还有一个是轮椅电机驱动控制开关。

- Kinect摄像头为绿色闪烁时即为正常工作，Leap传感器的三目摄像头都是红丝的时候即为正常工作。

- HC-12无线蓝牙模块是用来手动推轮椅的时候，无线传输推力，因此需要检查两块蓝牙是否能正常接受发送数据，可以通过串口助手检测。

