#include "entites.h"
#include <stdio.h>

int main(int argc, char const *argv[]) {
  zdd_entites *ent = create_entite((char *)argv[1], (char *)argv[2], (char *)argv[3], (char *)argv[4], (char *)argv[5], (char *)argv[6], (char *)argv[7]);
  if (ent != NULL) {
    //printf("Création d'entité: \n identifiant : %s, adresse %s, port tcp : %s\n", ent->id, ent->ip, &((ent->an).portDiff));
    showEntites(ent);
  }else{
    printf("Erreur\n");
    return 1;
  }
  char *ip = getAddr();
  printf("%s\n", ip);
//  accept_insertion(ent);
  //free_entite(ent);
  /*char *elt = malloc(16);
  string2sadress(elt, (char *)argv[1] );
  printf("%s\n", elt);
  free(elt);*/
  return 0;
}
