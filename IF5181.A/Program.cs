using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IF5181.A
{
    class Program
    {
        static void Main(string[] args)
        {
            Tree tree = null;
            Console.Write("Input  : ");
            var command = Console.ReadLine().Split(' ');

            while (command != null && command.Length > 0 && command[0] != "exit")
            {
                Console.Write("Output : ");

                if (command[0] == "add")
                {
                    if (tree == null)
                    {
                        tree = new Tree(command[1]);
                        Console.WriteLine(string.Format("Word '{0}' added successfully", command[1]));
                    }
                    else
                    {
                        if (tree.Add(command[1]))
                            Console.WriteLine(string.Format("Word '{0}' added successfully", command[1]));
                        else
                            Console.WriteLine(string.Format("Word '{0}' already exist", command[1]));
                    }
                }
                else if (command[0] == "search")
                {
                    if (tree == null)
                    {
                        Console.WriteLine(string.Format("Word '{0}' is not found", command[1]));
                    }
                    else
                    {
                        if (tree.Search(command[1]))
                            Console.WriteLine(string.Format("Word '{0}' found", command[1]));
                        else
                            Console.WriteLine(string.Format("Word '{0}' is not found", command[1]));
                    }
                }
                else if (command[0] == "print")
                {
                    if (tree == null)
                    {
                        Console.WriteLine("Empty Tree");
                    }
                    else
                    {
                        Console.WriteLine();
                        Console.WriteLine(tree.ToString());
                    }
                }
                else
                {
                    Console.WriteLine("Command not recognized");
                }

                Console.WriteLine();
                Console.Write("Input  : ");
                command = Console.ReadLine().Split(' ');
            }
        }
    }
}
