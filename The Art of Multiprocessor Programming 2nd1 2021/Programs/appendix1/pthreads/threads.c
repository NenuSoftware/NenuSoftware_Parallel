/*
 * threads.c
 *
 * Created on January 2, 2006, 11:43 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved. 
 */


#include <pthread.h>
/**
 * Illustrates Pthread creation and joining
 * @author Maurice Herlihy
 */
#define NUM_THREADS 8
void* hello(void* arg) {
  printf("Hello from thread %i\n", (int)arg);
}

int main() {
  pthread_t thread[NUM_THREADS];
  int status;
  int i;

  for (i = 0; i < NUM_THREADS; i++) {
    if ( pthread_create(&thread[i], NULL, hello, (void*)i) != 0 ) {
      printf("pthread_create() error");
      exit();
    }
  }
  for (i = 0; i < NUM_THREADS; i++) {
    pthread_join(thread[i], NULL);
  }
}
