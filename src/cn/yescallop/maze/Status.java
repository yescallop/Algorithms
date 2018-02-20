package cn.yescallop.maze;

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

    Status(int x, int y, int hash, int g, int m, Status parent) {
        this.x = x;
        this.y = y;
        this.hash = hash;
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
        return ((Status) obj).x == x && ((Status) obj).y == y;
    }
}
