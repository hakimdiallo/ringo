CC = gcc
CFLAGS =
EXEC = ringo_main
HEADERS = $(wildcard *.h)
OBJECTS = $(patsubst %.c, %.o, $(wildcard *.c))

all: $(EXEC)

$(EXEC): $(OBJECTS)
	$(CC) -o $@ $^ $(CPPFLAGS) $(CFLAGS)

%.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) -c $< $(CPPFLAGS)

mrproper: clean
	rm -f $(EXEC)

clean:
	rm -f $(OBJECTS)
	rm -f *~
