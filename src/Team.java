import java.util.*;

public abstract class Team<U extends Unit> implements Collection<U> {
    private List<U> units;

    public Team() {
    }

    public Team(List<U> units) {
        this.units = units;
    }

    public Team(Unit[] units) {
        this.units = (List<U>) Arrays.asList(units);
    }

    @Override
    public int size() {
        return units.size();
    }

    @Override
    public boolean isEmpty() {
        return units.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return units.contains(o);
    }

    @Override
    public Iterator<U> iterator() {
        return units.iterator();
    }

    @Override
    public Object[] toArray() {
        return units.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return units.toArray(a);
    }

    @Override
    public boolean add(U unit) {
        return units.add(unit);
    }

    @Override
    public boolean remove(Object o) {
        return units.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return units.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends U> c) {
        return units.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return units.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return units.retainAll(c);
    }

    @Override
    public void clear() {
        units.clear();
    }

    @Override
    public abstract String toString();

    public Unit[] getUnits() {
        return units.toArray(new Unit[0]);
    }

    public int[] getUnitIDs() {
        // get the IDs (indices) of the units starting with 1
        return General.intsInRange(1,units.size()+1);
    }

    public void regenerateTeamHP(double regenCoefficient){
        for( Unit u : units) {
            u.setCurrentHP((int) (u.getCurrentHP() + u.getHP()*regenCoefficient));
        }
    }

    public Unit[] getAliveUnits() {
        List<Unit> alive = new ArrayList<>();
        for (U unit : units) {
            if (unit.getCurrentHP() > 0) {
                alive.add(unit);
            }
        }
        Unit[] res = new Unit[alive.size()];
        res = alive.toArray(res);
        return res;
    }

    public Unit getRandomAliveUnit() {
        // returns random alive unit
        Unit[] alive = getAliveUnits();
        int id = new Random().nextInt(alive.length);
        return alive[id];
    }

    public void killTeam() {
        for (U unit : units) {
            unit.setCurrentHP(0);
        }
    }

    public void setUnits(Unit[] units) {
        this.units = (List<U>) Arrays.asList(units);
    }

    public boolean teamDead() {
        boolean res = true;
        for (U unit : units) {
            if (unit.getCurrentHP() > 0) {
                res = false;
            }
        }
        return res;
    }

    public int getHighestLevel() {
        // return the highest level in the team
        int res = -1;
        for (U unit : units) {
            if (unit.getLevel() > res) {
                res = unit.getLevel();
            }
        }
        return res;
    }

    public void setUnits(List<U> units) {
        this.units = units;
    }
}
