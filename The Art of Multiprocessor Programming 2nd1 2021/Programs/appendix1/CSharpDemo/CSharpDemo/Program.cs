using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

namespace CSharpDemo
{
    class Program
    {
        static void Main(string[] args)
        {
            Thread[] thread = new Thread[8];

            // simple threads

            // create threads
            for (int i = 0; i < thread.Length; i++)
            {
                String message = "Hello world from thread " + i;
                ThreadStart hello = delegate()
                {
                    Console.WriteLine(message);
                };
                thread[i] = new Thread(hello);
            }
            // start threads
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Start();
            }
            // wait for them to finish
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Join();
            }
            Console.WriteLine("done!");

            // thread-local

            // create threads
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i] = new Thread(delegate()
                {
                    Console.WriteLine("Hello from thread " + ThreadID.get());
                });
            }
            // start threads
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Start();
            }
            // wait for them to finish
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Join();
            }
            Console.WriteLine("done!");

            // queue test

            const int QSIZE = 16;
            bool[] map = new bool[QSIZE];
            Queue<int> q = new Queue<int>(QSIZE);
            thread = new Thread[2 * QSIZE];
            for (int i = 0; i < QSIZE; i++)
            {
                int k = i;
                thread[i] = new Thread(delegate()
                {
                    q.Enq(k);
                });
                thread[i + QSIZE] = new Thread(delegate()
                {
                    int j = q.Deq();
                    if (j < 0 || j >= map.Length)
                    {
                        Console.WriteLine("dequeued item out of range: {0}", j);
                    }
                    map[j] = true;
                });
            }
            // start threads
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Start();
            }
            // wait for them to finish
            for (int i = 0; i < thread.Length; i++)
            {
                thread[i].Join();
            }
            for (int i = 0; i < map.Length; i++)
            {
                if (!map[i])
                {
                    Console.WriteLine("undequeued: {0}", i);
                }
            }
            Console.WriteLine("done!");
        }
    }
}
