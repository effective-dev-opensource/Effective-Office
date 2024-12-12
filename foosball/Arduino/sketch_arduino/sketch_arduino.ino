const int laserPin1 = 7;
const int sensorPin1 = 2;
const int laserPin2 = 8;
const int sensorPin2 = 3;

int state1 = 0;
int prevState1 = 0;
int state2 = 0;
int prevState2 = 0;

unsigned long lastDebounceTime1 = 0;
unsigned long lastDebounceTime2 = 0;
const unsigned long debounceDelay = 50;

inline void handleSensorChange(int& state, int& prevState, unsigned long& lastDebounceTime, int sensorPin, int outputValue) {
  int reading = digitalRead(sensorPin);

  if (reading != prevState) {
    lastDebounceTime = millis();
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (reading != state) {
      state = reading;

      if (state == LOW) {
        Serial.write(outputValue);
      }
    }
  }
  prevState = reading;
}

void setup() {
  pinMode(laserPin1, OUTPUT);
  pinMode(laserPin2, OUTPUT);
  pinMode(sensorPin1, INPUT);
  pinMode(sensorPin2, INPUT);
  Serial.begin(115200);

  digitalWrite(laserPin1, HIGH);
  digitalWrite(laserPin2, HIGH);
}

void loop() {
  handleSensorChange(state1, prevState1, lastDebounceTime1, sensorPin1, 0x00);
  handleSensorChange(state2, prevState2, lastDebounceTime2, sensorPin2, 0x01);
}
