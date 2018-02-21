package cn.yescallop.algorithm.puzzle;

import cn.yescallop.algorithm.util.PriorityHashQueue;

import java.awt.*;
import java.util.*;

/**
 * @author Scallop Ye
 */
public class Main {

    public static void main(String[] args) {
        Status status = Status.of(5, "10,19,22,11,23\n" +
                "9,15,5,0,21\n" +
                "20,18,2,17,13\n" +
                "16,12,3,4,24\n" +
                "7,1,8,6,14");
        System.out.println(status);
        System.out.println();

        long start = System.currentTimeMillis();
        int[] path = findPath(status);
        System.out.println("Used time: " + (System.currentTimeMillis() - start) / 1000d + "s");
        if (path == null) {
            System.out.println("Path not found!");
            System.exit(1);
        }

        System.out.println("Path: ");
        for (int m : path) {
            System.out.print(m);
        }
        System.out.println();
    }

    private static int[] findPath(Status initialStatus) {
        PriorityHashQueue<Status> openQueue = new PriorityHashQueue<>(Comparator.comparingInt(s -> s.f));
        Set<Status> closedSet = new HashSet<>();

        Point[] index = createIndex(initialStatus.size);
        initialStatus.estimateCost(index);

        openQueue.add(initialStatus);

        while (openQueue.size() > 0) {
            Status cur = openQueue.poll();
            if (cur.h == 0) {
                System.out.println("Searched status: " + closedSet.size());
                return reconstructPath(cur);
            }
            closedSet.add(cur);

            for (int m = 0; m < 4; m++) {
                Status neighbor = cur.cloneWithMove(m);
                if (neighbor == null || closedSet.contains(neighbor))
                    continue;
                if (openQueue.contains(neighbor)) {
                    Status other = openQueue.get(neighbor);
                    if (neighbor.g < other.g) {
                        other.parent = cur;
                        other.g = neighbor.g;
                        other.m = m;
                        other.f = other.g + other.h;
                        openQueue.remove(other);
                        openQueue.add(other);
                    }
                } else {
                    neighbor.estimateCost(index);
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

    private static Point[] createIndex(int size) {
        Point[] res = new Point[size * size];
        res[0] = new Point(size - 1, size - 1);
        int i = 1;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (i == res.length) break;
                res[i++] = new Point(x, y);
            }
        }
        return res;
    }
}
