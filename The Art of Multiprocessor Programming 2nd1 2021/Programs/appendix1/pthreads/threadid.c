/*
 * threads.c
 *
 * Created on January 2, 2006, 10:54 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved. 
 */

#include <pthread.h>

/**
 * Illustrates thread-local storage with Pthreads
 * @author Maurice Herlihy
 */

pthread_key_t key;		/* key */
int counter;			/* generates unique value */
pthread_mutex_t mutex;		/* synchronizes counter */

threadID_init() {
  pthread_mutex_init(&mutex, NULL);
  pthread_key_create(&key, NULL);
  counter = 0;
}
int threadID_get() {
  int* id = (int*)pthread_getspecific(key);
  if (id == NULL) {		/* first time? */
    id = (int*)malloc(sizeof(int));
    pthread_mutex_lock(&mutex);
    *id = counter++;
    pthread_setspecific(key, id);
    pthread_mutex_unlock(&mutex);
  }
  return *id;
}
void* hello(void* arg) {
  printf("Hello from thread %i\n", threadID_get());
}
#define NUM_THREADS 8
int main() {
  pthread_t thread[NUM_THREADS];
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
