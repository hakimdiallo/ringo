#ifndef RINGO_ENTITY
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>

#define MESS_LENGTH 512 // Taille maximale du message en octets
#define LISTENING_PORT "3535"// Port UDP de reception de message
#define TCP_PORT "2525"// Prt TCP de duplication
#define NEXT_ENTITY_IP // Adresse IP de la prochaine entitee sur l'anneau
#define NEXT_ENTITY_PORT // Port de la prochaine entitee sur l'anneau
#define IP_DIFF "255.007.012.128"// adresse IP de diffusion
#define PORT_DIFF "7777"// Port de diffusion
#define DEFAULT_IP "127.0.0.1"// Adresse IP par défaut

#define WELC_MESS "WELC"
#define NEWC_MESS "NEWC"
#define ACKC_MESS "ACKC"
#define APPL_MESS "APPL"
#define WHOS_MESS "WHOS"
#define MEMB_MESS "MEMB"
#define GBYE_MESS "GBYE"
#define EYBG_MESS "EYBG"
#define TEST_MESS "TEST"
#define DOWN_MESS "DOWN"
#define DUPL_MESS "DUPL"
#define ACKD_MESS "ACKD"
#define NOTC_MESS "NOTC"

#define WRONG_MESS_FORMAT "Wrong message format..."

typedef struct messages{
  char **list;
  int count;
  int size;
}messages;

typedef struct entity{
  char *next_ip;
  char *next_port;
  char *ip_diff;
  char *port_diff;
} entity;

typedef struct my_ringo_entity{
  char id[8];
  char *listening_port;
  char *udp_port_2;
  char *tcp_port;
  char *ip;
  messages *old_mess;
  entity *entity;
  entity *doublure;
} my_ringo_entity;

my_ringo_entity *init_ringo_entity(char *udp_1, char *udp_2, char *tcp, char *diff);

void *receive_message_udp(void *ringo);

void *doublure_tcp(void *ringo);

void diffusion_udp(my_ringo_entity *ringo);

messages *init_messages(int n);

void add_message(my_ringo_entity *ringo, char *id);

int contains_message(my_ringo_entity *ringo, char *id);

#endif
