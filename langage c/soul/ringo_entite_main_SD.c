#include "ringo_entite_structure_SD.h"
#include <pthread.h>

int main(int argc, char *argv[]) { // int argc, char const *argv[]
  if(argc < 5){
    printf("4 arguments...\n");
    return -1;
  }
  my_ringo_entity *r = init_ringo_entity(argv[1],argv[2],argv[3],argv[4],argv[5]);
  pthread_t th, th2;
  pthread_create(&th, NULL, receive_message_udp, r);
  pthread_create(&th2, NULL, doublure_tcp, r);
  pthread_join(th,NULL);
  pthread_join(th2,NULL);
  return 0;
}
