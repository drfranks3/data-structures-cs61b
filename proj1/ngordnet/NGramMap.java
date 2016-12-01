package ngordnet;
import java.util.Collection;
import java.util.HashMap;
import edu.princeton.cs.introcs.In;

public class NGramMap {
    private In wordsFile, countsFile;
    private HashMap<Integer, YearlyRecord> years = new HashMap<Integer, YearlyRecord>();
    private HashMap<String, TimeSeries<Integer>> wordT = new HashMap<String, TimeSeries<Integer>>();
    private TimeSeries<Long> yearTotals = new TimeSeries<Long>();

    /** Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME. */
    /** ngram TAB year TAB match_count TAB volume_count NEWLINE. */
    /** TimeSeries for word, YearlyRecord date */
    public NGramMap(String wordsFilename, String countsFilename) {
        wordsFile = new In(wordsFilename);
        countsFile = new In(countsFilename);
        String[] entry;

        while (wordsFile.hasNextLine()) {
            entry = wordsFile.readLine().split("\\t");
            String ngram = entry[0];
            int year = Integer.parseInt(entry[1]);
            int count = Integer.parseInt(entry[2]);

            YearlyRecord record = new YearlyRecord();
            TimeSeries<Integer> series = new TimeSeries<Integer>();

            if (years.containsKey(year)) {
                record = years.get(year);
            }

            if (wordT.containsKey(ngram)) {
                series = wordT.get(ngram);
            }

            record.put(ngram, count);
            series.put(year, count);

            years.put(year, record);
            wordT.put(ngram, series);
        }
        
        while (countsFile.hasNextLine()) {
            entry = countsFile.readLine().split(",");
            int year = Integer.parseInt(entry[0]);
            long count = Long.valueOf(entry[1]);

            yearTotals.put(year, count);
        }
    }
    
    /** Returns the absolute count of WORD in the given YEAR. If the word
      * did not appear in the given year, return 0. */
    public int countInYear(String word, int year) {
        try {
            return getRecord(year).count(word);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /** Returns a defensive copy of the YearlyRecord of YEAR. */
    public YearlyRecord getRecord(int year) {
        YearlyRecord clone = new YearlyRecord();
        YearlyRecord original = years.get(year);
        for (String s : original.words()) {
            clone.put(s, original.count(s));
        }
        return clone;
    }

    /** Returns the total number of words recorded in all volumes. */
    public TimeSeries<Long> totalCountHistory() {
        return yearTotals;
    }

    /** Provides the history of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Integer> countHistory(String word, int startYear, int endYear) {
        return new TimeSeries(countHistory(word), startYear, endYear);
    }

    /** Provides a defensive copy of the history of WORD. */
    public TimeSeries<Integer> countHistory(String word) {
        return new TimeSeries(wordT.get(word));
    }

    /** Provides the relative frequency of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> weightHistory(String word, int startYear, int endYear) {
        return new TimeSeries(weightHistory(word), startYear, endYear);
    }

    /** Provides the relative frequency of WORD. */
    public TimeSeries<Double> weightHistory(String word) {
        TimeSeries<Long> partition = new TimeSeries<Long>();
        TimeSeries<Integer> rec = countHistory(word);
        for (Number year : rec.years()) {
            partition.put(year.intValue(), yearTotals.get(year.intValue()));
        }
        return rec.dividedBy(partition);
    }

    /** Provides the summed relative frequency of all WORDS between
      * STARTYEAR and ENDYEAR. If a word does not exist, ignore it rather
      * than throwing an exception. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words, 
                              int startYear, int endYear) {
        return new TimeSeries(summedWeightHistory(words), startYear, endYear);
    }

    /** Returns the summed relative frequency of all WORDS. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words) {
        TimeSeries<Double> result = new TimeSeries<Double>();
        for (String s : words) {
            result = result.plus(weightHistory(s));
        }
        return result;
    }

    /** Provides processed history of all words between STARTYEAR and ENDYEAR as processed
      * by YRP. */
    public TimeSeries<Double> processedHistory(int startYear, int endYear,
                                               YearlyRecordProcessor yrp) {
        return new TimeSeries(processedHistory(yrp), startYear, endYear);
    }

    /** Provides processed history of all words ever as processed by YRP. */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        TimeSeries<Double> result = new TimeSeries<Double>();
        for (Integer i : years.keySet()) {
            result.put(i, yrp.process(years.get(i)));
        }
        return result;
    }
}
