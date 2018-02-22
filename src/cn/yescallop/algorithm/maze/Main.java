package cn.yescallop.algorithm.maze;

import cn.yescallop.algorithm.util.PriorityHashQueue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Scallop Ye
 */
public class Main {

    private static int[] dx = {0, 1, 0, -1, -1, 1, 1, -1};
    private static int[] dy = {-1, 0, 1, 0, 1, -1, 1, -1};

    public static void main(String[] args) {
        int len;
        int[][] matrix;
        int destX = -1;
        int destY = -1;
        try {
            BufferedReader br = new BufferedReader(new FileReader("map.txt"));
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line.substring(1, line.length() - 1), ", ");
            len = st.countTokens();
            matrix = new int[len][len];
            for (int y = 0; y < len; y++) {
                if (y != 0) {
                    line = br.readLine();
                    st = new StringTokenizer(line.substring(1, line.length() - 1), ", ");
                }
                for (int x = 0; x < len; x++) {
                    int n = Integer.parseInt(st.nextToken());
                    matrix[y][x] = n;
                    if (n == 2) {
                        destX = x;
                        destY = y;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (destX == -1) {
            System.out.println("Destination not found!");
            System.exit(1);
        }

        int[] path = findPath(matrix, destX, destY);
        if (path == null) {
            System.out.println("Path not found!");
            System.exit(1);
        }

        for (int m : path) {
            System.out.print(m);
        }
        System.out.println();
    }

    private static int[] findPath(int[][] matrix, int destX, int destY) {
        PriorityHashQueue<Status> openQueue = new PriorityHashQueue<>(Comparator.comparingInt(s -> s.f));
        Set<Status> closedSet = new HashSet<>();

        Status initialStatus = new Status(0, 0, 0, -1, null);
        openQueue.add(initialStatus);

        while (openQueue.size() != 0) {
            Status cur = openQueue.poll();
            if (cur.x == destX && cur.y == destY) {
                return reconstructPath(cur);
            }
            closedSet.add(cur);

            for (int m = 0; m < 8; m++) {
                int x = cur.x + dx[m];
                int y = cur.y + dy[m];
                if (x == -1 || x == matrix.length || y == -1 || y == matrix.length || matrix[y][x] == 1)
                    continue;
                Status neighbor = new Status(x, y, cur.g + 1, m, cur);
                if (closedSet.contains(neighbor))
                    continue;
                int i = openQueue.indexOf(neighbor);
                if (i != -1) {
                    Status other = openQueue.get(i);
                    if (neighbor.g < other.g) {
                        other.parent = cur;
                        other.m = m;
                        other.g = neighbor.g;
                        other.f = other.g + other.h;
                        openQueue.siftUp(i, other);
                    }
                } else {
                    neighbor.estimateCost(destX, destY);
                    openQueue.add(neighbor);
                }
            }
        }
        return null;
    }

    private static int[] reconstructPath(Status s) {
        int[] path = new int[s.g];
        for (int i = s.g - 1; i >= 0; i--) {
            path[i] = s.m;
            s = s.parent;
        }
        return path;
    }
}