TARGET = sketch_jul16a

# Arduino плата (например, uno, nano, mega2560)
BOARD = nano

# Порт, к которому подключена Arduino
PORT = $(PORT)

# Директория Arduino Core и инструменты 
ARDUINO_DIR=$(HOME)/Documents/Arduino

# Опции компилятора и загрузчика
CFLAGS = -Wall -Os
UPLOAD_RATE = 115200

# Пути к утилитам
ARDUINO_AVR_DIR = $(ARDUINO_DIR)/hardware/arduino/avr
AVRDUDE = $(ARDUINO_AVR_DIR)/tools/avr/bin/avrdude
AVRDUDE_CONF = $(ARDUINO_AVR_DIR)/etc/avrdude.conf
ARDUINO_BUILDER = $(ARDUINO_DIR)/arduino-builder

all: upload

$(TARGET).hex: $(TARGET).ino
	$(ARDUINO_BUILDER) -compile -hardware $(ARDUINO_DIR)/hardware -tools $(ARDUINO_DIR)/tools-builder -libraries $(ARDUINO_DIR)/libraries -fqbn=arduino:avr:$(BOARD) -build-path . $(TARGET).ino

upload: $(TARGET).hex
	$(AVRDUDE) -C$(AVRDUDE_CONF) -v -p$(BOARD) -carduino -P$(PORT) -b$(UPLOAD_RATE) -D -Uflash:w:$(TARGET).hex:i

clean:
	rm -f *.hex