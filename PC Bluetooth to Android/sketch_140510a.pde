import processing.serial.*;

// The serial port:
Serial myPort;
Serial outPort;
int loc;
int r;
int g;
int b;
int i;
int time;
int temp;
PFont f;

void setup() {
  size(600,30);
  background(200, 200, 200);
  noStroke();
  f = createFont("Arial",16,true);
  
  // List all the available serial ports:
  println(Serial.list());
  // Open the port you are using at the rate you want:
  myPort = new Serial(this, Serial.list()[2], 9600); //COM5
  outPort = new Serial(this, Serial.list()[1], 9600);//COM3
  i = 0;
  time = 0;
}

void draw() {
  while (myPort.available() > 0) {
    temp = myPort.read();
    outPort.write(temp);
    switch(i){
     case (0):
       loc = temp;
       break;
     case (1):
       r = temp / 3;
       break;
     case (2):
       g = temp / 3;
       break;
     case (3):
       b = temp / 3;
       break;  
    }
    i++;
    if (i == 4){
      i = 0;
    }
     println("hi");
    
    /*if (i == 4){
      fill(r,g,b);
      ellipse(loc*10+5,25,10,10);
      i = 0;
      if (loc == 0){
        int newTime = millis();
        textFont(f);       
        fill(0);
        text("Refreah Rate: " + (newTime - time) + "ms",0,0);
        println("Refreah Rate: " + (newTime - time) + "ms");
        time = millis();
      }
    }*/
  }
}
