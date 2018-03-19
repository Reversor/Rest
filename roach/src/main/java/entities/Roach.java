package entities;

import java.util.Objects;
import javax.inject.Singleton;

@Singleton
public class Roach {

    private final String name;

    private byte fill;

    private Roach() {
        name = null;
    }

    public Roach(String name, byte fill) {
        this.name = name;
        this.fill = fill;
    }

    public String getName() {
        return name;
    }

    public byte getFill() {
        return fill;
    }

    public void setFill(byte fill) {
        this.fill = fill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Roach roach = (Roach) o;
        return fill == roach.fill &&
                Objects.equals(name, roach.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fill);
    }

    @Override
    public String toString() {
        return "Roach{" +
                "name='" + name + '\'' +
                ", fill=" + fill +
                '}';
    }
}
