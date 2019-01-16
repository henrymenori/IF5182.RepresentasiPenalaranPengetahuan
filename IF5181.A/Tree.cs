using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IF5181.A
{
    class Tree
    {
        #region Properties

        public bool Stop { get; set; }
        public char Data { get; set; }
        public Tree Left { get; set; }
        public Tree Right { get; set; }

        #endregion

        #region Constructor

        public Tree(char c)
        {
            Data = c;
            Right = null;
            Left = null;
            Stop = false;
        }

        public Tree(string s)
        {
            Data = s.First();
            Right = null;
            Left = s.Length > 1 ? new Tree(s.Substring(1)) : null;
            Stop = s.Length <= 1;
        }

        #endregion

        #region Public Methods

        public bool Add(string word)
        {
            var valid = true;

            if (word.First() < Data)
            {
                var stopTemp = Stop;
                var dataTemp = Data;
                var leftTemp = Left;
                var rightTemp = Right;

                Data = word.First();
                Stop = word.Length <= 1;
                Left = word.Length > 1 ? new Tree(word.Substring(1)) : null;
                Right = new Tree(dataTemp)
                {
                    Stop = stopTemp,
                    Left = leftTemp,
                    Right = rightTemp
                };
            }
            else if (word.First() > Data)
            {
                if (Right == null)
                    Right = new Tree(word);
                else
                    valid = Right.Add(word);
            }
            else
            {
                if (word.Length > 1)
                {
                    if (Left == null)
                        Left = new Tree(word.Substring(1));
                    else
                        valid = Left.Add(word.Substring(1));
                }
                else
                {
                    if (Stop)
                        valid = false;
                    else
                        Stop = true;
                }
            }

            return valid;
        }

        public bool Search(string word)
        {
            var valid = false;

            if (word.First() < Data)
            {

            }
            else if (word.First() > Data)
            {
                if (Right != null)
                    valid = Right.Search(word);
            }
            else
            {
                if (word.Length > 1)
                {
                    if (Left != null)
                        valid = Left.Search(word.Substring(1));
                }
                else
                {
                    valid = Stop;
                }
            }

            return valid;
        }

        public override string ToString()
        {
            return string.Join(Environment.NewLine, ToListString());
        }

        #endregion

        #region Private Methods

        private List<string> ToListString()
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

        #endregion
    }
}
