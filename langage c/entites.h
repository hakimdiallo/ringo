#ifndef _ENTITES_H
  #define _ENTITES_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>

typedef struct in_addr zdd_adresse4;

typedef struct {
  char *portUDPNext;   //port d'écoute  UDP de l'entité suivante sur l'anneau
  char *portDiff; //Port de multidiffusion
  char *ipNext; //Adresse IP de la machine suivante sur l'anneau
  char *ipDiff; //Adresse IPV4 de multi-diffusion
} zdd_anneau;

typedef struct {
  char *portUDP1;  //port d'écoute pour recevoir les messages UDP de l'entité précédente sur l'anneau
  char *portUDP2;  //port d'écoute pour recevoir les messages UDP de l'entité précédente sur l'anneau
  char *id;
  char *portTCP;
  char *ip;
  zdd_anneau *an;//Caractéristique lié à l'anneau
  zdd_anneau *an_d;//Caractéristique lié à l'anneau doubleur
} zdd_entites;

//alloue la mémoire pour la structure zdd_anneau
void malloc_an(zdd_anneau *an);

//libère la structure zdd_anneau
void free_an(zdd_anneau *an);

//rempli la strucure zdd_anneau
int fill_an(zdd_anneau *an, char *ipNext, char *ipDiff, char *portUDPNext, char *portDiff);


//Créer une nouvelle entité avec ses caractéristiques
zdd_entites* create_entite(char *id, char *ip, char *ipDiff, char *portTCP, char *portUDP1, char *portUDP2, char *portUDP,char *portDiff);
//Voir les informations d'une entité
void showEntites(zdd_entites *ent);

//détruit une entité
void free_entite(zdd_entites *ent);


//Demande d'insertion
int ask_insertion(zdd_entites *new_ent, char adress[], char portTCP[]);

//Autorisation d'insertion
int accept_insertion(zdd_entites *ent);

//
const char * string2sadress(char *res, char *addr);

char * adress2string(char addr[]);
#endif
