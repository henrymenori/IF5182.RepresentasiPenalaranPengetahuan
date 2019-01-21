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

        public Node Root { get; set; }

        #endregion

        #region Constructor

        public Tree()
        {
            Root = null;
        }

        #endregion

        #region Public Methods

        public bool Add(string word)
        {
            if (IsEmpty())
            {
                Root = new Node(word);
                return true;
            }
            else
            {
                return Root.Add(word);
            }
        }

        public bool IsEmpty()
        {
            return Root == null;
        }

        public bool Search(string word)
        {
            if (IsEmpty())
            {
                return false;
            }
            else
            {
                return Root.Search(word);
            }
        }

        public override string ToString()
        {
            return string.Join(Environment.NewLine, Root.ToListString());
        }

        #endregion
    }
}
