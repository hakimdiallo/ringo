CC = gcc
CFLAGS = -W -Wall -g
LDFLAGS = -pthread

SRC = $(wildcard *.c)
OBJS = $(SRC:.c=.o)
AOUT = server.exe

all : $(AOUT)

$(AOUT) : $(OBJS)
	$(CC) -o $@ $^ $(LDFLAGS) $(LDFLAGS)
%.o : %.c
	$(CC) -o $@ -c $< $(CFLAGS) $(LDFLAGS)

clean :
	rm -f *.o

fclean : clean
	rm -f $(AOUT)
