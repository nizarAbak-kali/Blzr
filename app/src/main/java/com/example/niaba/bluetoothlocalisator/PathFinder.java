package com.example.niaba.bluetoothlocalisator;

/**
 * Created by niaba on 20/05/17.
 */

public class PathFinder {
    /* A utility function to find the vertex with minimum distance value,
      from the set of vertexes not yet included in shortest path tree */
    static final int V = 9;
    private static final int INT_MAX = Integer.MAX_VALUE;

    int minDistance(int dist[], boolean[] sptSet)
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for(int v = 0; v < V; v++)
        {
            if(sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }
        }
        return min_index;
    }

    // A utility function to print the constructed distance array
    void printSolution(int dist[], int n)
    {
        System.out.println("Vertex    Distance from Source");
        for(int i = 0; i < V; i++)
            System.out.println(i+" \t\t "+dist[i]);
    }

    /* Function that implements Dijkstra's single source shortest path
       algorithm for a graph represented using adjacency matrix
       representation */
    void dijkstra(int graph[][], int src)
    {
        int dist[] = new int[V];  /* The output array, dist[i] will hold
                                     the shortest distance from src to i */
        /* sptSet[i] will be true if vertex i is included in shortest
           path tree or shortest distance from src to i is finalized */
        boolean sptSet[] = new boolean[V];

        for(int i = 0; i < V; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        //Find shortest path for all vertexes
        for(int count = 0; count < V-1; count++)
        {
            /* Pick the minimum distance vertex from the set of vertexes
               not yet processed. u is always equal to src in first
               iteration. */
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            /* Update dist value of the adjacent vertexes of the
               picked vertex. */
            for(int v = 0; v < V; v++)
            {
                /* Update dist[v] only if it is not in sptSet, there is an
                   edge from u to v, and total weight of path from src to
                   v through u is smaller than current value of dist[v] */
                if (!sptSet[v] && graph[u][v] && dist[u] != INT_MAX
                        && dist[u]+graph[u][v] < dist[v])
                    dist[v] = dist[u] + graph[u][v];
            }
        }

        // print the constructed distance array
        printSolution(dist, V);
    }
}