#include <Arduino.h>
#include <Wire.h>
#include "song.h"

#define SLAVE_ADDRESS 0x42 // Replace with desired I2C slave address

// Pin Definitions
const int buttonPin = 2;
const int buzzerPin = 3;
const int trigPin = 4;
const int echoPin = 5;
const int ledPin = 6;

// Frequency settings
const int lowFrequency = 100;   // Low frequency in Hz
const int highFrequency = 1000; // High frequency in Hz


// Variables
bool buttonPressed = false;
bool objectDetected = false;
bool isBuzzerOn = false;
const int proximityThreshold = 50; // Threshold distance in centimeters
unsigned long startTime = 0;

void playMelody(int pin) {
  for (unsigned int i = 0; i < sizeof(melody) / sizeof(melody[0]); i++) {
    int noteDuration = 1000 / noteDurations[i];
    tone(pin, melody[i], noteDuration);
    delay(noteDuration * 1.30); // Add a slight pause between notes
    noTone(pin);
    
    if(digitalRead(buttonPin) == LOW) {
      break;
    }
  }
}

void requestEvent() {
  // Handle the request for data from the master
  // You can send data back to the Raspberry Pi using Wire.write()

  if (buttonPressed) {
    Wire.write("ring!"); // Send "ring" message
    Serial.println("Sending: ring");
  } else if (objectDetected) {
    Wire.write("sensor!"); // Send "sensor" message
    Serial.println("Sending: sensor");
  } else {
    Wire.write("!"); // Send "idle" message
    Serial.println("Sending: idle");
  }
}

void receiveEvent(int numBytes) {
  // Handle incoming data from the master
  // You can read data sent by the Raspberry Pi using Wire.read()

  while (Wire.available()) {
    char c = Wire.read();
    Serial.println(c);
    if(c == '1'){
      digitalWrite(ledPin, HIGH);
    }
    if(c == '0'){
      digitalWrite(ledPin, LOW);
    }
  }
}

void setup() {
  // Initialize pins
  pinMode(buttonPin, INPUT_PULLUP);
  pinMode(buzzerPin, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(ledPin, OUTPUT);

  Wire.begin(SLAVE_ADDRESS); // Initialize I2C as a slave
  Wire.onRequest(requestEvent); // Register the request event handler
  Wire.onReceive(receiveEvent); // Register the receive event handler
  
  // Turn off the buzzer and LED initially
  digitalWrite(buzzerPin, LOW);
  digitalWrite(ledPin, LOW);
  
  // Initialize Serial communication
  Serial.begin(9600);
}

void loop() {
   // Check if the button is pressed
  if (digitalRead(buttonPin) == HIGH) {
    delay(250);

    // Turn on the buzzer
    //tone(buzzerPin, lowFrequency);
    buttonPressed = true;

    playMelody(buzzerPin);

    // Print button press action to Serial Monitor
    Serial.println("Button Pressed: Buzzer On");
  } else {
    // Turn off the buzzer
    noTone(buzzerPin);
    buttonPressed = false;

    // Print button release action to Serial Monitor
    Serial.println("Button Released: Buzzer Off");
  }
  
  // Measure distance
  long duration, distance;
  
  digitalWrite(trigPin, LOW);  // Set trigger pin low
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH); // Send 10us pulse
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  duration = pulseIn(echoPin, HIGH);  // Measure the echo pulse duration
  distance = duration * 0.034 / 2;    // Calculate distance in cm

  // Print distance to Serial Monitor
  Serial.print("Distance: ");
  Serial.print(distance);
  Serial.println(" cm");
  
  // Check the measured distance
  if (distance < proximityThreshold) {
    if (startTime == 0) {
      startTime = millis();  // Start the timer if distance is below the threshold
    } else if (millis() - startTime >= 500) {
      objectDetected = true; // Set objectDetected to true if half second has passed
    }
    
    // Print proximity action to Serial Monitor
    Serial.println("Proximity Detected: LED On");
  } else {
    startTime = 0;  // Reset the timer if distance is above the threshold
    objectDetected = false;
    
    // Print proximity action to Serial Monitor
    Serial.println("No Proximity: LED Off");
  }
  
  // Delay for a short time to debounce the inputs
  delay(10);
}
