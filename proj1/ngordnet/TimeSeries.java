package ngordnet;
import java.util.Collection;
import java.util.TreeMap;
import java.util.ArrayList;

public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {   

    /** Constructs a new empty TimeSeries. */
    public TimeSeries() {

    }

    /** Creates a copy of TS, but only between STARTYEAR and ENDYEAR. 
     * inclusive of both end points. */
    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        if (ts != null) {
            putAll((ts.headMap(endYear, true)).tailMap(startYear, true));
        }
    }

    /** Creates a copy of TS. */
    public TimeSeries(TimeSeries<T> ts) {
        if (ts != null) {
            putAll(ts);
        }
    }

    /** Returns either the sum of THIS and TS depending on the second parameter. */
    private TimeSeries<Double> manageYears(TimeSeries<? extends Number> ts, int sum) {
        TimeSeries<Double> result = new TimeSeries<Double>();

        Collection<Number> allKeys = years();
        allKeys.addAll(ts.years());

        for (Number k : allKeys) {
            double thisT = 0, thatT = 0;

            try {
                thisT = get(k).doubleValue();
            } catch (NullPointerException e) { /* Ignore NULLPOINTEREXCEPTION */ }
            try {
                thatT = ts.get(k).doubleValue();
            } catch (NullPointerException e) { /* Ignore NULLPOINTEREXCEPTION */ }

            if (sum == 1) {
                result.put(k.intValue(), thisT + thatT);
            } else {
                if (thatT == 0) {
                    throw new IllegalArgumentException("Heyyyy");
                } else {
                    result.put(k.intValue(), thisT / thatT);
                }
            }
        }
        return result;
    }

    /** Returns the quotient of this time series divided by the iiiįerelevant value in ts.
      * If ts is missing a key in this time series, return an IllegalArgumentException. */
    public TimeSeries<Double> dividedBy(TimeSeries<? extends Number> ts) {
        return manageYears(ts, 0);
    }

    /** Returns the sum of this time series with the given ts. The result is a 
      * a Double time series (for simplicity). */
    public TimeSeries<Double> plus(TimeSeries<? extends Number> ts) {
        return manageYears(ts, 1);
    }

    /** Returns all years for this time series (in any order). */
    public Collection<Number> years() {
        ArrayList<Number> result = new ArrayList<Number>();
        for (Integer k : keySet()) {
            result.add(k);
        }
        return result;
    }

    /** Returns all data for this time series. 
      * Must be in the same order as years(). */
    public Collection<Number> data()  {
        ArrayList<Number> result = new ArrayList<Number>();
        for (T k : values()) {
            result.add(k);
        }
        return result;
    }
}
