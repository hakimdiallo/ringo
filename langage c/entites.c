#include "entites.h"

#define DEBUG 1

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


void malloc_an(zdd_anneau *an){
  /*an->portUDPNext = NULL;
  an->portDiff = NULL;
  an->ipNext = NULL;
  an->ipDiff = NULL;*/
  an = (zdd_anneau*)malloc(sizeof(zdd_anneau));
}

void free_an(zdd_anneau *an){
  free(an->portUDPNext);
  free(an->portDiff);
  free(an->ipNext);
  free(an->ipDiff);
  free(an);
}

int ipIsOk(char *ip){
  if((strlen(ip) != 15) ){
    if(DEBUG)
      fprintf(stderr, "format adresse  invalide : %s\n", ip);
    return 0;
  }
  return 1;
}

int idIsOk(char *id){
  if((strlen(id) != 8) ){
    if(DEBUG)
      fprintf(stderr, "format identifiant  invalide : %s\n", id);
    return 0;
  }
  return 1;
}

int portIsOk(char *port){
  if((strlen(port) != 4) ){
    if(DEBUG)
      fprintf(stderr, "format port  invalide : %s\n", port);
    return 0;
  }
  return 1;
}

int messIsOk(char *mess){
  if((strlen(mess) > 512) ){
    if(DEBUG)
      fprintf(stderr, "format message  invalide : taille %d\n", (int)strlen(mess));
    return 0;
  }
  return 1;
}

int fill_an(zdd_anneau *an, char *ipNext, char *ipDiff, char *portUDPNext, char *portDiff){
  an->ipDiff = strdup(ipDiff);
  an->ipNext = strdup(ipNext);
  an->portDiff = strdup(portDiff);
  an->portUDPNext = strdup(portUDPNext);
  return 0;
}


zdd_entites* create_entite(char *id, char *ip, char *ipDiff, char *portTCP, char *portUDP1, char *portUDP2, char *portUDP, char *portDiff){
  if( (strlen(id) > 8) || (strlen(ip) > 15) || (strlen(portTCP) > 4) || (strlen(portUDP1) > 4) || (strlen(portUDP2) > 4)){
    if(DEBUG)
      fprintf(stderr, "Erreur lors de la création \n");
    return NULL;
  }
  zdd_entites *ent = (zdd_entites *)malloc(sizeof(zdd_entites));
  if(ipIsOk(ip) && idIsOk(id) && ipIsOk(ipDiff) && portIsOk(portUDP1) && portIsOk(portTCP) && portIsOk(portUDP2) && portIsOk(portDiff)){
    ent->id = strdup(id);
    ent->ip = strdup(ip);
    ent->portTCP = strdup(portTCP);
    ent->portUDP1 = strdup(portUDP1);
    ent->portUDP2 = strdup(portUDP2);
    fill_an(&(ent->an), ip, ipDiff, portUDP1, portDiff);
    return ent;
  }
}

void showEntites(zdd_entites *ent){
  printf("INFO entité ---------------\nidentifiant : %s\n", ent->id);
  printf("adresse : %s\n", ent->ip);
  printf("port UDP 1 : %s\n", ent->portUDP1);
  printf("port UDP 2 : %s\n", ent->portUDP2);
  printf("port TCP : %s\n", ent->portTCP);
  if( &(ent->an) != NULL ){
    printf("Anneau 1 ---------------\nIp next : %s\n", (ent->an).ipNext);
    printf("port UDP next : %s\n", (ent->an).portUDPNext);
    printf("Ip multidiffusion : %s\n", ent->an.ipDiff);
    printf("port multidiffusion : %s\n", ent->an.portDiff);
  }
}

void free_entite(zdd_entites *ent){
  free(ent->id);
  free(ent->portTCP);
  free(ent->ip);
  free_an(&(ent->an));
/*  if(ent->an_d != NULL)
    free_an(ent->an_d);*/
  free(ent);
}

const char *string2addressApp(char *ip){
  char *res = malloc(sizeof(char) * 16);
  char *addr = strdup(ip);
  addr = strdup(ip);
  char *elt = NULL;
  elt = strtok(addr, ".");
  //res = "\0";
  int i;
  for (i = 0; i <= 3; i++) {
    if(strlen(elt)==1)
      sprintf(res, "00%s%s", res, elt);
    if(strlen(elt)==2)
      sprintf(res, "0%s%s", res, elt);
    if(strlen(elt)==3)
        sprintf(res, "%s%s", res, elt);
    elt = strtok(NULL, ".");
    if(i!=3)
      sprintf(res, "%s%s", res, ".");
  }
  printf("res %s\n", res);
  free(addr);
	  return res;
  }
/*
int ask_insertion(zdd_entites *new_ent, char *adress, char *portTCP){
  int port = atoi((const char*)portTCP);
  struct sockaddr_in adress_sock;
  adress_sock.sin_family = AF_INET;
  adress_sock.sin_port = htons(port);
  char *elt = malloc( sizeof(char) * 16);
  string2sadress(elt, adress);
  inet_aton(elt, &adress_sock.sin_addr);
  free(elt);
  int descr = socket(PF_INET, SOCK_STREAM, 0);
  int r = connect(descr,(struct sockaddr*)&adress_sock, sizeof(struct sockaddr_in));
  if(r == -1){
    if(DEBUG)
      fprintf(stderr, "connexion impossible\n");
    return -1;
  }
  char mess_r[47];
  int size_rec=read (descr,mess_r,46*sizeof(char));
  mess_r[size_rec - 2]='\0';

  printf("message reçu%s\n", mess_r);
  char *decp = strtok(mess_r, " ");
  if( strcmp(decp, WE) != 0 ){
    if(DEBUG)
      fprintf(stderr, "Erreur : commande %s incorrecte\n", decp);
    return -1;
  }
  char *ip = NULL;
  char *portUDP = NULL;
  char *port_diff = NULL;
  char *ip_diff = NULL;

  ip = strtok(NULL, " ");
  if(ip != NULL){
    portUDP = strtok(NULL, " ");
    if(portUDP != NULL){
      ip_diff = strtok(NULL, " ");
      if(ip_diff != NULL){
        port_diff = strtok(NULL, " ");
      }
    }
    strncpy(((new_ent->an).ipNext), ip, 15);
    strncpy(((new_ent->an).portUDPNext), portUDP, 4);
    strncpy(((new_ent->an).portDiff), port_diff, 4);
    strncpy(((new_ent->an).ipDiff), ip_diff, 4);
    char mess[26];
    sprintf(mess, "%s %s %s\n", _CMD_NEWC, new_ent->ip, ((new_ent->an).portUDP));
    write(descr, mess, strlen(mess));
    int size_rec=read (descr,mess_r,5*sizeof(char));
    mess_r[size_rec - 2]='\0';
    printf("message reçu%s\n", mess_r);
    return 0;
  }
  return -1;
}

int accept_insertion(zdd_entites *ent){
  int sock=socket(PF_INET,SOCK_STREAM,0);
  struct sockaddr_in address_sock;
  address_sock.sin_family = AF_INET;
  int port = atoi(ent->portTCP);
  address_sock.sin_port = htons(port);
  address_sock.sin_addr.s_addr = htonl(INADDR_ANY);
  int r = bind(sock, (struct sockaddr *)&address_sock, sizeof(struct sockaddr_in));
  if(r == 0){
    r = listen(sock, 0);
    while(1){
      struct sockaddr_in caller;
      socklen_t size = sizeof(caller);
      int sock2=accept(sock,(struct sockaddr *)&caller,&size);
      if(sock2>=0){
        printf("connexion accepté de \n");
        char *mess = malloc(sizeof(char) * 47);
        sprintf(mess, "%s %s %s %s %s\n", WELC_MESS, ((ent->an).ipNext), ((ent->an).portUDPNext), ((ent->an).ipDiff), ((ent->an).portDiff));
        send(sock2, mess, strlen(mess) * sizeof(char), 0);
        int recu = recv(sock2, mess, 46*sizeof(char), 0);
        mess[recu] = '\0';
        printf("message reçu%s\n", mess);
        char *ip = NULL;
        char *port = NULL;
        char *decp = strtok(mess, " ");
        if( strcmp(decp, NEWC_MESS) != 0 ){
          if(DEBUG)
            fprintf(stderr, "Erreur : commande %s incorrecte\n", decp);
          return -1;
        }
        ip = strtok(NULL, " ");
        if ( (ip != NULL) && (strlen(ip)==15 ) ) {
          port = strtok(NULL, " ");
          if( (port!=NULL) && (strlen(port)<=4) && (atoi(port)<9999)){
            strncpy(((ent->an).ipNext), ip, 15);
            strncpy(((ent->an).portUDPNext), port, 4);
          }else{
            return -1;
          }
        }else{
          return -1;
        }
        mess = realloc(mess, sizeof(char) * 6);
        strcpy(mess, "ACKC\n");
        send(sock2, mess, strlen(mess)*sizeof(char), 0);
        free(mess);
      }
      close(sock2);
    }
  }
}
*/

const char * string2sadress(char *res, char *ip){
  char * elt = NULL;
  char *addr = malloc(sizeof(char) * 16);
  strcpy(addr, (const char *)ip);
  elt = strtok(addr, ".");
  int i = 1;
  while ((elt != NULL)) {
    if( (elt[0] == '0') && (elt[1] == '0') ){
      elt[0] = elt[2];
      elt[1] = '\0';
    }else {
      if(elt[0] == '0'){
        elt[0] = elt[1];
        elt[1] = elt[2];
        elt[2] = '\0';
      }
    }
    printf("elt :%s\n", elt);
    printf("res : %s\n", res);
    //strcat(res, elt);

    if(i < 4)
      sprintf(res, "%s%s.", res, elt);
    else
      sprintf(res, "%s%s", res, elt);
    i++;
    elt = strtok(NULL, ".");
  }
  free(addr);
  printf(" resultat %s\n", res);
  return  res;
}

char * adress2string(char addr[]){
  return addr;
}
