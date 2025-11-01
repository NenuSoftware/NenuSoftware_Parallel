using System;
using System.Threading;
namespace CSharpDemo
{
    class ThreadID
    {
        [ThreadStatic] static int myID;
        static int counter;
        public static int get()
        {
            if (myID == 0)
            {
                myID = Interlocked.Increment(ref counter);
            }
            return myID - 1;
        }
    }
}

