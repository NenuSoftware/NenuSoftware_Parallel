using System;
using System.Collections.Generic;
using System.Text;

namespace CSharpDemo
{
    class Counter
    {
        int value;
        int GetAndIncrement()
        {
            lock (this)
            {
                return value++;
            }
        }
    }
}
