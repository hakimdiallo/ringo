/**
    Copyright (C) 2016

    Authors:
      - Souleymane DIALLO diallosouleymane.07@gmail.com
      - Jules Camille ZIRIGA julesziriga@gmail.com
      - Cyrille DE

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

**/

#include "ringo_entite_structure_SD.h"

char *string2addressApp(char *ip){
  char *res = malloc(sizeof(char) * 15);
  sprintf(res, "");
  char *addr = strdup(ip);
  addr = strdup(ip);
  char *elt = NULL;
  elt = strtok(addr, ".");
  //res = "\0";
  int i;
  for (i = 0; i <= 3; i++) {
    if(strlen(elt)==1)
      sprintf(res, "%s00%s", res, elt);
    if(strlen(elt)==2)
      sprintf(res, "%s0%s", res, elt);
    if(strlen(elt)==3)
        sprintf(res, "%s%s", res, elt);
    elt = strtok(NULL, ".");
    if(i!=3)
      sprintf(res, "%s%s", res, ".");
  }
  free(addr);
	  return res;
}

char* getAddr(){
  struct ifaddrs *myaddrs, *ifa;
  struct sockaddr_in *s4;
  int status;
  char *ip=(char *)malloc(64*sizeof(char));
  status = getifaddrs(&myaddrs);
  if (status != 0){
    perror("Probleme de recuperation d'adresse IP");
    exit(1);
  }
  for (ifa = myaddrs; ifa != NULL; ifa = ifa->ifa_next){
    if (ifa->ifa_addr == NULL) continue;
    if ((ifa->ifa_flags & IFF_UP) == 0) continue;
    if ((ifa->ifa_flags & IFF_LOOPBACK) != 0) continue;
    if (ifa->ifa_addr->sa_family == AF_INET){
      s4 = (struct sockaddr_in *)(ifa->ifa_addr);
      freeifaddrs(myaddrs);
      if (inet_ntop(ifa->ifa_addr->sa_family, (void *)&(s4->sin_addr),
      ip, 64*sizeof(char)) != NULL)
        return ip;
    }
  }
  return NULL;
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

my_ringo_entity *init_ringo_entity(char *udp_1, char *udp_2, char *tcp, char *diff, char *ipDiff){
  my_ringo_entity *ringo = (my_ringo_entity *)malloc(sizeof(my_ringo_entity));
  ringo->entity = (entity *)malloc(sizeof(entity));
  ringo->listening_port = strdup(udp_1);
  ringo->udp_port_2 = strdup(udp_2);
  ringo->tcp_port = strdup(tcp);
  ringo->ip = getAddr();
  ringo->entity->next_ip = strdup(string2addressApp());
  ringo->entity->next_port = strdup(udp_1);
  ringo->entity->ip_diff = strdup(string2addressApp(ipDiff));
  ringo->entity->port_diff = strdup(diff);
  ringo->old_mess = init_messages(10);
  ringo->doublure = NULL;
  return ringo;
}

messages *init_messages(int n){
  messages *mess = (messages *)malloc(n * sizeof(messages));
  mess->count = 0;
  mess->size = n;
  return mess;
}

void add_message(my_ringo_entity *ringo, char *id){
  if ( ringo->old_mess->count == ringo->old_mess->size ) {
    ringo->old_mess->size += 10;
    ringo->old_mess->list = realloc(ringo->old_mess->list, ringo->old_mess->size * sizeof(messages));
  }
  ringo->old_mess->list[ringo->old_mess->count] = strdup(id);
  ringo->old_mess->count += 1;
}

int contains_message(my_ringo_entity *ringo, char *id){
  int i;
  for (i = 0; i < ringo->old_mess->count; i++) {
    if ( !strcmp(ringo->old_mess->list[i],id) ) {
      return 1;
    }
  }
  return 0;
}

entity *init_doublure(char *next_ip, char *next_port, char *next_ip_diff, char *next_port_diff){
  entity *ent = (entity *)malloc(sizeof(entity));
  ent->next_ip = strdup(next_ip);
  ent->next_port = strdup(next_port);
  ent->ip_diff = strdup(next_ip_diff);
  ent->port_diff = strdup(next_port_diff);

  return ent;
}

void *receive_message_udp(void *smt) {
  my_ringo_entity *ringo = (my_ringo_entity *)smt;
  int sock = socket(PF_INET,SOCK_DGRAM,0);
  sock = socket(PF_INET,SOCK_DGRAM,0);
  struct sockaddr_in address_sock;
  address_sock.sin_family = AF_INET;
  address_sock.sin_port = htons(atoi(ringo->listening_port));
  address_sock.sin_addr.s_addr = htonl(INADDR_ANY);
  int r = bind(sock,(struct sockaddr *)&address_sock,sizeof(struct sockaddr_in));
  struct sockaddr_in emet;
  socklen_t a = sizeof(emet);
  if(r==0){
    char *mess = (char *)malloc(MESS_LENGTH * sizeof(char));
    while(1){
      int rec = recvfrom(sock,mess,MESS_LENGTH,0,(struct sockaddr *)&emet,&a);
      mess[rec] = '\0';
      if ( ringo->old_mess != NULL && strcmp(mess,ringo->old_mess)) {
        ringo->old_mess = strdup(mess);
        char *pref = strtok(mess," ");
        if( pref != NULL ){
          if(!strcmp(pref,APPL_MESS)){// message application
            char *idm = strtok(mess," ");
            if ( idm != NULL ) {
              if ( !contains_message(ringo, idm) ) {
                char *mess = (char *)malloc(MESS_LENGTH * sizeof(char));
                mess = strcat(mess,(char *)MEMB_MESS);
                mess = strcat(mess," ");
                mess = strcat(mess,"########");
                mess = strcat(mess," ");
                mess = strcat(mess,ringo->id);
                mess = strcat(mess," ");
                mess = strcat(mess,ringo->ip);
                mess = strcat(mess," ");
                mess = strcat(mess,ringo->listening_port);
                mess = strcat(mess,"\n");
                printf("sending to next entity...\n");
              }
              else{
                printf("Message déjà vu...\n");
              }
            }
            else{
              printf("%s\n", WRONG_MESS_FORMAT);
            }
          }
          else if(!strcmp(pref,WHOS_MESS)){
            char *idm = strtok(mess," ");
            if ( idm != NULL ) {
              if ( !contains_message(ringo, idm) ) {

                printf("sending to next entity...\n");
              }
              else{
                printf("Message déjà vu...\n");
              }
            }
            else{
              printf("%s\n", WRONG_MESS_FORMAT);
            }
          }
          else if(!strcmp(pref,MEMB_MESS)){
            printf("got welcome\n");
          }
          else if(!strcmp(pref,GBYE_MESS)){
            printf("got welcome\n");
          }
          else if(!strcmp(pref,EYBG_MESS)){
            printf("got welcome\n");
          }
          else if(!strcmp(pref,TEST_MESS)){
            printf("got welcome\n");
          }
          else if(!strcmp(pref,DOWN_MESS)){
            printf("got welcome\n");
          }
          else{
            printf("%s\n", WRONG_MESS_FORMAT);
          }
        }
        else{
          printf("%s\n", WRONG_MESS_FORMAT);
        }
      }
      else{
        printf("Already got the message. Aborting...\n");
        ringo->old_mess = NULL;
      }
    }
  }
  return NULL;
}

void *doublure_tcp(void *smt){
  my_ringo_entity *ringo = (my_ringo_entity *)smt;
  int sock = socket(PF_INET,SOCK_STREAM,0);
  struct sockaddr_in address_sock;
  address_sock.sin_family = AF_INET;
  address_sock.sin_port = htons(atoi(ringo->tcp_port));
  address_sock.sin_addr.s_addr = htonl(INADDR_ANY);
  int r = bind(sock,(struct sockaddr *)&address_sock,sizeof(struct sockaddr_in));
  if( r == 0 ){
    r = listen(sock,0);
    while(1){
      struct sockaddr_in caller;
      socklen_t size = sizeof(caller);
      int sock2 = accept(sock,(struct sockaddr *)&caller,&size);
      printf("Nouvelle connexion...\n");
      if( sock2 >= 0 ){
        // Pas encore de DUPLICATION
        if( ringo->doublure == NULL ){
          char *mess = (char *)malloc(MESS_LENGTH * sizeof(char));
          mess = strcat(mess,(char *)WELC_MESS);
          mess = strcat(mess," ");
          mess = strcat(mess,ringo->entity->next_ip);
          mess = strcat(mess," ");
          mess = strcat(mess,ringo->entity->next_port);
          mess = strcat(mess," ");
          mess = strcat(mess,ringo->entity->ip_diff);
          mess = strcat(mess," ");
          mess = strcat(mess,ringo->entity->port_diff);
          mess = strcat(mess,"\n");
          send(sock2,mess,strlen(mess)*sizeof(char),0);
          char *buff = (char *)malloc(MESS_LENGTH * sizeof(char));
          int recu = recv(sock2,buff,(MESS_LENGTH - 1)*sizeof(char),0);
          buff[recu] = '\0';
          char *pref = strtok(buff," ");
          if( pref != NULL ){
            // Pour l'INSERTION
            if (!strcmp(pref,NEWC_MESS)){
              printf("Nouvelle insertion dans l'anneau...\n");
              char *ip = strtok(buff," ");
              if( ip != NULL ){
                char *port = strtok(buff,"\n");
                if( port != NULL ){
                  strcpy(ringo->entity->next_ip,ip);
                  strcpy(ringo->entity->next_port,port);
                  strcpy(mess,ACKC_MESS);
                  mess = strcat(mess,"\n");
                  send(sock2,mess,strlen(mess)*sizeof(char),0);
                  printf("Insertion reussie...\n");
                }
              }
            }
            // Pour la DUPLICATION
            else if(!strcmp(pref,DUPL_MESS)){
              printf("Duplication de l'entite...\n");
              char *ip = strtok(buff," ");
              if( ip != NULL ) {
                char *port = strtok(buff," ");
                if( port != NULL ){
                  char *ip_diff = strtok(buff," ");
                  if( ip_diff != NULL ) {
                    char *port_diff = strtok(buff,"\n");
                    if( port_diff != NULL ){
                      ringo->doublure = init_doublure(ip,port,ip_diff,port_diff);
                      char *mess = (char *)malloc(MESS_LENGTH * sizeof(char));
                      strcpy(mess,ACKD_MESS);
                      strcat(mess," ");
                      strcat(mess,ringo->listening_port);
                      strcat(mess,"\n");
                      send(sock2,mess,strlen(mess)*sizeof(char),0);
                      printf("Duplication reussie\n");
                    }
                  }
                }
              }
            }
          }
        }
        // DUPLICATION existe deja : INSERTION et DUPLICATION impossible
        else{
          printf("Tentative d'insertion ou de duplication impossible...\n");
          char *mess = (char *)malloc(MESS_LENGTH * sizeof(char));
          strcpy(mess,NOTC_MESS);
          strcat(mess,"\n");
          send(sock2,mess,strlen(mess)*sizeof(char),0);
        }
      }
      close(sock2);
    }
  }
  return NULL;
}

void send_to_next(entity *ent, char *mess){
  int sock=socket(PF_INET,SOCK_DGRAM,0);
  struct addrinfo *first_info;
  struct addrinfo hints;
  memset(&hints, 0, sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype=SOCK_DGRAM;
  int r=getaddrinfo(ent>next_ip,ent->next_port,&hints,&first_info);
  if(r==0){
    if(first_info!=NULL){
      struct sockaddr *saddr=first_info->ai_addr;
      sendto(sock,mess,strlen(mess),0,saddr(socklen_t)sizeof(struct sockaddr_in));
    }
  }
}
