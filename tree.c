#include "tree.h"

// ==== Constant ====

#define INITIAL_BLOCK_COUNT 1000
#define BLOCK_BYTE_SIZE 10
#define MARK_OFFSET_INDEX 1
#define LEFT_OFFSET_INDEX 2
#define RIGHT_OFFSET_INDEX 6
#define NULL_INDEX -1

// ==== Initializer ====

tree *create_tree()
{
    tree *t = malloc(sizeof(tree));
    char *d = malloc(sizeof(char) * BLOCK_BYTE_SIZE * INITIAL_BLOCK_COUNT);

    t->data = d;
    t->root = NULL_INDEX;
    t->curr = 0;
    t->size = BLOCK_BYTE_SIZE * INITIAL_BLOCK_COUNT;

    return t;
}

// ==== Getter & Setter ====

char get_value_node(tree *t, int n_idx)
{
    return t->data[n_idx];
}

bool get_mark_node(tree *t, int n_idx)
{
    return t->data[n_idx + MARK_OFFSET_INDEX];
}

int get_left_node(tree *t, int n_idx)
{
    int l_idx;
    memcpy(&l_idx, &t->data[n_idx + LEFT_OFFSET_INDEX], sizeof(int));

    return l_idx;
}

int get_right_node(tree *t, int n_idx)
{
    int r_idx;
    memcpy(&r_idx, &t->data[n_idx + RIGHT_OFFSET_INDEX], sizeof(int));

    return r_idx;
}

void set_value_node(tree *t, int n_idx, char c)
{
    t->data[n_idx] = c;
}

void set_mark_node(tree *t, int n_idx, bool b)
{
    t->data[n_idx + MARK_OFFSET_INDEX] = b;
}

void set_left_node(tree *t, int n_idx, int l_idx)
{
    memcpy(&t->data[n_idx + LEFT_OFFSET_INDEX], &l_idx, sizeof(int));
}

void set_right_node(tree *t, int n_idx, int r_idx)
{
    memcpy(&t->data[n_idx + RIGHT_OFFSET_INDEX], &r_idx, sizeof(int));
}

// ==== Basic Function ====

bool is_empty_tree(tree *t)
{
    return t->curr == 0;
}

bool is_full_tree(tree *t)
{
    return !(t->curr < t->size);
}

int add_node(tree *t, char *s, int s_idx, int s_length)
{
    if (s_idx < s_length)
    {
        int n_idx = t->curr, l_idx;

        t->curr += BLOCK_BYTE_SIZE;
        l_idx = add_node(t, s, s_idx + 1, s_length);

        set_value_node(t, n_idx, s[s_idx]);
        set_mark_node(t, n_idx, s_length == s_idx + 1);
        set_left_node(t, n_idx, l_idx);
        set_right_node(t, n_idx, NULL_INDEX);

        return n_idx;
    }
    else
    {
        return NULL_INDEX;
    }
}

// ==== Helper Function ====

bool search_word(tree *t, char *s, int s_idx, int s_length, int n_idx)
{
    char c = get_value_node(t, n_idx);

    if (s_length - s_idx == 1)
    {
        return c == s[s_idx] && get_mark_node(t, n_idx);
    }
    else
    {
        int l_idx, r_idx;

        if (n_idx == NULL_INDEX || s[s_idx] < c)
        {
            return false;
        }
        else if (s[s_idx] > c)
        {
            r_idx = get_right_node(t, n_idx);
            return r_idx == NULL_INDEX ? false : search_word(t, s, s_idx, s_length, r_idx);
        }
        else
        {
            l_idx = get_left_node(t, n_idx);
            return l_idx == NULL_INDEX ? false : search_word(t, s, s_idx + 1, s_length, l_idx);
        }
    }
}

int insert_word(tree *t, char *s, int s_idx, int s_length, int n_idx)
{
    if (s_idx < s_length)
    {
        char c = get_value_node(t, n_idx);
        int l_idx, r_idx, t_idx;

        if (n_idx == NULL_INDEX || s[s_idx] < c)
        {
            t_idx = add_node(t, s, s_idx, s_length);
            set_right_node(t, t_idx, n_idx);

            return t_idx;
        }
        else if (s[s_idx] > c)
        {
            r_idx = get_right_node(t, n_idx);
            t_idx = r_idx == NULL_INDEX ? add_node(t, s, s_idx, s_length) : insert_word(t, s, s_idx, s_length, r_idx);
            set_right_node(t, n_idx, t_idx);

            return n_idx;
        }
        else
        {
            l_idx = get_left_node(t, n_idx);
            t_idx = l_idx == NULL_INDEX ? add_node(t, s, s_idx + 1, s_length) : insert_word(t, s, s_idx + 1, s_length, l_idx);
            set_left_node(t, n_idx, t_idx);

            if (s_length == s_idx + 1)
            {
                set_mark_node(t, n_idx, true);
            }

            return n_idx;
        }
    }
    else
    {
        return n_idx;
    }
}

// ==== Main Function ====

bool search_tree(tree *t, char *s)
{
    return search_word(t, s, 0, strlen(s), t->root);
}

void insert_tree(tree *t, char *s)
{
    t->root = insert_word(t, s, 0, strlen(s), t->root);
}

void load_tree(tree *t, char *filename)
{
    FILE *f;

    f = fopen(filename, "r");
    fread(&t->root, sizeof(int), 1, f);
    fread(&t->curr, sizeof(int), 1, f);
    fread(t->data, sizeof(char), t->curr, f);
}

void save_tree(tree *t, char *filename)
{
    FILE *f;

    f = fopen(filename, "w");
    fwrite(&t->root, sizeof(int), 1, f);
    fwrite(&t->curr, sizeof(int), 1, f);
    fwrite(t->data, sizeof(char), t->curr, f);
    fclose(f);
}

void print_memory_tree(tree *t)
{
    int l_idx, r_idx;

    printf("Root : %05d\n", t->root);
    printf("Curr : %05d\n\n", t->curr);
    printf(" index | v |   l   |   r   |\n");
    printf(" ------+---+-------+-------+\n");

    for (int i = 0; i < t->curr; i += BLOCK_BYTE_SIZE)
    {
        printf(" %05d |", i);
        printf(" %c |", get_value_node(t, i));

        l_idx = get_left_node(t, i);
        if (l_idx != NULL_INDEX)
        {
            printf(" %05d |", l_idx);
        }
        else
        {
            printf("       |");
        }

        r_idx = get_right_node(t, i);
        if (r_idx != NULL_INDEX)
        {
            printf(" %05d |", r_idx);
        }
        else
        {
            printf("       |");
        }

        printf(" %c\n", get_mark_node(t, i) ? '*' : ' ');
    }
}