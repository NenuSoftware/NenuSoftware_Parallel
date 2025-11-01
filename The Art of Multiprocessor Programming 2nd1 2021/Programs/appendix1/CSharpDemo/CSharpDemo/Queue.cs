
using System.Threading;

namespace CSharpDemo
{
    class Queue<T>
    {
        int head, tail;
        T[] items;
        public Queue(int capacity)
        {
            items = new T[capacity];
            head = tail = 0;
        }
        public void Enq(T x)
        {
            Monitor.Enter(this);
            try
            {
                while (tail - head == items.Length)
                {
                    Monitor.Wait(this); // queue is empty
                }
                items[(tail++) % items.Length] = x;
                Monitor.PulseAll(this);    // notify waiting dequeuers
            }
            finally
            {
                Monitor.Exit(this);
            }

        }

        public T Deq()
        {
            Monitor.Enter(this);
            try
            {
                while (tail == head)
                {
                    Monitor.Wait(this); // queue is full
                }
                T y = items[(head++) % items.Length];
                Monitor.PulseAll(this);    // notify waiting enqueuers
                return y;
            }
            finally
            {
                Monitor.Exit(this);
            }

        }
    }
}
