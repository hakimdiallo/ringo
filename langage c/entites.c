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
  free(&(an->portUDPNext));
  free(&(an->portDiff));
  free(&(an->ipNext));
  free(&(an->ipDiff));
  free(an);
}

int add_ip(char *p, char *ip){
  /*if((strlen(ip) != 15) ){
    if(DEBUG)
      fprintf(stderr, "format adresse  invalide : %s\n", ip);
    return -1;
  }*/
  p = strdup(ip);
  return 0;
}

int add_port(char *p, char *port){
  int port_i = atoi(port);
  if( (port_i < 0) || (port_i >= 9999)  ){
    if(DEBUG)
      fprintf(stderr, "port incorrect ! : %s\n", port);
    return -1;
  }
  p = (char *)strdup(port);
  return 0;
}

int fill_an(zdd_anneau *an, char *ipNext, char *ipDiff, char *portUDPNext, char *portDiff){
  //if( (add_ip((void *)(an->ipNext), ipNext)) || (add_ip((void *)(an->ipDiff), ipDiff)) || (add_port((void *)(an->portUDP), portUDP)) || (add_port((void *)(an->portUDPNext), portUDPNext)) || add_port((void *)(an->portDiff), portDiff) )
  add_ip((void *)(an->ipNext), ipNext);
  add_ip((void *)(an->ipDiff), ipDiff);
  add_port((void *)(an->portUDPNext), portUDPNext);
  add_port((void *)(an->portDiff), portDiff);
  //    return -1;
  return 0;
}


zdd_entites* create_entite(char *id, char *ip, char *ipDiff, char *portTCP, char *portUDP1, char *portUDP2, char *portUDP, char *portDiff){
  if( (strlen(id) > 8) || (strlen(ip) > 15) || (strlen(portTCP) > 4) || (strlen(portUDP1) > 4) || (strlen(portUDP2) > 4)){
    if(DEBUG)
      fprintf(stderr, "Erreur lors de la création \n");
    return NULL;
  }
  zdd_entites *ent = (zdd_entites *)malloc(sizeof(zdd_entites));
  /*ent->id = malloc(sizeof(char)*8);
  ent->portTCP = malloc(sizeof(char)*4);
  ent->ip = malloc(sizeof(char)*15);*/
  if((strlen(ip)!=15)||(strlen(id)!=8)||(strlen(ipDiff)!=15)||(strlen(portUDP1)!=4)||(strlen(portUDP)!=4)||(strlen(portDiff)!=4)){
    if(DEBUG)
      fprintf(stderr, "Erreur  : création d'entité");
    return NULL;
  }
  ent->id = strdup(id);
  ent->ip = strdup(ip);
  ent->portTCP = strdup(portTCP);
  ent->portUDP1 = strdup(portUDP1);
  ent->portUDP2 = strdup(portUDP2);
  ent->an = (zdd_anneau *)malloc(sizeof(zdd_anneau));
  ent->an->ipDiff = strdup(ipDiff);
  ent->an->ipNext = strdup(ip);
  ent->an->portDiff = strdup(portDiff);
  ent->an->portUDPNext = strdup(portUDP1);
  return ent;
}

void showEntites(zdd_entites *ent){
  printf("INFO entité ---------------\nidentifiant : %s\n", ent->id);
  printf("adresse : %s\n", ent->ip);
  printf("port UDP 1 : %s\n", ent->portUDP1);
  printf("port UDP 2 : %s\n", ent->portUDP2);
  printf("port TCP : %s\n", ent->portTCP);
  if( &(ent->an) != NULL ){
    printf("Anneau 1 ---------------\nIp next : %s\n", ent->an->ipNext);
    printf("port UDP next : %s\n", ent->an->portUDPNext);
    printf("Ip multidiffusion : %s\n", ent->an->ipDiff);
    printf("port multidiffusion : %s\n", ent->an->portDiff);
  }
}

void free_entite(zdd_entites *ent){
  free(ent->id);
  free(ent->portTCP);
  free(ent->ip);
  free_an(ent->an);
  if(ent->an_d != NULL)
    free_an(ent->an_d);
  free(ent);
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
}*/
