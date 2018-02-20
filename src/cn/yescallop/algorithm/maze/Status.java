package cn.yescallop.algorithm.maze;

/**
 * @author Scallop Ye
 */
class Status {

    final int x;
    final int y;
    final int hash;
    int g, h, f;
    int m;

    Status parent;

    Status(int x, int y, int g, int m, Status parent) {
        this.x = x;
        this.y = y;
        this.hash = x << 16 | y;
        this.g = g;
        this.m = m;
        this.parent = parent;
    }

    void estimateCost(int destX, int destY) {
        int dx = Math.abs(destX - x);
        int dy = Math.abs(destY - y);
        this.h = dx > dy ? dx : dy;
        this.f = this.g + this.h;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hash == ((Status) obj).hash;
    }
}
