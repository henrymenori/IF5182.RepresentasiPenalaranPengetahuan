using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IF5181.A
{
    public class Node
    {
        #region Properties

        protected bool Stop { get; set; }
        protected char Data { get; set; }
        protected Node Left { get; set; }
        protected Node Right { get; set; }

        #endregion

        #region Constructor

        public Node() { }

        public Node(string s)
        {
            Stop = s.Length == 1;
            Data = s.First();
            Left = s.Length > 1 ? new Node(s.Substring(1)) : null;
            Right = null;
        }

        #endregion

        #region Public Methods

        public bool Add(string word)
        {
            if (word.First() < Data)
            {
                Right = Copy();
                Data = word.First();
                Stop = word.Length <= 1;
                Left = word.Length > 1 ? new Node(word.Substring(1)) : null;

                return true;
            }
            else if (word.First() > Data)
            {
                if (Right == null)
                {
                    Right = new Node(word);
                    return true;
                }
                else
                {
                    return Right.Add(word);
                }
            }
            else
            {
                if (word.Length > 1)
                {
                    if (Left == null)
                    {
                        Left = new Node(word.Substring(1));
                        return true;
                    }
                    else
                    {
                        return Left.Add(word.Substring(1));
                    }
                }
                else
                {
                    if (Stop)
                    {
                        return false;
                    }
                    else
                    {
                        Stop = true;
                        return true;
                    }
                }
            }
        }

        public bool Search(string word)
        {
            if (word.First() < Data)
            {
                return false;
            }
            else if (word.First() > Data)
            {
                if (Right == null)
                {
                    return false;
                }
                else
                {
                    return Right.Search(word);
                }
            }
            else
            {
                if (word.Length > 1)
                {
                    if (Left == null)
                    {
                        return false;
                    }
                    else
                    {
                        return Left.Search(word.Substring(1));
                    }
                }
                else
                {
                    return Stop;
                }
            }
        }

        public List<string> ToListString()
        {
            var list = new List<string> { string.Format("{0}{1}", Data, Stop ? '*' : ' ') };

            if (Left != null)
            {
                var leftList = Left.ToListString();
                list[0] = string.Format("{0} - {1}", list[0], leftList[0]);

                for (int i = 1; i < leftList.Count; i++)
                {
                    list.Add(string.Format("     {0}", leftList[i]));
                }
            }

            if (Right != null)
            {
                for (int i = 1; i < list.Count; i++)
                {
                    list[i] = string.Format("|{0}", list[i].Substring(1));
                }

                list.Add("|");
                list.AddRange(Right.ToListString());
            }

            return list;
        }

        public Node Copy()
        {
            return new Node()
            {
                Stop = Stop,
                Data = Data,
                Left = Left,
                Right = Right
            };
        }

        #endregion
    }
}
