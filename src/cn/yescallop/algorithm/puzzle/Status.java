package cn.yescallop.algorithm.puzzle;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author Scallop Ye
 */
class Status {

    private static int[] dx = {0, 1, 0, -1};
    private static int[] dy = {-1, 0, 1, 0};
    final int[][] matrix;
    final int size;
    int hash;
    int spaceX, spaceY;
    int g, h, f;
    int m;
    Status parent;

    Status(int[][] matrix, int hash, int spaceX, int spaceY, int g, int m, Status parent) {
        this.matrix = matrix;
        this.size = matrix.length;
        this.hash = hash;
        this.spaceX = spaceX;
        this.spaceY = spaceY;
        this.g = g;
        this.m = m;
        this.parent = parent;
    }

    public static Status of(int size, String s) {
        int[][] matrix = new int[size][size];
        int spaceX = -1, spaceY = -1;
        String[] a = s.split("\\n");
        for (int y = 0; y < a.length; y++) {
            String[] ap = a[y].split(",");
            for (int x = 0; x < ap.length; x++) {
                int n = Integer.parseInt(ap[x]);
                matrix[y][x] = n;
                if (n == 0) {
                    spaceX = x;
                    spaceY = y;
                }
            }
        }
        if (spaceX == -1)
            throw new IllegalArgumentException("No space");
        return new Status(matrix, deepHashCode(matrix), spaceX, spaceY, 0, -1, null);
    }

    public static Status init(int size) {
        int[][] matrix = new int[size][size];
        int tmp = 1;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                matrix[y][x] = tmp++;
            }
        }
        matrix[size - 1][size - 1] = 0;
        return new Status(matrix, 0, size - 1, size - 1, 0, -1, null);
    }

    public static Status generate(int size, int steps, Random random) {
        Status p = init(size);
        for (int i = 0; i < steps; i++) {
            if (!p.moveRandomly(random)) i--;
        }
        p.hash = deepHashCode(p.matrix);
        return p;
    }

    static int deepHashCode(int[][] a) {
        int result = 1;
        for (int[] element : a) {
            int elementHash = 1;
            for (int e : element)
                elementHash = elementHash * 31 + e;
            result = result * 31 + elementHash;
        }
        return result;
    }

    public boolean move(int m) {
        int x = spaceX + dx[m];
        int y = spaceY + dy[m];
        if (x != size && x != -1 && y != size && y != -1) {
            matrix[spaceY][spaceX] = matrix[y][x];
            matrix[y][x] = 0;
            spaceX = x;
            spaceY = y;
            return true;
        }
        return false;
    }

    public boolean moveRandomly(Random random) {
        return this.move(random.nextInt(4));
    }

    void estimateCost(Point[] index) {
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix.length; x++) {
                int n = matrix[y][x];
                if (n == 0)
                    continue;
                Point p = index[n];
                this.h += Math.abs(p.x - x) + Math.abs(p.y - y);
            }
        }
        this.h *= 5;
        this.f = this.g + this.h;
    }

    Status cloneWithMove(int m) {
        int x = spaceX + dx[m];
        int y = spaceY + dy[m];
        if (x != size && x != -1 && y != size && y != -1) {
            int n = matrix[y][x];
            matrix[spaceY][spaceX] = n;
            matrix[y][x] = 0;
            int[][] matrix = new int[size][size];
            for (int i = 0; i < size; i++) {
                System.arraycopy(this.matrix[i], 0, matrix[i], 0, size);
            }
            this.matrix[spaceY][spaceX] = 0;
            this.matrix[y][x] = n;
            return new Status(matrix, deepHashCode(matrix), x, y, g + 1, m, this);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.deepEquals(this.matrix, ((Status) obj).matrix);
    }

    @Override
    public String toString() {
        StringJoiner a = new StringJoiner("\n");
        for (int y = 0; y < size; y++) {
            StringJoiner b = new StringJoiner(",");
            for (int x = 0; x < size; x++) {
                b.add(Integer.toString(matrix[y][x]));
            }
            a.add(b.toString());
        }
        return a.toString();
    }
}
