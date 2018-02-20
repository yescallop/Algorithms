package cn.yescallop.puzzle;

import java.awt.*;
import java.util.*;

/**
 * @author Scallop Ye
 */
public class Main {

    public static void main(String[] args) {
        Status status = Status.of(5, "19,10,8,7,0\n" +
                "4,21,22,5,13\n" +
                "16,6,24,3,14\n" +
                "1,11,12,15,2\n" +
                "17,23,20,9,18");
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
            status.move(m);
            System.out.print(m);
        }
        System.out.println();
    }

    private static int[] findPath(Status initialStatus) {
        Queue<Status> openQueue = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Map<Status, Status> openMap = new HashMap<>();
        Map<Status, Status> closedMap = new HashMap<>();

        Point[] index = createIndex(initialStatus.size);
        initialStatus.estimateCost(index);

        openQueue.add(initialStatus);
        openMap.put(initialStatus, initialStatus);

        while (openQueue.size() > 0) {
            Status cur = openQueue.poll();
            if (cur.h == 0) {
                System.out.println("Searched status: " + closedMap.size());
                return reconstructPath(cur);
            }
            openMap.remove(cur);
            closedMap.put(cur, cur);

            for (int m = 0; m < 4; m++) {
                Status neighbor = cur.cloneWithMove(m);
                if (neighbor == null)
                    continue;
                if (openMap.containsKey(neighbor)) {
                    Status other = openMap.get(neighbor);
                    if (neighbor.g < other.g) {
                        other.parent = cur;
                        other.m = m;
                        other.g = neighbor.g;
                        other.f = other.g + other.h;
                    }
                } else if (closedMap.containsKey(neighbor)) {
                    Status other = closedMap.get(cur);
                    if (neighbor.g < other.g) {
                        other.parent = cur;
                        other.m = m;
                        other.g = neighbor.g;
                        other.f = other.g + other.h;
                    }
                } else {
                    neighbor.estimateCost(index);
                    openQueue.add(neighbor);
                    openMap.put(neighbor, neighbor);
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
