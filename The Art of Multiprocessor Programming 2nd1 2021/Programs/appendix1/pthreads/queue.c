/*
 * queue.c
 *
 * Created on January 2, 2006, 10:53 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved. 
 */
 
/**
 * Simple bounded queue using Pthreads
 * @author Maurice Herlihy
 */

#include <pthread.h>
#define QSIZE 16

typedef struct {
  int buf[QSIZE];
  long head, tail;
  pthread_mutex_t *mutex;
  pthread_cond_t *notFull, *notEmpty;
} queue;

queue* queue_init();
void queue_enq(queue* q, int item);
int queue_deq(queue* q);

queue* q;
int map[QSIZE];

void* doEnq(void* arg) {
  int i = (int)arg;
  queue_enq(q, i);
}

void* doDeq(void* arg) {
  int j = queue_deq(q);
  if (j < 0 || j >= QSIZE) {
    printf("dequeued value out of bounds: %i\n", j);
  }
  map[j] = 1;
}

int main() {
  int i,j;
  pthread_t enqThread[QSIZE];
  pthread_t deqThread[QSIZE];
  q = (queue*)queue_init();
  for (i = 0; i < QSIZE; i++) {
    map[i] = 0;
  }
  for (i = 0; i < QSIZE; i++) {
    if ( pthread_create(&deqThread[i], NULL, doDeq, (void*)i) != 0 ) {
      printf("pthread_create() error");
      exit();
    }
    if ( pthread_create(&enqThread[i], NULL, doEnq, (void*)i) != 0 ) {
      printf("pthread_create() error");
      exit();
    }
  }
  for (i = 0; i < QSIZE; i++) {
    pthread_join(enqThread[i], NULL);
    pthread_join(deqThread[i], NULL);
  }
  for (i = 0; i < QSIZE; i++) {
    if (!map[i]) {
      printf("deq missed a value %i\n", i);
    }
  }
  printf("queue ckecked out\n");
}

void queue_enq(queue* q, int item) {
  // lock object
  pthread_mutex_lock (q->mutex);
  // wait while full
  while (q->tail - q->head == QSIZE) {
    pthread_cond_wait (q->notFull, q->mutex);
  }
  q->buf[q->tail % QSIZE] = item;
  q->tail++;
  // release lock
  pthread_mutex_unlock (q->mutex);
  // inform waiting dequeuers
  pthread_cond_signal (q->notEmpty);
}

int queue_deq(queue* q) {
  int result;
  // lock object
  pthread_mutex_lock (q->mutex);
  // wait while full
  while (q->tail == q->head) {
    pthread_cond_wait (q->notEmpty, q->mutex);
  }
  result = q->buf[q->head % QSIZE];
  q->head++;
  // release lock
  pthread_mutex_unlock (q->mutex);
  // inform waiting dequeuers
  pthread_cond_signal (q->notFull);
  return result;
}

queue *queue_init (void) {
  queue *q;
  q = (queue*)malloc (sizeof (queue));
  if (q == NULL) return (NULL);
  q->head = 0;
  q->tail = 0;
  q->mutex = (pthread_mutex_t*) malloc (sizeof (pthread_mutex_t));
  pthread_mutex_init (q->mutex, NULL);
  q->notFull = (pthread_cond_t*) malloc (sizeof (pthread_cond_t));
  pthread_cond_init (q->notFull, NULL);
  q->notEmpty = (pthread_cond_t*) malloc (sizeof (pthread_cond_t));
  pthread_cond_init (q->notEmpty, NULL);
	
  return (q);
}

void queue_delete (queue* q) {
  pthread_mutex_destroy (q->mutex);
  free (q->mutex);	
  pthread_cond_destroy (q->notFull);
  free (q->notFull);
  pthread_cond_destroy (q->notEmpty);
  free (q->notEmpty);
  free (q);
}
