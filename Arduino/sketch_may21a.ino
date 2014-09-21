#include <Adafruit_NeoPixel.h>

#define PIN 6

Adafruit_NeoPixel strip = Adafruit_NeoPixel(60, PIN, NEO_GRB + NEO_KHZ800);

int loc;
int r;
int g;
int b;
int i;

void setup() {
  // initialize serial:
  Serial.begin(9600);
  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
}


void loop() {
  if (i == 4){
      strip.setPixelColor(loc,r,g,b);
      i = 0;
      if (loc == 0){
        strip.show();
      }
  }
}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  while (Serial.available()) {
    switch(i){
     case (0):
       loc = Serial.read();
       break;
     case (1):
       r = Serial.read();
       break;
     case (2):
       g = Serial.read();
       break;
     case (3):
       b = Serial.read();
       break;  
    }
    i++;
  }
}

