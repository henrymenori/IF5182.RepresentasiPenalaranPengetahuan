#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "tree.h"

int main()
{
    char cmd, *arg;
    tree *t = create_tree();

    printf("\ncommand : ");
    scanf("%c", &cmd);

    while (cmd != 'x')
    {
        if (cmd == 'a')
        {
            scanf("%s", arg);
            insert_tree(t, arg);
            printf("Word '%s' added successfully\n", arg);
        }
        else if (cmd == 'q')
        {
            scanf("%s", arg);
            if (search_tree(t, arg))
            {
                printf("Word '%s' exist\n", arg);
            }
            else
            {
                printf("Word '%s' does not exist\n", arg);
            }
        }
        else if (cmd == 's')
        {
            scanf("%s", arg);
            save_tree(t, arg);
            printf("Tree successfully saved to %s\n", arg);
        }
        else if (cmd == 'l')
        {
            scanf("%s", arg);
            load_tree(t, arg);
            printf("Tree successfully loaded from %s\n", arg);
        }
        else if (cmd == 'm')
        {
            printf("\n");
            print_memory_tree(t);
        }
        else
        {
            printf("wrong command!\n");
        }

        getchar();
        printf("\ncommand : ");
        scanf("%c", &cmd);
    }

    return 0;
}