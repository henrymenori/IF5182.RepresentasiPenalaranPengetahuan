#ifndef TREE_H_
#define TREE_H_

#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <stdio.h>

typedef struct tree
{
    char *data;
    int root;
    int curr;
    int size;
} tree;

tree *create_tree();
bool search_tree(tree *t, char *s);
void insert_tree(tree *t, char *s);
void load_tree(tree *t, char *filename);
void save_tree(tree *t, char *filename);
void print_memory_tree(tree *t);

#endif